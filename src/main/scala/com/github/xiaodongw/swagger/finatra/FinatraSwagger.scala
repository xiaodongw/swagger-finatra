package com.github.xiaodongw.swagger.finatra

import com.fasterxml.jackson.databind.{JavaType, ObjectMapper}
import com.google.inject.{Inject => GInject}
import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{FormParam, QueryParam, RouteParam, Header => HeaderParam}
import io.swagger.converter.{ModelConverter, ModelConverterContext, ModelConverters}
import io.swagger.jackson.ModelResolver
import io.swagger.models._
import io.swagger.models.parameters._
import io.swagger.models.properties.{Property, RefProperty}
import io.swagger.util.Json
import java.lang.annotation.Annotation
import java.lang.reflect.ParameterizedType
import java.util
import javax.inject.{Inject => JInject}
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.`type`.TypeDescription
import net.bytebuddy.description.modifier.Visibility
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

object FinatraSwagger {
  private val finatraRouteParamter = ":(\\w+)".r

  /**
   * Cache of dynamically generated class bodies keyed by qualified names
   */
  private val dynamicClassBodies: mutable.HashMap[String, Class[_]] = new mutable.HashMap[String, Class[_]]()

  implicit def convertToFinatraSwagger(swagger: Swagger): FinatraSwagger = new FinatraSwagger(swagger)
}

sealed trait ModelParam {
  val name: String
  val description: String
  val required: Boolean
  val typ: Class[_]
}

sealed trait FinatraRequestParam
case class RouteRequestParam(name: String, typ: Class[_], description: String = "", required: Boolean = true) extends FinatraRequestParam with ModelParam
case class QueryRequestParam(name: String, typ: Class[_], description: String = "", required: Boolean = true) extends FinatraRequestParam with ModelParam
case class BodyRequestParam(description: String = "", name: String, typ: Class[_], innerOptionType: Option[java.lang.reflect.Type] = None) extends FinatraRequestParam
case class RequestInjectRequestParam(name: String) extends FinatraRequestParam
case class HeaderRequestParam(name: String, required: Boolean = true, description: String = "", typ: Class[_]) extends FinatraRequestParam with ModelParam
case class FormRequestParam(name: String, description: String = "", required: Boolean = true, typ: Class[_]) extends FinatraRequestParam with ModelParam

object Resolvers {
  class ScalaOptionResolver(objectMapper: ObjectMapper) extends ModelResolver(objectMapper) {
    override def resolveProperty(
      propType: JavaType,
      context: ModelConverterContext,
      annotations: Array[Annotation],
      next: util.Iterator[ModelConverter]): Property = {
      if (propType.getRawClass == classOf[Option[_]]) {
        try {
          return super.resolveProperty(propType.containedType(0), context, annotations, next)
        } catch {
          case _: Exception =>
        }
      }

      super.resolveProperty(propType, context, annotations, next)
    }
  }

  def register(objectMapper: ObjectMapper = Json.mapper): Unit = {
    ModelConverters.getInstance().addConverter(new ScalaOptionResolver(objectMapper))
  }
}

class FinatraSwagger(swagger: Swagger) {

  import FinatraSwagger._

  /**
   * Register a request object that contains body information/route information/etc
   *
   * @tparam T
   * @return
   */
  def register[T: TypeTag]: List[Parameter] = {
    val properties = getFinatraProps[T]

    val className = currentMirror.runtimeClass(typeOf[T]).getName

    val swaggerProps =
      properties.collect {
        case x: ModelParam => x
      }.map {
        case param @ (x: RouteRequestParam) =>
          new PathParameter().
            name(param.name).
            description(param.description).
            required(param.required).
            property(registerModel(param.typ))
        case param @ (x: QueryRequestParam) =>
          new QueryParameter().
            name(param.name).
            description(param.description).
            required(param.required).
            property(registerModel(param.typ))
        case param @ (x: HeaderRequestParam) =>
          new HeaderParameter().
            name(param.name).
            description(param.description).
            required(param.required).
            property(registerModel(param.typ))
        case param @ (x: FormRequestParam) =>
          new FormParameter().
            name(param.name).
            description(param.description).
            required(param.required).
            property(registerModel(param.typ))
      }

    val bodyElements = properties.collect { case b: BodyRequestParam => b }

    swaggerProps ++ List(registerDynamicBody(bodyElements, className)).flatten
  }

  /**
   * Given the request object format its finatra parameters via reflection
   *
   * @tparam T
   * @return
   */
  private def getFinatraProps[T: TypeTag]: List[FinatraRequestParam] = {
    val clazz = currentMirror.runtimeClass(typeOf[T])

    val fields = clazz.getDeclaredFields

    val constructorArgWithField =
      clazz.
        getConstructors.
        head.getParameters.
        map(m => (clazz: Class[_ <: Annotation]) => {
          val annotation = m.getAnnotationsByType(clazz)

          if (annotation.isEmpty) {
            None
          } else {
            Some(annotation)
          }
        }).
        zip(fields)

    val ast: List[Option[FinatraRequestParam]] =
      constructorArgWithField.map { case (annotationExtractor, field) =>
        val routeParam = annotationExtractor(classOf[RouteParam])
        val queryParam = annotationExtractor(classOf[QueryParam])
        val injectJavax = annotationExtractor(classOf[JInject])
        val injectGuice = annotationExtractor(classOf[GInject])
        val header = annotationExtractor(classOf[HeaderParam])
        val form = annotationExtractor(classOf[FormParam])

        val (isRequired, innerOptionType) = field.getGenericType match {
          case parameterizedType: ParameterizedType =>

            val required = parameterizedType.getRawType.asInstanceOf[Class[_]] == classOf[Option[_]]

            (required, Some(parameterizedType.getActualTypeArguments.apply(0)))
          case _ =>
            (true, None)
        }

        if (routeParam.isDefined) {
          Some(RouteRequestParam(field.getName, typ = field.getType))
        }
        else if (queryParam.isDefined) {
          Some(QueryRequestParam(field.getName, typ = field.getType, required = isRequired))
        }
        else if ((injectJavax.isDefined || injectGuice.isDefined) && field.getType.isAssignableFrom(classOf[Request])) {
          Some(RequestInjectRequestParam(field.getName))
        }
        else if (header.isDefined) {
          Some(HeaderRequestParam(field.getName, typ = field.getType, required = isRequired))
        }
        else if (form.isDefined) {
          Some(FormRequestParam(field.getName, typ = field.getType, required = isRequired))
        }
        else {
          Some(BodyRequestParam(name = field.getName, typ = field.getType, innerOptionType = innerOptionType))
        }
      }.toList

    ast.flatten
  }

  private def emitBodyClassForElements(bodyElements: List[BodyRequestParam], className: String): Class[_] = {
    val byteBuddy = new ByteBuddy()

    // add "Body" to avoid name collisions
    val bodyEmittedClass = byteBuddy.subclass(classOf[Object]).name(className)

    val bodyFields = bodyElements.foldLeft(bodyEmittedClass) { (asm, body) =>
      // if we have an inner option type, unwrap the option
      // and pass it to the class builder so we can get proper
      // definitions of the inner type in the swagger model
      val bodyType = body.innerOptionType.getOrElse(body.typ).asInstanceOf[Class[_]]

      asm.defineField(body.name, new TypeDescription.Generic.OfNonGenericType.ForLoadedType(bodyType), Visibility.PUBLIC)
    }

    bodyFields.make().load(getClass.getClassLoader).getLoaded
  }

  /**
   * Creates a fake object for swagger to reflect upon
   *
   * @param bodyElements
   * @param name
   * @return
   */
  private def registerDynamicBody(bodyElements: List[BodyRequestParam], name: String): Option[Parameter] = {
    if (bodyElements.isEmpty) {
      return None
    }

    val className = name + "Body"

    val bodyClass = dynamicClassBodies.getOrElse(className, emitBodyClassForElements(bodyElements, className))

    dynamicClassBodies.put(className, bodyClass)

    val schema = registerModel(bodyClass, Some(name))

    val model = SchemaUtil.toModel(schema)

    Some(
      new BodyParameter().name("body").schema(model)
    )
  }

  def registerModel[T: TypeTag]: Property = {
    val paramType: Type = typeOf[T]
    if (paramType =:= TypeTag.Nothing.tpe) {
      null
    } else {
      val typeClass = currentMirror.runtimeClass(paramType)

      registerModel(typeClass)
    }
  }

  private def registerModel(typeClass: Class[_], name: Option[String] = None) = {
    val modelConverters = ModelConverters.getInstance()
    val models = modelConverters.readAll(typeClass)
    for (entry <- models.entrySet().asScala) {
      swagger.addDefinition(entry.getKey, entry.getValue)
    }
    val schema = modelConverters.readAsProperty(typeClass)

    schema
  }

  def convertPath(path: String): String = {
    FinatraSwagger.finatraRouteParamter.replaceAllIn(path, "{$1}")
  }

  def registerOperation(path: String, method: String, operation: Operation): Swagger = {
    val swaggerPath = convertPath(path)

    var spath = swagger.getPath(swaggerPath)
    if (spath == null) {
      spath = new Path()
      swagger.path(swaggerPath, spath)
    }

    spath.set(method, operation)

    swagger
  }
}

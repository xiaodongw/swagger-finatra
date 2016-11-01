package com.github.xiaodongw.swagger.finatra

import io.swagger.models.parameters._
import io.swagger.models.properties.{Property, RefProperty}
import io.swagger.models.{Operation, RefModel, Response, Swagger}
import io.swagger.util.Json

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.reflect.runtime.universe._

object FinatraOperation {
  implicit def convertToFinatraOperation(operation: Operation): FinatraOperation = new FinatraOperation(operation)
}

class FinatraOperation(operation: Operation) {
  import FinatraSwagger._

  def routeParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                            (implicit swagger: Swagger): Operation = {
    val param = new PathParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerModel[T])

    operation.parameter(param)

    operation
  }

  def queryParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                            (implicit swagger: Swagger): Operation = {
    val param = new QueryParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerModel[T])

    operation.parameter(param)

    operation
  }

  def headerParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                             (implicit swagger: Swagger): Operation = {
    val param = new HeaderParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerModel[T])

    operation.parameter(param)

    operation
  }

  def formParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                           (implicit swagger: Swagger): Operation = {
    val param = new FormParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerModel[T])

    operation.parameter(param)

    operation
  }

  def cookieParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                             (implicit swagger: Swagger): Operation = {
    val param = new CookieParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerModel[T])

    operation.parameter(param)

    operation
  }

  def bodyParam[T: TypeTag](name: String, description: String = "", example: Option[T] = None)
                           (implicit swagger: Swagger): Operation = {
    val schema = swagger.registerModel[T]

    val param = new BodyParameter()
      .name(name)
      .description(description)
      .schema(registerExample(example, schema))

    operation.parameter(param)

    operation
  }

  private def registerExample[T: TypeTag](example: Option[T], schema: Property) = {
    val model = schema match {
      case null => null
      case p: RefProperty => new RefModel(p.getSimpleRef)
      case _ => null //todo map ArrayProperty to ArrayModel?
    }

    example.foreach { e =>
      if (model != null) {
        model.setExample(Json.mapper.writeValueAsString(e))
      }
    }
    model
  }

  def responseWith[T: TypeTag](status: Int, description: String = "", example: Option[T] = None)
                              (implicit swagger: Swagger): Operation = {
    val schema = swagger.registerModel[T]

    registerExample(example, schema)

    val param = new Response()
      .description(description)
      .schema(schema)

    operation.response(status, param)

    operation
  }

  def addSecurity(name: String, scopes: List[String]): Operation = {
    operation.addSecurity(name, scopes.asJava)

    operation
  }

  def tags(tags: List[String]): Operation = {
    operation.setTags(tags.asJava)
    operation
  }
}

package com.github.xiaodongw.swagger.finatra

import io.swagger.models.parameters._
import io.swagger.models.properties.RefProperty
import io.swagger.models.{Swagger, Operation, RefModel, Response}
import io.swagger.util.Json

import scala.collection.JavaConverters._
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

    val model = schema match {
      case null => null
      case p: RefProperty => new RefModel(p.getSimpleRef)
      case _ => null  //todo map ArrayProperty to ArrayModel?
    }

    //todo not working
    example.foreach { e =>
      if(model != null) {
        model.setExample(Json.mapper.writeValueAsString(e))
      }
    }

    val param = new BodyParameter()
      .name(name)
      .description(description)
      .schema(model)

    operation.parameter(param)

    operation
  }

  def responseWith[T: TypeTag](status: Int, description: String = "", example: Option[T] = None)
                          (implicit finatraSwagger: Swagger): Operation = {
    val ref = finatraSwagger.registerModel[T]

    //todo not working, sample is not in the generated api, waiting for swagger fix
    example.foreach { e =>
      if(ref != null) {
        val example = Json.mapper.writeValueAsString(e)

        ref.setExample(example)
        //val model = api.swagger.getDefinitions.get(ref.asInstanceOf[RefProperty].getSimpleRef)
        //model.setExample(example)
      }
    }

    val param = new Response()
      .description(description)
      .schema(ref)

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

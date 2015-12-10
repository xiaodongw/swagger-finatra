package com.github.xiaodongw.swagger.finatra

import io.swagger.models.properties.RefProperty
import io.swagger.models.{Response, RefModel, Operation}
import io.swagger.models.parameters._
import io.swagger.util.Json
import scala.collection.JavaConverters._

import scala.reflect.runtime.universe._

class OperationWrap(finatraSwagger: FinatraSwagger) {
  private[finatra] val operation = new Operation

  def summary(value: String): Unit = {
    operation.summary(value)
  }

  def tags(values: String*): Unit = {
    operation.setTags(values.asJava)
  }

  def routeParam[T: TypeTag](name: String, description: String = "", required: Boolean = true): Unit = {
    val param = new PathParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(finatraSwagger.registerModel[T])

    operation.parameter(param)
  }

  def queryParam[T: TypeTag](name: String, description: String = "", required: Boolean = true): Unit = {
    val param = new QueryParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(finatraSwagger.registerModel[T])

    operation.parameter(param)
  }

  def headerParam[T: TypeTag](name: String, description: String = "", required: Boolean = true): Unit = {
    val param = new HeaderParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(finatraSwagger.registerModel[T])

    operation.parameter(param)
  }

  def formParam[T: TypeTag](name: String, description: String = "", required: Boolean = true): Unit = {
    val param = new FormParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(finatraSwagger.registerModel[T])

    operation.parameter(param)
  }

  def cookieParam[T: TypeTag](name: String, description: String = "", required: Boolean = true): Unit = {
    val param = new CookieParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(finatraSwagger.registerModel[T])

    operation.parameter(param)
  }

  def bodyParam[T: TypeTag](name: String, description: String = "", example: Option[T] = None): Unit = {
    val schema = finatraSwagger.registerModel[T]

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
  }

  def response[T: TypeTag](status: Int, description: String = "", example: Option[T] = None): Unit = {
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
  }

  def description(value: String): Unit = {
    operation.setDescription(value)
  }

  def consumes(values: String*): Unit = {
    operation.setConsumes(values.asJava)
  }

  def produces(values: String*): Unit = {
    operation.setProduces(values.asJava)
  }

  def deprecated(value: Boolean): Unit = {
    operation.deprecated(value)
  }
}

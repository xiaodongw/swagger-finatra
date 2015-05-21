package com.github.xiaodongw.swagger.finatra

import com.wordnik.swagger.models.properties.RefProperty
import com.wordnik.swagger.models.{Response, RefModel, Operation}
import com.wordnik.swagger.models.parameters._
import com.wordnik.swagger.util.Json
import scala.collection.JavaConverters._

import scala.reflect.runtime.universe._

class OperationWrap {
  private[finatra] val operation = new Operation
  private[this] val swagger = FinatraSwagger

  def summary(value: String): Unit = {
    operation.summary(value)
  }

  def tags(values: String*): Unit = {
    operation.setTags(values.asJava)
  }

  def routeParam[T: TypeTag](name: String, description: String = ""): Unit = {
    val param = new PathParameter()
      .name(name)
      .description(description)
      .property(swagger.registerModel[T])

    operation.parameter(param)
  }

  def queryParam[T: TypeTag](name: String, description: String = "", required: Boolean = false): Unit = {
    val param = new QueryParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerModel[T])

    operation.parameter(param)
  }

  def headerParam[T: TypeTag](name: String, description: String = ""): Unit = {
    val param = new HeaderParameter()
      .name(name)
      .description(description)
      .property(swagger.registerModel[T])

    operation.parameter(param)
  }

  def formParam[T: TypeTag](name: String, description: String = ""): Unit = {
    val param = new FormParameter()
      .name(name)
      .description(description)
      .property(swagger.registerModel[T])

    operation.parameter(param)
  }

  def cookieParam[T: TypeTag](name: String, description: String = ""): Unit = {
    val param = new CookieParameter()
      .name(name)
      .description(description)
      .property(swagger.registerModel[T])

    operation.parameter(param)
  }

  def bodyParam[T: TypeTag](name: String, description: String = "", example: Option[T] = None): Unit = {
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
  }

  def response[T: TypeTag](status: Int, description: String = "", example: Option[T] = None): Unit = {
    val ref = swagger.registerModel[T]

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
}

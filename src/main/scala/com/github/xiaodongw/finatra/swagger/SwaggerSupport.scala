package com.github.xiaodongw.finatra.swagger

import com.twitter.finatra.{Controller, Request, ResponseBuilder}
import com.twitter.util.Future
import com.wordnik.swagger.models.parameters.{BodyParameter, PathParameter, QueryParameter}
import com.wordnik.swagger.models.properties.RefProperty
import com.wordnik.swagger.models.{Operation, RefModel, Response}
import com.wordnik.swagger.util.Json

import scala.reflect.runtime.universe._
import scala.util.DynamicVariable
import scala.collection.JavaConverters._

trait SwaggerSupport {
  self: Controller =>
  private[this] val api = FinatraSwagger

  private def addOperation(path: String, method: String, operation: Operation): Unit = {
    api.registerOperation(path, method, operation)
  }

  def post(path: String, doc: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "post", doc)
    self.post(path)(callback)
  }

  def get(path: String, doc: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "get", doc)
    self.get(path)(callback)
  }

  def put(path: String, doc: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "put", doc)
    self.put(path)(callback)
  }

  def patch(path: String, doc: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "patch", doc)
    self.patch(path)(callback)
  }

  def delete(path: String, doc: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "delete", doc)
    self.delete(path)(callback)
  }

  def head(path: String, doc: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "head", doc)
    self.head(path)(callback)
  }

  def options(path: String, doc: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "options", doc)
    self.options(path)(callback)
  }

  private val currentOperation = new DynamicVariable[Operation](null)

  protected def swagger(f: => Unit): Operation = {
    val op = new Operation
    currentOperation.withValue(op) {
      f
    }

    op
  }

  protected def summary(value: String): Unit = {
    currentOperation.value.summary(value)
  }

  protected def tags(values: String*): Unit = {
    currentOperation.value.setTags(values.asJava)
  }

  protected def routeParam[T: TypeTag](name: String, description: String = ""): Unit = {
    val param = new PathParameter()
      .name(name)
      .description(description)
      .property(api.registerModel[T])

    currentOperation.value.parameter(param)
  }

  protected def queryParam[T: TypeTag](name: String, description: String = "", required: Boolean = false): Unit = {
    val param = new QueryParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(api.registerModel[T])

    currentOperation.value.parameter(param)
  }

  protected def bodyParam[T: TypeTag](name: String, description: String = "", example: Option[T] = None): Unit = {
    val schema = api.registerModel[T]
    val model = if(schema == null) null else new RefModel(schema.asInstanceOf[RefProperty].getSimpleRef)

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

    currentOperation.value.parameter(param)
  }

  protected def response[T: TypeTag](status: Int, description: String = "", example: Option[T] = None): Unit = {
    val ref = api.registerModel[T]

    //todo not working
    example.foreach { e =>
      if(ref != null) {
        ref.setExample(Json.mapper.writeValueAsString(e))
      }
    }

    val param = new Response()
      .description(description)
      .schema(api.registerModel[T])

    currentOperation.value.response(status, param)
  }
}

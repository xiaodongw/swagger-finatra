package com.github.finatra.swagger

import java.io.{PrintWriter, File}

import com.twitter.finatra.{ResponseBuilder, Request, Controller}
import com.twitter.util.Future
import com.wordnik.swagger.core.util.JsonSerializer
import com.wordnik.swagger.model._

case class OperationWithPath(path: String, operation: Operation)

case class OperationDoc(summary: String, notes: String, params: Seq[DocParam]) {
  def responseClass: Class[_] = classOf[String]
}

trait DocParam
case class RouteParam[T](name: String, summary: String) extends DocParam
case class QueryParam[T](name: String, summary: String) extends DocParam
case class ResponseParam[T](status: Int, summary: String) extends DocParam

trait SwaggerSupport {
  self: Controller =>

  private def addOperation(path: String, method: String, doc: OperationDoc): Unit = {
    val operation = Operation(
      method = method,
      summary = doc.summary,
      notes = doc.notes,
      responseClass = doc.responseClass.getCanonicalName,
      nickname = "",
      position = 0)

    //todo convert parameters
    //todo convert responses

    ApiRegiester.register(path, operation)
  }

  def post(path: String, doc: OperationDoc)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "POST", doc)
    self.post(path)(callback)
  }

  def get(path: String, doc: OperationDoc)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "GET", doc)
    self.get(path)(callback)
  }

  def put(path: String, doc: OperationDoc)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "PUT", doc)
    self.put(path)(callback)
  }

  def patch(path: String, doc: OperationDoc)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "PATCH", doc)
    self.patch(path)(callback)
  }

  def delete(path: String, doc: OperationDoc)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "DELETE", doc)
    self.delete(path)(callback)
  }

  def head(path: String, doc: OperationDoc)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "HEAD", doc)
    self.head(path)(callback)
  }

  def options(path: String, doc: OperationDoc)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    addOperation(path, "OPTIONS", doc)
    self.options(path)(callback)
  }

  protected def swagger(summary: String, notes: String, params: DocParam*): OperationDoc = {
    OperationDoc(
      summary = summary,
      notes = notes,
      params = params
    )
  }

  protected def response[T](status: Int, summary: String): ResponseParam[T] = ResponseParam[T](status, summary)
  protected def routeParam[T](name: String, summary: String): RouteParam[T] = RouteParam[T](name, summary)
  protected def queryParam[T](name: String, summary: String): QueryParam[T] = QueryParam[T](name, summary)

  private def write(file: File, apis: ApiListing): Unit = {
    val json = JsonSerializer.asJson(apis)
    val pw = new PrintWriter(file)
    pw.write(json)
    pw.close()
  }

  private def write(file: File, resources: ResourceListing): Unit = {
    val json = JsonSerializer.asJson(resources)
    val pw = new PrintWriter(file)
    pw.write(json)
    pw.close()
  }
}

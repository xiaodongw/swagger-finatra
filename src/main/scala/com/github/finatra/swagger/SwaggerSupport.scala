package com.github.finatra.swagger

import java.io.{PrintWriter, File}

import com.twitter.finatra.{ResponseBuilder, Request, Controller}
import com.twitter.util.Future
import com.wordnik.swagger.core.util.JsonSerializer
import com.wordnik.swagger.model._

import scala.collection.mutable

case class OperationWithPath(path: String, operation: Operation)

trait SwaggerSupport {
  self: Controller =>
  protected val operations = mutable.ArrayBuffer.empty[OperationWithPath]

  protected def registerDoc(path: String, method: String, doc: RouteDoc[_]): Unit = {
    val operation = Operation(method = method, summary = doc.summary, notes = doc.notes,
      responseClass = doc.responseClass.getCanonicalName,
      nickname = "", position = 0)
    operations += OperationWithPath(path, operation)
  }

  def post(path: String, doc: RouteDoc[_])(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerDoc(path, "POST", doc)
    self.post(path)(callback)
  }

  def get(path: String, doc: RouteDoc[_])(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerDoc(path, "GET", doc)
    self.get(path)(callback)
  }

  protected def generateDocuments(): Unit = {
    val apis = operations.groupBy(_.path).map { case (path, ops) =>
      ApiDescription(path, None, operations = ops.map(_.operation).toList)
    }

    val apiListing = ApiListing("1.0", "1.2", "/", "/", apis = apis.toList)

    new File("./docs").mkdirs()
    write(new File("./docs/api1"), apiListing)
  }

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

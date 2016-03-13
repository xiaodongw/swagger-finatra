package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.{Controller, Request, ResponseBuilder}
import com.twitter.util.Future
import io.swagger.models.Operation

trait SwaggerSupport extends SwaggerSupportBase {
  self: Controller =>

  def post(path: String, op: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerOperation(path, "post", op)
    self.post(path)(callback)
  }

  def get(path: String, op: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerOperation(path, "get", op)
    self.get(path)(callback)
  }

  def put(path: String, op: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerOperation(path, "put", op)
    self.put(path)(callback)
  }

  def patch(path: String, op: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerOperation(path, "patch", op)
    self.patch(path)(callback)
  }

  def delete(path: String, op: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerOperation(path, "delete", op)
    self.delete(path)(callback)
  }

  def head(path: String, op: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerOperation(path, "head", op)
    self.head(path)(callback)
  }

  def options(path: String, op: Operation)(callback: (Request) => Future[ResponseBuilder]): Unit = {
    registerOperation(path, "options", op)
    self.options(path)(callback)
  }
}

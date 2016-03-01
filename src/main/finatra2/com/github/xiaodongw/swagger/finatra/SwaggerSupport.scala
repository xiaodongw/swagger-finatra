package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.http.Controller
import io.swagger.models.Operation

trait SwaggerSupport extends SwaggerSupportBase {
  self: Controller =>

  def post[RequestType: Manifest, ResponseType: Manifest](path: String, op: Operation)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "post", op)
    self.post(path)(callback)
  }

  def get[RequestType: Manifest, ResponseType: Manifest](path: String, op: Operation)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "get", op)
    self.get(path)(callback)
  }

  def put[RequestType: Manifest, ResponseType: Manifest](path: String, op: Operation)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "put", op)
    self.put(path)(callback)
  }

  def patch[RequestType: Manifest, ResponseType: Manifest](path: String, op: Operation)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "patch", op)
    self.patch(path)(callback)
  }

  def delete[RequestType: Manifest, ResponseType: Manifest](path: String, op: Operation)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "delete", op)
    self.delete(path)(callback)
  }

  def head[RequestType: Manifest, ResponseType: Manifest](path: String, op: Operation)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "head", op)
    self.head(path)(callback)
  }

  def options[RequestType: Manifest, ResponseType: Manifest](path: String, op: Operation)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "options", op)
    self.options(path)(callback)
  }
}

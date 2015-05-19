package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.http.Controller

trait SwaggerSupport extends SwaggerSupportBase {
  self: Controller =>

  def post[RequestType: Manifest, ResponseType: Manifest](path: String, doc: OperationWrap)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "post", doc)
    self.post(path)(callback)
  }

  def get[RequestType: Manifest, ResponseType: Manifest](path: String, doc: OperationWrap)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "get", doc)
    self.get(path)(callback)
  }

  def put[RequestType: Manifest, ResponseType: Manifest](path: String, doc: OperationWrap)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "put", doc)
    self.put(path)(callback)
  }

  def patch[RequestType: Manifest, ResponseType: Manifest](path: String, doc: OperationWrap)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "patch", doc)
    self.patch(path)(callback)
  }

  def delete[RequestType: Manifest, ResponseType: Manifest](path: String, doc: OperationWrap)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "delete", doc)
    self.delete(path)(callback)
  }

  def head[RequestType: Manifest, ResponseType: Manifest](path: String, doc: OperationWrap)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "head", doc)
    self.head(path)(callback)
  }

  def options[RequestType: Manifest, ResponseType: Manifest](path: String, doc: OperationWrap)(callback: RequestType => ResponseType): Unit = {
    registerOperation(path, "options", doc)
    self.options(path)(callback)
  }
}

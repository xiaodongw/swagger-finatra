package com.github.xiaodongw.swagger.finatra

import io.swagger.models.{Swagger, Operation}

trait SwaggerSupportBase {
  implicit protected val swagger: Swagger

  implicit protected val convertToFinatraOperation = FinatraOperation.convertToFinatraOperation _
  implicit protected val convertToFinatraSwagger = FinatraSwagger.convertToFinatraSwagger _

  protected def registerOperation(path: String, method: String, operation: Operation): Unit = {
    swagger.registerOperation(path, method, operation)
  }

  protected def swagger(f: Operation => Unit): Operation = {
    val op = new Operation

    f(op)

    op
  }
}

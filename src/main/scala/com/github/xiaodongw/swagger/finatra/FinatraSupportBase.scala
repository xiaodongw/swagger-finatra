package com.github.xiaodongw.swagger.finatra

import io.swagger.models.Operation

trait SwaggerSupportBase {
  implicit protected val finatraSwagger: FinatraSwagger

  protected def registerOperation(path: String, method: String, operation: Operation): Unit = {
    finatraSwagger.registerOperation(path, method, operation)
  }

  protected def swagger(f: Operation => Unit): Operation = {
    val op = new Operation

    f(op)

    op
  }
}

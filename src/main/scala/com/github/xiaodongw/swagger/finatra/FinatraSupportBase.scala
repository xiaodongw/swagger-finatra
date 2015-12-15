package com.github.xiaodongw.swagger.finatra

trait SwaggerSupportBase {
  val finatraSwagger: FinatraSwagger

  protected def registerOperation(path: String, method: String, op: OperationWrap): Unit = {
    finatraSwagger.registerOperation(path, method, op.operation)
  }

  protected def swagger(f: OperationWrap => Unit): OperationWrap = {
    val op = new OperationWrap(finatraSwagger)

    f(op)

    op
  }


}

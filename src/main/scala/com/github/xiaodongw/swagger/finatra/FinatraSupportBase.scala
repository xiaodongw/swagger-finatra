package com.github.xiaodongw.swagger.finatra

trait SwaggerSupportBase {
  val finatraSwager: FinatraSwagger

  protected def registerOperation(path: String, method: String, op: OperationWrap): Unit = {
    finatraSwager.registerOperation(path, method, op.operation)
  }

  protected def swagger(f: OperationWrap => Unit): OperationWrap = {
    val op = new OperationWrap(finatraSwager)

    f(op)

    op
  }


}

package com.github.xiaodongw.swagger.finatra

trait SwaggerSupportBase {
  protected def registerOperation(path: String, method: String, op: OperationWrap): Unit = {
    FinatraSwagger.registerOperation(path, method, op.operation)
  }

  protected def swagger(f: OperationWrap => Unit): OperationWrap = {
    val op = new OperationWrap

    f(op)

    op
  }


}

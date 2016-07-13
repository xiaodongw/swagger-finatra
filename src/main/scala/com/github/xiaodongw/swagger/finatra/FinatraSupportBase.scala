package com.github.xiaodongw.swagger.finatra

import io.swagger.models.Swagger

trait SwaggerSupportBase {
  implicit protected val swagger: Swagger

  implicit protected val convertToFinatraOperation = FinatraOperation.convertToFinatraOperation _
  implicit protected val convertToFinatraSwagger = FinatraSwagger.convertToFinatraSwagger _
}

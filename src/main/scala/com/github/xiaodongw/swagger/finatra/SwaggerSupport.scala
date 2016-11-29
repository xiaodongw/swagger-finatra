package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.http.{Controller, SwaggerRouteDSL}

trait SwaggerSupport extends SwaggerRouteDSL {
  self: Controller =>
  override protected val dsl = self

  implicit protected val convertToFinatraOperation = FinatraOperation.convertToFinatraOperation _
  implicit protected val convertToFinatraSwagger = FinatraSwagger.convertToFinatraSwagger _
  implicit protected val convertToSwaggerRouteDSL = SwaggerRouteDSL.convertToSwaggerRouteDSL _
}

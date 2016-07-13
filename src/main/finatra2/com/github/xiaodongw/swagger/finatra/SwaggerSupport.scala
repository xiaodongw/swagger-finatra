package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.http.{Controller, SwaggerRouteDSL}

trait SwaggerSupport extends SwaggerRouteDSL with SwaggerSupportBase {
  self: Controller =>
  override protected val dsl = self

  protected implicit val convertToSwaggerRouteDSL = SwaggerRouteDSL.convertToSwaggerRouteDSL _
}

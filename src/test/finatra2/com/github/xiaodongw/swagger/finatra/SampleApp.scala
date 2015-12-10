package com.github.xiaodongw.swagger.finatra

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import io.swagger.util.Json

object SampleSwagger extends FinatraSwagger {
  Json.mapper().setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy)
}

object SampleApp extends HttpServer {
  SampleSwagger.registerInfo(
    description = "The Student / Course management API, this is a sample for swagger document generation",
    version = "1.0.1",
    title = "Student / Course Management API")

  override def configureHttp(router: HttpRouter) {
    router
      .add(new SwaggerController(finatraSwagger = SampleSwagger))
      .add[SampleController]
      .filter[CommonFilters]
  }
}

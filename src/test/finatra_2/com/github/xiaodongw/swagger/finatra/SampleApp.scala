package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter

object SampleApp extends HttpServer {
  FinatraSwagger.registerInfo(
    description = "The Student / Course management API, this is a sample for swagger document generation",
    version = "1.0.1",
    title = "Student / Course Management API")

  override def configureHttp(router: HttpRouter) {
    router
      .add(new SwaggerController)
      .add[SampleController]
  }
}

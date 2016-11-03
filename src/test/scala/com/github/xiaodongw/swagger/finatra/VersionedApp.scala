package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.{Info, Swagger}

object VersionedSwagger extends Swagger

object VersionedApp extends HttpServer {
  val info = new Info()
              .description("The Student / Course management API, this is a sample for swagger document generation")
              .version("1.0.1")
              .title("Student / Course Management API")

  VersionedSwagger
    .info(info)
    .basePath("/v1")
    .addSecurityDefinition("sampleBasic", {
      val d = new BasicAuthDefinition()
      d.setType("basic")
      d
    })


  override def configureHttp(router: HttpRouter) {
    router
      .filter[CommonFilters]
      .add(new WebjarsController("swagger-ui", "2.2.6"))
      .add(new SwaggerController(swagger = VersionedSwagger))
      .add(new SampleController(VersionedSwagger))
  }
}

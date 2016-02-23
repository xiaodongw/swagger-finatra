package com.github.xiaodongw.swagger.finatra

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import io.swagger.models.{ExternalDocs, Tag}
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.util.Json

object SampleSwagger extends FinatraSwagger {
  Json.mapper().setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy)
}

object SampleApp extends HttpServer {
  SampleSwagger
    .registerInfo(
      description = "The Student / Course management API, this is a sample for swagger document generation",
      version = "1.0.1",
      title = "Student / Course Management API")
    .addSecurityDefinition("sampleBasic", {
      val d = new BasicAuthDefinition()
      d.setType("basic")
      d
    })
    .addTag(new Tag()
      .name("Student")
      .description("Everything about student")
      .externalDocs(new ExternalDocs()
        .url("http://example.com/examle")
        .description("this is external doc")
      )
    )
    .setExternalDocs({
      val d = new ExternalDocs()
      d.setDescription("This is external document")
      d.setUrl("https://github.com/Mekajiki/swagger-finatra")
      d
    })


  override def configureHttp(router: HttpRouter) {
    router
      .filter[CommonFilters]
      .add(new SwaggerController(finatraSwagger = SampleSwagger))
      .add[SampleController]
  }
}

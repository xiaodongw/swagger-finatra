package com.github.xiaodongw.swagger.finatra

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.twitter.finatra.FinatraServer
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.{Info, Swagger}
import io.swagger.util.Json

object SampleSwagger extends Swagger {
  Json.mapper().setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy)
}

object SampleApp extends FinatraServer {
  val info = new Info()
    .description("The Student / Course management API, this is a sample for swagger document generation")
    .version("1.0.1")
    .title("Student / Course Management API")
  SampleSwagger
    .info(info)
    .addSecurityDefinition("sampleBasic", {
      val d = new BasicAuthDefinition()
      d.setType("basic")
      d
    })

  register(new SwaggerController(swagger = SampleSwagger))
  register(new SampleController)
}

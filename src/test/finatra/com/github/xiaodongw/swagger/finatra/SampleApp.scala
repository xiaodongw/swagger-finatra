package com.github.xiaodongw.swagger.finatra

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.twitter.finatra.FinatraServer
import io.swagger.util.Json

object SampleSwagger extends FinatraSwagger {
  Json.mapper().setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy)
}

object SampleApp extends FinatraServer {
  SampleSwagger.registerInfo(
    description = "The Student / Course management API, this is a sample for swagger document generation",
    version = "1.0.1",
    title = "Student / Course Management API")

  register(new SwaggerController(finatraSwagger = SampleSwagger))
  register(new SampleController)
}

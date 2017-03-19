package com.github.xiaodongw.swagger.finatra

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.response.Mustache
import io.swagger.models.Swagger
import io.swagger.util.Json

class SwaggerController(docPath: String = "/api-docs", swagger: Swagger) extends Controller {
  get(s"${docPath}/model") { request: Request =>
    response.ok.body(Json.mapper.writeValueAsString(swagger))
      .contentType("application/json").toFuture
  }

  get(s"${docPath}/ui") { request: Request =>
    response.temporaryRedirect
        .location(s"/webjars/swagger-ui/2.2.8/index.html?url=${docPath}/model")
  }
}

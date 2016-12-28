package com.github.xiaodongw.swagger.finatra

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.response.Mustache
import io.swagger.models.Swagger
import io.swagger.util.Json

@Mustache("index")
case class SwaggerView(title: String, path: String)

class SwaggerController(root: String = "/api-docs", modelUri: String = "/model", uiUri: String = "/ui", webJarUri: String = "/webjars", swagger: Swagger) extends Controller {

  import com.twitter.finatra.http.SwaggerPaths._

  private val apiSpecUri = s"${swagger.base}$root$modelUri"

  get(apiSpecUri) { request: Request =>
    response.ok.body(Json.mapper.writeValueAsString(swagger))
      .contentType("application/json").toFuture
  }

  get(s"${swagger.base}$root$uiUri") { request: Request =>
    response.temporaryRedirect
            .location(s"$webJarUri/index.html?url=$apiSpecUri")
  }
}

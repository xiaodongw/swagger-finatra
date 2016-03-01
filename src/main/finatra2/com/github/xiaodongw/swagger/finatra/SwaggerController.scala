package com.github.xiaodongw.swagger.finatra

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.response.Mustache
import io.swagger.models.Swagger
import io.swagger.util.Json

@Mustache("index")
case class SwaggerView(title: String, path: String)

class SwaggerController(docPath: String = "/api-docs", swagger: Swagger) extends Controller {
  get(docPath) { request: Request =>
    response.ok.body(Json.mapper.writeValueAsString(swagger))
      .contentType("application/json").toFuture
  }

  get(s"${docPath}/ui") { request: Request =>
    val view = SwaggerView(swagger.getInfo.getTitle, docPath)
    response.ok.view("swagger-ui/index.mustache", view).toFuture
  }

  get(s"${docPath}/ui/:*") { request: Request =>
    val res = request.getParam("*")

    response.ok.file(s"public/swagger-ui/${res}")
  }
}

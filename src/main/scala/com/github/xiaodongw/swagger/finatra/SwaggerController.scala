package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra.Controller
import com.wordnik.swagger.util.Json

class SwaggerController(docPath: String = "/api-docs") extends Controller {
  get(docPath) { request =>
    val swagger = FinatraSwagger.swagger

    render.body(Json.mapper.writeValueAsString(swagger))
      .contentType("application/json").toFuture
  }

  get(docPath + "/ui") { request =>
    redirect(docPath + "/ui/index.html").toFuture
  }

  get(docPath + "/ui/*") { request =>
    val res = request.path.replace(docPath + "/ui/", "")

    render.static(s"swagger-ui/${res}").toFuture
  }
}

package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra._
import io.swagger.models.Swagger
import io.swagger.util.Json
import org.apache.commons.io.IOUtils

case class SwaggerView(title: String, path: String) extends View {
  override def template: String = "templates/swagger-ui/index.mustache"
}

class SwaggerController(docPath: String = "/api-docs", swagger: Swagger) extends Controller {
  get(docPath) { request =>
    render.body(Json.mapper.writeValueAsString(swagger))
      .contentType("application/json").toFuture
  }

  get(s"${docPath}/ui") { request =>
    render.view(SwaggerView(swagger.getInfo.getTitle, docPath)).toFuture
  }

  get(s"${docPath}/ui/*") { request =>
    val res = request.path.replace(docPath + "/ui/", "")

    resource(s"/public/swagger-ui/${res}").toFuture
  }

  /*
  render.static cannot load resource file when in non-production mode
  so use this method to work around this issue
   */
  private def resource(path: String): ResponseBuilder = {
    val stream  = getClass.getResourceAsStream(path)

    try {
      val bytes   = IOUtils.toByteArray(stream)
      stream.read(bytes)
      val mtype = FileService.extMap.getContentType('.' + path.split('.').last)

      render.status(200)
        .header("Content-Type", mtype)
        .body(bytes)
    } finally {
      IOUtils.closeQuietly(stream)
    }
  }
}

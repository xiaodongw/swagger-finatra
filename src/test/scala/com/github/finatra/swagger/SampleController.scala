package com.github.finatra.swagger

import com.twitter.finatra.Controller

class SampleController extends Controller with SwaggerSupport {
  case class HelloResponse(text: String)

  get("/hello", RouteDoc[HelloResponse]("The hello summary", "The hello notes")) { request =>
    render.json(HelloResponse("hello")).toFuture
  }

  get("/test", RouteDoc[Unit]("", "")) { request =>
    render.ok.toFuture
  }

  generateDocuments()
}

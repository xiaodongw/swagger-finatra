package com.github.finatra.swagger

import com.twitter.finatra.Controller

class SampleController extends Controller with SwaggerSupport {
  case class HelloResponse(text: String)

  get("/hello/:name",
    swagger("The hello summary",
      notes = "The hello notes",
      routeParam[String]("name", "the name"),
      queryParam[Int]("age", "the age"),
      response[HelloResponse](200, "The invocation is success"),
      response(401, "")
    )) { request =>
    val name = request.routeParams("name")
    val age = request.params.getOrElse("age", "20").toInt

    render.json(HelloResponse(s"hello ${name}, your age is ${age}")).toFuture
  }
}

package com.github.finatra.swagger

import com.twitter.finatra.Controller
import com.wordnik.swagger.config.ConfigFactory
import com.wordnik.swagger.core.util.JsonSerializer
import com.wordnik.swagger.model.{ApiListingReference, ResourceListing, SwaggerJsonSchemaSerializers}
import org.json4s.jackson.Serialization.write

class SwaggerController(docPath: String = "/api/docs", apiVersion: String = "1.2") extends Controller {


  get(docPath) { request =>
    val docs = ApiRegiester.generateDocuments(apiVersion)

    val config = ConfigFactory.config
    val references = docs.map { api =>
      ApiListingReference(api.resourcePath, api.description)
    }

    val resourceListing = ResourceListing(config.apiVersion,
      config.swaggerVersion,
      references
    )

    render.body(JsonSerializer.asJson(resourceListing))
      .contentType("application/json").toFuture
  }

  get(docPath + "/:resource") { request =>
    val resource = request.routeParams("resource")

    val apiListing = ApiRegiester.generateDocuments(apiVersion).head
    render.body(JsonSerializer.asJson(apiListing))
      .contentType("application/json").toFuture
  }

  get(docPath + "/ui/*") { request =>
    val res = request.path.replace(docPath + "/ui/", "")

    render.static(s"swagger-ui/${res}").toFuture
  }
}

package com.github.finatra.swagger

import com.wordnik.swagger.config.ConfigFactory

import scala.collection.mutable

import com.wordnik.swagger.model.{ApiListing, ApiDescription, Operation}

object ApiRegiester {
  private val apis = mutable.Map.empty[String, mutable.Buffer[Operation]]

  def register(path: String, operation: Operation): Unit = {
    val operations = apis.getOrElseUpdate(path, mutable.Buffer.empty[Operation])

    operations += operation
  }

  def generateDocuments(apiVersion: String): List[ApiListing] = {
    val ads = apis.map { case (path, operations) =>
      ApiDescription(path, None, operations.toList)
    }

    val config = ConfigFactory.config
    val apiListing = ApiListing(apiVersion, config.swaggerVersion, "/aa", "/aa", apis = ads.toList)

    //new File("./docs").mkdirs()
    //write(new File("./docs/api1"), apiListing)
    List(apiListing)
  }
}

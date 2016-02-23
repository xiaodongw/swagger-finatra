package com.github.xiaodongw.swagger.finatra

import io.swagger.converter.ModelConverters
import io.swagger.models.auth.SecuritySchemeDefinition
import io.swagger.models.properties.Property
import io.swagger.models._

import scala.collection.JavaConverters._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

class FinatraSwagger() {
  private[this] val _swagger = {
    val swagger = new Swagger

    //default info
    val info = new Info()
      .description("Description")
      .version("Version")
      .title("Title")

    swagger.info(info)

    swagger
  }

  def registerModel[T: TypeTag]: Property = {
    val paramType: Type = typeOf[T]
    if(paramType =:= TypeTag.Nothing.tpe) {
      null
    } else {
      val typeClass = currentMirror.runtimeClass(paramType)

      val modelConverters = ModelConverters.getInstance()
      val models = modelConverters.readAll(typeClass)
      for(entry <- models.entrySet().asScala) {
        _swagger.addDefinition(entry.getKey, entry.getValue)
      }
      val schema = modelConverters.readAsProperty(typeClass)

      schema
    }
  }

  def addSecurityDefinition(name: String, sd: SecuritySchemeDefinition): FinatraSwagger = {
    swagger.addSecurityDefinition(name, sd)
    this
  }

  def setExternalDocs(externalDocs: ExternalDocs): FinatraSwagger = {
    swagger.setExternalDocs(externalDocs)
    this
  }

  def addTag(tag: Tag) = {
    swagger.addTag(tag)
    this
  }

  private[this] val finatraRouteParamter = ":(\\w+)".r
  def convertPath(path: String): String = {
    finatraRouteParamter.replaceAllIn(path, "{$1}")
  }

  def registerOperation(path: String, method: String, operation: Operation): FinatraSwagger = {
    val swaggerPath = convertPath(path)

    var spath = _swagger.getPath(swaggerPath)
    if(spath == null) {
      spath = new Path()
      _swagger.path(swaggerPath, spath)
    }

    spath.set(method, operation)
    this
  }

  def registerInfo(description: String, version: String, title: String): FinatraSwagger = {
    val info = new Info()
      .description(description)
      .version(version)
      .title(title)

    _swagger.info(info)
    this
  }

  //use it to modify something not available on API
  def swagger = _swagger
}

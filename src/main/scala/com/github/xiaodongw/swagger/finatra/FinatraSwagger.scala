package com.github.xiaodongw.swagger.finatra

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.wordnik.swagger.converter.ModelConverters
import com.wordnik.swagger.models.properties.Property
import com.wordnik.swagger.models.{Info, Path, Swagger, Operation}
import com.wordnik.swagger.util.Json

import scala.reflect.runtime.universe._
import scala.reflect.runtime._
import scala.collection.JavaConverters._

object FinatraSwagger {
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
  Json.mapper.registerModule(DefaultScalaModule)

  def registerModel[T: TypeTag]: Property = {
    val paramType: Type = typeOf[T]
    if(paramType =:= TypeTag.Nothing.tpe) {
      null
    } else {
      val typeClass = currentMirror.runtimeClass(paramType)

      val modelConverter = ModelConverters.getInstance()
      val models = modelConverter.readAll(typeClass)
      for(entry <- models.entrySet().asScala) {
        _swagger.addDefinition(entry.getKey, entry.getValue)
      }
      val schema = modelConverter.readAsProperty(typeClass)

      schema
    }
  }


  private[this] val finatraRouteParamter = ":(\\w+)".r
  def convertPath(path: String): String = {
    finatraRouteParamter.replaceAllIn(path, "{$1}")
  }

  def registerOperation(path: String, method: String, operation: Operation): Unit = {
    val swaggerPath = convertPath(path)

    var spath = _swagger.getPath(swaggerPath)
    if(spath == null) {
      spath = new Path()
      _swagger.path(swaggerPath, spath)
    }

    spath.set(method, operation)
  }

  def registerInfo(description: String, version: String, title: String): Unit = {
    val info = new Info()
      .description(description)
      .version(version)
      .title(title)

    _swagger.info(info)
  }

  //use it to modify something not available on API
  def swagger = _swagger
}

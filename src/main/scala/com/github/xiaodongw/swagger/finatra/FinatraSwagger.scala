package com.github.xiaodongw.swagger.finatra

import io.swagger.converter.ModelConverters
import io.swagger.models.properties.Property
import io.swagger.models.{Operation, Path, Swagger}

import scala.collection.JavaConverters._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

object FinatraSwagger {
  private val finatraRouteParamter = ":(\\w+)".r

  implicit def convertToFinatraSwagger(swagger: Swagger): FinatraSwagger = new FinatraSwagger(swagger)
}

class FinatraSwagger(swagger: Swagger) {

  def registerModel[T: TypeTag]: Property = {
    val paramType: Type = typeOf[T]
    if(paramType =:= TypeTag.Nothing.tpe) {
      null
    } else {
      val typeClass = currentMirror.runtimeClass(paramType)

      val modelConverters = ModelConverters.getInstance()
      val models = modelConverters.readAll(typeClass)
      for(entry <- models.entrySet().asScala) {
        swagger.addDefinition(entry.getKey, entry.getValue)
      }
      val schema = modelConverters.readAsProperty(typeClass)

      schema
    }
  }

  def convertPath(path: String): String = {
    FinatraSwagger.finatraRouteParamter.replaceAllIn(path, "{$1}")
  }

  def registerOperation(path: String, method: String, operation: Operation): Swagger = {
    val swaggerPath = convertPath(path)

    var spath = swagger.getPath(swaggerPath)
    if(spath == null) {
      spath = new Path()
      swagger.path(swaggerPath, spath)
    }

    spath.set(method, operation)

    swagger
  }
}

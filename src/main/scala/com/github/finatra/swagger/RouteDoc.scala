package com.github.finatra.swagger

import com.wordnik.swagger.model.Parameter

import scala.reflect.ClassTag

case class RouteDoc[T](summary: String, notes: String)(implicit classTag: ClassTag[T]) {
  def responseClass: Class[_] = classTag.runtimeClass

  def parameter[P](name: String, description: Option[String])(implicit classTag: ClassTag[P]): Unit = {
    Parameter(name, description, dataType = classTag.runtimeClass.getCanonicalName, required = true, allowMultiple = false,
    paramType = classTag.runtimeClass.getCanonicalName)
  }

  def response[R](): Unit = {

  }
}

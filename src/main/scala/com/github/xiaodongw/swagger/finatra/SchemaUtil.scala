package com.github.xiaodongw.swagger.finatra

import io.swagger.models.{ArrayModel, Model, RefModel}
import io.swagger.models.properties.{ArrayProperty, Property, RefProperty}

object SchemaUtil {
  def toModel(schema: Property): Model = {
    val model = schema match {
      case null => null
      case p: RefProperty => new RefModel(p.getSimpleRef)
      case p: ArrayProperty => {
        val arrayModel = new ArrayModel()
        arrayModel.setItems(p.getItems)
        arrayModel
      }
      case _ => null
    }
    model
  }
}

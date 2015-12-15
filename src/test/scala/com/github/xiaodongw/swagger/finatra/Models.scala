package com.github.xiaodongw.swagger.finatra

import io.swagger.annotations.{ApiModelProperty, ApiModel}
import org.joda.time.{LocalDate, DateTime}

@ApiModel(value="AddressModel", description="Sample address model for documentation")
case class Address(street: String, zip: String)

case class Student(firstName: String, lastName: String, gender: Gender, birthday: LocalDate, grade: Int, address: Option[Address])

object CourseType extends Enumeration {
  val LEC, LAB = Value
}

case class Course(time: DateTime,
                  name: String,
                  @ApiModelProperty(required = false, example = "[math,stem]")
                  tags: Seq[String],
                  @ApiModelProperty(dataType = "string", allowableValues = "LEC,LAB")
                  typ: CourseType.Value,
                  @ApiModelProperty(readOnly = true)
                  capacity: Int,
                  @ApiModelProperty(dataType = "double", required = true)
                  cost: BigDecimal)

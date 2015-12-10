package com.github.xiaodongw.swagger.finatra

import io.swagger.annotations.{ApiModelProperty, ApiModel}
import org.joda.time.{LocalDate, DateTime}

@ApiModel(value="AddressModel", description="Sample address model for documentation")
case class Address(street: String, zip: String)

case class Student(firstName: String, lastName: String, gender: Gender, birthday: LocalDate, grade: Int, address: Option[Address])

//todo scala enumeration is not supported
object CourseType extends Enumeration {
  val LEC, LAB = Value
}

case class Course(time: DateTime,
                  name: String,
                  tags: Seq[String],
                  @ApiModelProperty(dataType = "string", allowableValues = "LEC,LAB") typ: CourseType.Value,
                  capacity: Int,
                  @ApiModelProperty(dataType = "double") cost: BigDecimal)

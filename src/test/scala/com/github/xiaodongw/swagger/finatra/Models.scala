package com.github.xiaodongw.swagger.finatra

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import javax.inject.Inject
import org.joda.time.{DateTime, LocalDate}

@ApiModel(value="AddressModel", description="Sample address model for documentation")
case class Address(street: String, zip: String)

case class Student(firstName: String, lastName: String, gender: Gender, birthday: LocalDate, grade: Int, address: Option[Address])

case class StudentWithRoute(
  @RouteParam id: String,
  @Inject request: Request,
  firstName: String,
  lastName: String,
  gender: Gender,
  birthday: LocalDate,
  grade: Int,
  emails: Array[String],
  address: Option[Address]
)

case class StringWithRequest(
  @Inject request: Request,
  firstName: String
)

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

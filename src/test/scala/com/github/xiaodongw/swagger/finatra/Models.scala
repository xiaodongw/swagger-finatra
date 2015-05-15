package com.github.xiaodongw.swagger.finatra

import org.joda.time.{LocalDate, DateTime}

case class Address(street: String, zip: String)

case class Student(name: String, gender: Gender, birthday: LocalDate, grade: Int, address: Address)

//todo scala enumeration is not supported
object CourseType extends Enumeration {
  val LEC, LAB = Value
}

case class Course(time: DateTime, name: String, tag: String, typ: CourseType.Value, capacity: Int, cost: BigDecimal)
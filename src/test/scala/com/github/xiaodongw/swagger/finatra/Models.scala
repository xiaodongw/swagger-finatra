package com.github.xiaodongw.swagger.finatra

case class Address(street: String, zip: String)

case class Student(name: String, gender: Gender, grade: Int, address: Address)

case class Course(name: String, tag: String, capacity: Int)
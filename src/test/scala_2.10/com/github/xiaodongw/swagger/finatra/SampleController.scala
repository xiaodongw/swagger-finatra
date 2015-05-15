package com.github.xiaodongw.swagger.finatra

import java.util.Date

import com.twitter.finatra.Controller
import org.joda.time.{DateTime, LocalDate}

class SampleController extends Controller with SwaggerSupport {
  case class HelloResponse(text: String, time: Date)

  get("/students/:id",
    swagger {
      summary("Read the detail information about the student")
      tags("Student")
      routeParam[String]("id", "the student id")
      response[Student](200, "the student object",
        example = Some(Student("Tom", Gender.Male, new LocalDate(), 4, Address("California Street", "94111"))))
      response(404, "the student is not found")
    }) { request =>
    val id = request.routeParams("id")

    render.json(Student("Alice", Gender.Female, new LocalDate(), 4, Address("California Street", "94111"))).toFuture
  }

  post("/students",
    swagger {
      summary("Create a new student")
      tags("Student")
      bodyParam[Student]("student", "the student details")
      response(200, "the student is created")
      response(500, "internal error")
    }) { request =>
    val student = request.contentString

    render.ok.toFuture
  }

  put("/students/:id",
    swagger {
      summary("Update the student")
      tags("Student")
      formParam[String]("name", "the student name")
      formParam[Int]("grade", "the student grade")
      routeParam[String]("id", "student ID")
      cookieParam[String]("who", "who make the update")
      headerParam[String]("token", "the token")
      response(200, "the student is updated")
      response(404, "the student is not found")
    }) { request =>
    val id = request.routeParams("id")
    val name = request.getParam("name")
    val grade = request.getIntParam("grade")
    val who = request.cookies.getOrElse("who", "Sam")  //todo swagger-ui not set the cookie?
    val token = request.headerMap("token")

    render.ok.toFuture
  }

  get("/students",
    swagger {
      summary("Get a list of students")
      tags("Student")
      response[Array[String]](200, "the student ids")
      response(500, "internal error")
    }) { request =>
    render.json(Array("student1", "student2")).toFuture
  }

  get("/courses",
    swagger {
      summary("Get a list of courses")
      tags("Course")
      response[Array[String]](200, "the courses ids")
      response(500, "internal error")
    }) { request =>
    render.json(Array("course1", "course2")).toFuture
  }

  get("/courses/:id",
    swagger {
      summary("Get the detail of a course")
      tags("Course")
      routeParam[String]("id", "the course id")
      response[Course](200, "the courses detail")
      response(500, "internal error")
    }) { request =>
    render.json(Course(new DateTime(), "calculation", "math", CourseType.LAB, 20, BigDecimal(300.54))).toFuture
  }

  get("/courses/:courseId/student/:studentId",
    swagger {
      summary("Is the student in this course")
      tags("Course", "Student")
      routeParam[String]("courseId", "the course id")
      routeParam[String]("studentId", "the student id")
      response[Boolean](200, "true / false")
      response(500, "internal error")
    }) { request =>
    render.json(true).toFuture
  }
}

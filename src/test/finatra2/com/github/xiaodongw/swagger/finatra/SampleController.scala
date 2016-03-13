package com.github.xiaodongw.swagger.finatra

import java.util.Date

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import org.joda.time.{DateTime, LocalDate}

class SampleController extends Controller with SwaggerSupport {
  override implicit protected val swagger = SampleSwagger

  case class HelloResponse(text: String, time: Date)

  get("/students/:id",
    swagger { o =>
      o.summary("Read student information")
        .description("Read the detail information about the student.")
        .tag("Student")
        .routeParam[String]("id", "the student id")
        .produces("application/json")
        .responseWith[Student](200, "the student object",
          example = Some(Student("Tom", "Wang", Gender.Male, new LocalDate(), 4, Some(Address("California Street", "94111")))))
        .responseWith[Unit](404, "the student is not found")
    }) { request: Request =>
    val id = request.getParam("id")

    response.ok.json(Student("Alice", "Wang", Gender.Female, new LocalDate(), 4, Some(Address("California Street", "94111")))).toFuture
  }

  post("/students",
    swagger { o =>
      o.summary("Create a new student")
        .tag("Student")
        .bodyParam[Student]("student", "the student details")
        .responseWith[Unit](200, "the student is created")
        .responseWith[Unit](500, "internal error")
    }) { student: Student =>
    //val student = request.contentString
    response.ok.json(student).toFuture
  }

  put("/students/:id",
    swagger { o =>
      o.summary("Update the student")
        .tag("Student")
        .formParam[String]("name", "the student name")
        .formParam[Int]("grade", "the student grade")
        .routeParam[String]("id", "student ID")
        .cookieParam[String]("who", "who make the update")
        .headerParam[String]("token", "the token")
        .responseWith[Unit](200, "the student is updated")
        .responseWith[Unit](404, "the student is not found")
    }) { request: Request =>
    val id = request.getParam("id")
    val name = request.getParam("name")
    val grade = request.getIntParam("grade")
    val who = request.cookies.getOrElse("who", "Sam")  //todo swagger-ui not set the cookie?
    val token = request.headerMap("token")

    response.ok.toFuture
  }

  get("/students",
    swagger { o =>
      o.summary("Get a list of students")
        .tag("Student")
        .responseWith[Array[String]](200, "the student ids")
        .responseWith[Unit](500, "internal error")
        .addSecurity("sampleBasic", List())
    }) { request: Request =>
    response.ok.json(Array("student1", "student2")).toFuture
  }

  get("/courses",
    swagger { o =>
      o.summary("Get a list of courses")
        .tag("Course")
        .responseWith[Array[String]](200, "the courses ids")
        .responseWith[Unit](500, "internal error")
    }) { request: Request =>
    response.ok.json(Array("course1", "course2")).toFuture
  }

  get("/courses/:id",
    swagger { o =>
      o.summary("Get the detail of a course")
        .tag("Course")
        .routeParam[String]("id", "the course id")
        .responseWith[Course](200, "the courses detail")
        .responseWith[Unit](500, "internal error")
    }) { request: Request =>
    response.ok.json(Course(new DateTime(), "calculation", Seq("math"), CourseType.LAB, 20, BigDecimal(300.54))).toFuture
  }

  get("/courses/:courseId/student/:studentId",
    swagger { o =>
      o.summary("Is the student in this course")
        .tags(List("Course", "Student"))
        .routeParam[String]("courseId", "the course id")
        .routeParam[String]("studentId", "the student id")
        .responseWith[Boolean](200, "true / false")
        .responseWith[Unit](500, "internal error")
        .deprecated(true)
    }) { request: Request =>
    response.ok.json(true).toFuture
  }
}

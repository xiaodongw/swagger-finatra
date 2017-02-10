# swagger-finatra
Add Swagger support for Finatra (1.6 and 2.2.0) web framework.

It requires Java 8 from version 0.6.0.

# Getting started
## Gradle
#### Add repository

	repositories {
	  maven { url "https://oss.sonatype.org/content/repositories/releases/" }
	}

#### Add Dependency

##### Scala 2.10, Finatra 2.2.0

	compile "com.github.xiaodongw:swagger-finatra_2.10:0.7.2"

##### Scala 2.11, Finatra 2.2.0

	compile "com.github.xiaodongw:swagger-finatra_2.11:0.7.2"

## SBT
	resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"

#### Add Dependency

##### Finatra 2.2.0

    libraryDependencies += "com.github.xiaodongw" %% "swagger-finatra" % "0.7.2"

## Add document information for you controller
    object SampleSwagger extends Swagger

    class SampleController extends Controller with SwaggerSupport {
      implicit protected val swagger = SampleSwagger

      getWithDoc("/students/:id") { o =>
        o.summary("Read the detail information about the student")
          .tag("Student")
          .routeParam[String]("id", "the student id")
          .responseWith[Student](200, "the student details")
          .responseWith(404, "the student is not found")
      } { request =>
        ...
      }

## Add document controller

##### Finatra 2.2.0
    object SampleApp extends HttpServer {
      val info = new Info()
        .description("The Student / Course management API, this is a sample for swagger document generation")
        .version("1.0.1")
        .title("Student / Course Management API")
      SampleSwagger.info(info)

      override def configureHttp(router: HttpRouter) {
        router
          .add[WebjarsController]
          .add(new SwaggerController(swagger = SampleSwagger))
          ...
      }
    }
Swagger API document: ```http://localhost:8888/api-docs/model```

Swagger UI: ```http://localhost:8888/api-docs/ui```

# Finatra 1.6
Previous version of Finatra (1.6) is also supported, Check [here](finatra1.md) for the guide.

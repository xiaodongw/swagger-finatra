# swagger-finatra
Add Swagger support for Finatra web framework.

# Getting started
## Gradle
#### Add repository

	repositories {
	  maven { url "https://oss.sonatype.org/content/repositories/releases/" }
	}

#### Add Dependency

##### Scala 2.10, Finatra 1.6.0

	compile "com.github.xiaodongw:swagger-finatra_2.10:0.5.1"

## SBT
	resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"

#### Add Dependency

##### Finatra 1.6.0

	libraryDependencies += "com.github.xiaodongw" %% "swagger-finatra" % "0.5.1"

## Add document information for you controller
    object SampleSwagger extends Swagger

    class SampleController extends Controller with SwaggerSupport {
      implicit protected val swagger = SampleSwagger

      get("/students/:id",
        swagger { o =>
          o.summary("Read the detail information about the student")
            .tag("Student")
            .routeParam[String]("id", "the student id")
            .responseWith[Student](200, "the student details")
            .responseWith(404, "the student is not found")
        }) { request =>
        ...
      }

## Add document controller

##### Finatra 1.6.0

    object SampleApp extends FinatraServer {
      val info = new Info()
        .description("The Student / Course management API, this is a sample for swagger document generation")
        .version("1.0.1")
        .title("Student / Course Management API")
      SampleSwagger.info(info)

      register(new SwaggerController(finatraSwagger = SampleSwagger))
      ...
    }

Swagger API document: ```http://localhost:7070/api-docs```

Swagger UI: ```http://localhost:7070/api-docs/ui```

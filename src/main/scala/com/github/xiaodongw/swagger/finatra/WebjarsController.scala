package com.github.xiaodongw.swagger.finatra

import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.http.routing.FileResolver
import com.twitter.util.Duration

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

object WebjarsController {
  private val DEFAULT_EXPIRE_TIME_MS: Long = 86400000L // 1 day
}

class WebjarsController @Inject() (resolver: FileResolver) extends Controller {
  import WebjarsController._

  private val root: String = "/webjars"
  private val disableCache: Boolean = false

  get(s"${root}/:*") { request: Request =>
    val resourcePath = request.getParam("*")

    val webjarsResourceURI: String = "/META-INF/resources/webjars/" + resourcePath
    //logger.log(Level.FINE, "Webjars resource requested: {0}", webjarsResourceURI)

    if (isDirectoryRequest(webjarsResourceURI)) {
      response.forbidden
    } else {
      val eTagNameTry = Try(getETagName(webjarsResourceURI))
      eTagNameTry match {
        case Failure(e) =>
          response.notFound
        case Success(eTagName) =>
          if (!disableCache) {
            if (checkETagMatch(request, eTagName) || checkLastModify(request)) {
              response.notModified
            } else {
              val inputStream = getClass.getResourceAsStream(webjarsResourceURI)
              if (inputStream != null) {
                val resp = response.ok
                try {
                  if (!disableCache) {
                    prepareCacheHeaders(resp, eTagName)
                  }
                  val filename: String = getFileName(webjarsResourceURI)
                  resp.mediaType = resolver.getContentType(filename)
                  resp.body(inputStream)
                } finally {
                  inputStream.close
                }
              }
              else {
                response.notFound
              }
            }
          }
      }
    }
  }

  private def isDirectoryRequest(uri: String): Boolean = {
    uri.endsWith("/")
  }

  private def getFileName(webjarsResourceURI: String): String = {
    val tokens: Array[String] = webjarsResourceURI.split("/")
    tokens(tokens.length - 1)
  }

  private def getETagName(webjarsResourceURI: String): String = {
    val tokens: Array[String] = webjarsResourceURI.split("/")
    if (tokens.length < 7) {
      throw new IllegalArgumentException("insufficient URL has given: " + webjarsResourceURI)
    }
    val version: String = tokens(5)
    val fileName: String = tokens(tokens.length - 1)
    val eTag: String = fileName + "_" + version
    eTag
  }

  private def checkETagMatch(request: Request, eTagName: String): Boolean = {
    request.headerMap.get("If-None-Match") match {
      case None => false
      case Some(token) => token == eTagName
    }
  }

  private def checkLastModify(request: Request): Boolean = {
    request.headerMap.get("If-Modified-Since").map(_.toLong) match {
      case None => false
      case Some(last) => last - System.currentTimeMillis > 0L
    }
  }

  private def prepareCacheHeaders(response: ResponseBuilder#EnrichedResponse, eTag: String): Unit = {
    response.header("ETag", eTag)
    response.expires = new Date(System.currentTimeMillis() + DEFAULT_EXPIRE_TIME_MS)
    response.lastModified = new Date(System.currentTimeMillis() + DEFAULT_EXPIRE_TIME_MS)
    response.cacheControl = Duration(DEFAULT_EXPIRE_TIME_MS, TimeUnit.MILLISECONDS)
  }
}

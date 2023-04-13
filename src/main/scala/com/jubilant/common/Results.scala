//package com.jubilant.common
//
//import com.jubilant.domain.Errors
//import play.api.Logging
//import play.api.libs.json.{Json, Writes}
//import play.api.mvc.Results._
//import play.api.mvc._
//
//object Results extends Logging {
//
//  def success[T](data: T)(implicit tjs: Writes[T]): Result = Ok(Json.toJson(data))
//
//  def fail(error: Errors): Result = error.httpStatus(Json.obj("code" -> error.code, "message" -> error.message))
//
//  def fail(ex: Throwable): Result = {
//    logger.error("System Error! ", ex)
//    InternalServerError(Json.obj("message" -> ex.getMessage))
//  }
//
//}

package com.jubilant.common
import cats.effect.IO
import org.slf4j.LoggerFactory
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.io.Ok

object Results extends {

  private val logger = LoggerFactory.getLogger(getClass)

}

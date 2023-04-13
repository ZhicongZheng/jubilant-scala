package com.jubilant.interfaces.api

import sttp.model.StatusCode
import sttp.tapir._
import io.circe.generic.auto._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody

package object endpoints {

  case class ErrorMessage(code: Int, message: String)

  // 需要验证 cookie 的接口
  val securedWithBearerEndpoint = endpoint
    .securityIn(auth.apiKey[String](cookie("cookie")))
    .errorOut(statusCode(StatusCode.Unauthorized))
    .errorOut(jsonBody[ErrorMessage])

}

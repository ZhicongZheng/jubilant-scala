package com.jubilant.common

import com.jubilant.domain.Errors
import io.circe.Json

object Util {
  def error2Json(err: Errors): String =
    Json.obj("code" -> Json.fromInt(err.code), "message" -> Json.fromString(err.message)).noSpaces

}

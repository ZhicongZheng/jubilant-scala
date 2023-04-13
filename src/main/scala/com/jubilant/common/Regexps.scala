package com.jubilant.common

import scala.util.matching.Regex

object Regexps {

  private val emailRegex: Regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".r

  def validEmail(email: String): Boolean =
    emailRegex.findFirstMatchIn(email).isDefined

}

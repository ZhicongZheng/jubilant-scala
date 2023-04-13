package com.jubilant.application.command

case class LoginCommand(username: String, password: String, code: String) {}

object LoginCommand {}

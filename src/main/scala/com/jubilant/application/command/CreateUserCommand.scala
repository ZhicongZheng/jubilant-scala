package com.jubilant.application.command

import com.jubilant.common.Constant
import com.jubilant.domain.auth.Role
import com.jubilant.domain.user.User

import scala.language.implicitConversions

case class CreateUserCommand(
  username: String,
  password: String,
  avatar: String,
  nickName: String,
  phone: Option[String] = None,
  email: String,
  role: Long
)

object CreateUserCommand {

  implicit def requestToDo(command: CreateUserCommand): User =
    User(
      Constant.domainCreateId,
      command.username,
      // fixme
      // entryPwd(command.password),
      command.password,
      command.avatar,
      command.nickName,
      command.phone.getOrElse(""),
      command.email,
      Some(Role.justId(command.role))
    )
}

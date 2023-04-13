package com.jubilant.application.command

import scala.language.implicitConversions

case class UpdateUserCommand(
  id: Long,
  avatar: String,
  nickName: String,
  email: String,
  phone: Option[String] = None,
  role: Long,
  updateBy: Long = -1
)

object UpdateUserCommand {

  implicit def commandToDo(command: UpdateUserCommand): (String, String, String, String, Long, Long) =
    (
      command.avatar,
      command.nickName,
      command.phone.getOrElse(""),
      command.email,
      command.role,
      command.updateBy
    )
}

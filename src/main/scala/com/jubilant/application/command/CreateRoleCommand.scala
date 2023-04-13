package com.jubilant.application.command

import com.jubilant.common.Constant
import com.jubilant.domain.auth.{Permission, Role}

import scala.language.implicitConversions

case class CreateRoleCommand(code: String, name: String, permission: Seq[Long] = Nil)

object CreateRoleCommand {

  implicit def requestToDo(request: CreateRoleCommand): Role =
    Role(Constant.domainCreateId, request.code, request.name, permissions = request.permission.map(id => Permission.justId(id)))
}

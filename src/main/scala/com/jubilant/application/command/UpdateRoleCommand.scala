package com.jubilant.application.command

case class UpdateRoleCommand(id: Long, name: String, permissions: Seq[Long] = Nil, updateBy: Option[Long] = None)

object UpdateRoleCommand {}

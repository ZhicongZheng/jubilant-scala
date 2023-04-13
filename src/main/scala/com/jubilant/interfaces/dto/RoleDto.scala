package com.jubilant.interfaces.dto

import com.jubilant.domain.auth.Role
import com.jubilant.infra.db.po.RolePo
import io.scalaland.chimney.dsl.TransformerOps

import java.time.LocalDateTime
import scala.language.implicitConversions

case class RoleDto(
  id: Long,
  code: String,
  name: String,
  permissions: Seq[PermissionDto] = Nil,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
)

object RoleDto {

  implicit def formDo(role: Role): RoleDto =
    role.into[RoleDto].withFieldConst(_.permissions, Nil).transform

  implicit def fromPo(role: RolePo): RoleDto =
    role.into[RoleDto].withFieldConst(_.permissions, Nil).transform

  implicit def fromDoOpt(roleOpt: Option[Role]): Option[RoleDto] =
    roleOpt.map(d => RoleDto.formDo(d))
}

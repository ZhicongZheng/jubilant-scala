package com.jubilant.interfaces.dto

import com.jubilant.domain.user.User
import com.jubilant.infra.db.po.UserPo
import io.scalaland.chimney.dsl.TransformerOps

import java.time.LocalDateTime
import scala.language.implicitConversions

case class UserDto(
  id: Long,
  username: String,
  password: String,
  avatar: String,
  nickName: String,
  phone: String,
  email: String,
  role: Option[RoleDto] = None,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
)

object UserDto {

  implicit def fromDo(user: User): UserDto =
    user.into[UserDto].withFieldConst(_.password, "").transform

  implicit def fromPo(user: UserPo): UserDto =
    user
      .into[UserDto]
      .withFieldConst(_.password, "")
      .withFieldConst(_.role, None)
      .transform
}

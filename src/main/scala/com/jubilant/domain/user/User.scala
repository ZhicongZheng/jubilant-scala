package com.jubilant.domain.user

import com.jubilant.domain.{BaseEntity, Errors, LOGIN_FAILED}
import com.jubilant.domain.auth.Role

import java.time.LocalDateTime
import scala.util.{Success, Try}

final case class User(
  id: Long,
  username: String,
  password: String,
  avatar: String,
  nickName: String,
  phone: String,
  email: String,
  role: Option[Role] = None,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
) extends BaseEntity {

  def login(pwd: String)(implicit checkDef: (String, String) => Boolean): Either[Errors, User] =
    Try(checkDef(pwd, password)) match {
      case Success(res) if res => Right(this)
      case _                   => Left(LOGIN_FAILED)
    }

  def checkPwd(oldPassword: String)(implicit checkDef: (String, String) => Boolean): Boolean =
    Try(checkDef(oldPassword, password)).getOrElse(false)

  def changeRole(role: Role): User = copy(role = Some(role))

  def changePwd(password: String)(implicit encode: String => String): User = copy(password = encode(password))

  def update(param: (String, String, String, String, Long, Long)): User = {
    val user =
      copy(avatar = param._1, nickName = param._2, phone = param._3, email = param._4, updateBy = param._5, updateAt = LocalDateTime.now())
    user.changeRole(Role.justId(param._6))
  }

}

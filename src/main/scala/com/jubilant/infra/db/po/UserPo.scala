package com.jubilant.infra.db.po

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.time.LocalDateTime

final case class UserPo(
  id: Long,
  username: String,
  password: String,
  avatar: String,
  nickName: String,
  phone: String,
  email: String,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
) extends Po

object UserPo {

  class UserTable(tag: Tag) extends Table[UserPo](tag, "users") with BaseTable {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def username = column[String]("username")

    def password = column[String]("password")

    def avatar = column[String]("avatar")

    def nickName = column[String]("nick_name")

    def phone = column[String]("phone")

    def email = column[String]("email")

    def createBy = column[Long]("create_by")

    def updateBy = column[Long]("update_by")

    def createAt = column[LocalDateTime]("create_at")

    def updateAt = column[LocalDateTime]("update_at")

    override def * = (
      id,
      username,
      password,
      avatar,
      nickName,
      phone,
      email,
      createBy,
      updateBy,
      createAt,
      updateAt
    ) <> ((UserPo.apply _).tupled, UserPo.unapply)
  }

}

package com.jubilant.infra.db.po

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.time.LocalDateTime

final case class RolePo(
  id: Long,
  code: String,
  name: String,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
) extends Po

object RolePo {

  class RoleTable(tag: Tag) extends Table[RolePo](tag, "roles") with BaseTable {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def code = column[String]("code")

    def name = column[String]("name")

    def createBy = column[Long]("create_by")

    def updateBy = column[Long]("update_by")

    def createAt = column[LocalDateTime]("create_at")

    def updateAt = column[LocalDateTime]("update_at")

    override def * =
      (id, code, name, createBy, updateBy, createAt, updateAt) <> ((RolePo.apply _).tupled, RolePo.unapply)
  }

  class UserRoleTable(tag: Tag) extends Table[(Long, Long, Long)](tag, "user_roles") with IdTable {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def roleId = column[Long]("role_id")

    override def * = (id, userId, roleId)
  }

}

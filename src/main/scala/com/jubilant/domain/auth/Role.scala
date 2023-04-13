package com.jubilant.domain.auth

import com.jubilant.domain.BaseEntity

import java.time.LocalDateTime

final case class Role(
  id: Long,
  code: String,
  name: String,
  permissions: Seq[Permission] = Nil,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
) extends BaseEntity {

  def update(name: String, permissionIds: Seq[Long]): Role = {
    val role = this.copy(name = name, updateAt = LocalDateTime.now())
    role.copy(permissions = permissionIds.map(id => Permission.justId(id)))
  }

  def beSuperAdmin: Boolean = name == "SUPER_ADMIN"

}

object Role {

  def justId(id: Long): Role = Role(id, "", "")
}

package com.jubilant.domain.auth

import com.jubilant.domain.BaseEntity

import java.time.LocalDateTime

final case class Permission(
  id: Long,
  `type`: String,
  value: String,
  name: String,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
) extends BaseEntity

object Permission {

  def justId(id: Long): Permission = Permission(id, "", "", "")
}

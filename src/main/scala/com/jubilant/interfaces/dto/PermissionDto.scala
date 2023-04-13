package com.jubilant.interfaces.dto

import com.jubilant.domain.auth.Permission
import com.jubilant.infra.db.po.PermissionPo
import io.scalaland.chimney.dsl.TransformerOps

import java.time.LocalDateTime
import scala.language.implicitConversions

case class PermissionDto(
  id: Long,
  `type`: String,
  value: String,
  name: String,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
)

object PermissionDto {

  implicit def fromDo(p: Permission): PermissionDto = p.into[PermissionDto].transform

  implicit def fromPo(p: PermissionPo): PermissionDto = p.into[PermissionDto].transform

  implicit def fromDoSeq(seq: Seq[Permission]): Seq[PermissionDto] = seq.map(p => PermissionDto.fromDo(p))
}

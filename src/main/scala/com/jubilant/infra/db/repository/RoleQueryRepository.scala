package com.jubilant.infra.db.repository

import com.jubilant.infra.db.po.{PermissionPo, RolePo}

import scala.concurrent.Future

trait RoleQueryRepository extends QueryRepository[RolePo] {

  def findByCode(code: String): Future[Option[RolePo]]

  def listPermissions(): Future[Seq[PermissionPo]]

  def findUserRoleMap(userId: Seq[Long]): Future[Map[Long, RolePo]]

  def findRolePermissionMap(roleIds: Seq[Long]): Future[Map[Long, Seq[PermissionPo]]]
}

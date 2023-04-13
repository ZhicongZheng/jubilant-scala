package com.jubilant.infra.db.repository.impl

import com.jubilant.common.Constant
import com.jubilant.domain.auth.{Permission, Role, RoleRepository}
import com.jubilant.infra.db.assembler.AuthorityAssembler._
import com.jubilant.infra.db.po.PermissionPo.{PermissionTable, RolePermissionTable}
import com.jubilant.infra.db.po.RolePo.{RoleTable, UserRoleTable}
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import com.jubilant.infra.db.slick_pg.PostgresProfile.backend.Database
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class RoleRepositoryImpl(private val db: Database)(implicit ec: ExecutionContext) extends RoleRepository {

  private val roles = TableQuery[RoleTable]

  private val permissions = TableQuery[PermissionTable]

  private val userRoles = TableQuery[UserRoleTable]

  private val rolePermissions = TableQuery[RolePermissionTable]

  override def getByUser(userId: Long): Future[Option[Role]] =
    db.run(userRoles.filter(_.userId === userId).map(_.roleId).result.headOption).flatMap {
      case None         => Future.successful(None)
      case Some(roleId) => get(roleId)
    }

  override def save(domain: Role): Future[Long] =
    domain.id match {
      case Constant.domainCreateId => doInsert(domain)
      case _                       => doUpdate(domain)
    }

  override def get(id: Long): Future[Option[Role]] =
    db.run(roles.filter(_.id === id).result.headOption).map(toRoleDoOpt).flatMap {
      case None       => Future.successful(None)
      case Some(role) => findPermissionByRole(id).map(ps => Some(role.copy(permissions = ps)))
    }

  override def remove(id: Long): Future[Unit] =
    db.run {
      for {
        delCount <- roles.filter(_.id === id).delete
        _        <- rolePermissions.filter(_.roleId === id).delete
      } yield ()
    }

  def findPermissionByRole(roleId: Long): Future[Seq[Permission]] = {
    val joinQuery = permissions join rolePermissions on (_.id === _.roleId)
    db.run(joinQuery.filter(_._2.roleId === roleId).map(_._1).result).map(toPermissionDoSeq)
  }

  private def doInsert(role: Role): Future[Long] = {
    val insertRole = db.run(roles returning roles.map(_.id) += role)
    insertRole.flatMap { roleId =>
      role.permissions match {
        case Nil => Future.successful(roleId)
        case _   => insertRolePermission(roleId, role.permissions.map(_.id)).map(_ => roleId)
      }
    }
  }

  private def doUpdate(role: Role): Future[Long] = {
    val updateRole = db.run(roles.filter(_.id === role.id).update(role)).map(_ => role.id)
    updateRole.flatMap { roleId =>
      role.permissions match {
        case Nil => Future.successful(roleId)
        case _   => deleteRolePermission(roleId).flatMap(_ => insertRolePermission(roleId, role.permissions.map(_.id)).map(_ => roleId))
      }
    }
  }

  private def deleteRolePermission(roleId: Long): Future[Unit] =
    db.run(rolePermissions.filter(_.roleId === roleId).delete).map(_ => ())

  private def insertRolePermission(roleId: Long, permissions: Seq[Long]): Future[Unit] = {
    val rolePermissionSeq = permissions.map(permissionId => (roleId, permissionId))
    db.run(rolePermissions.map(t => (t.roleId, t.permissionId)) ++= rolePermissionSeq).map(_ => ())
  }
}

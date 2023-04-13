package com.jubilant.application.service

import com.jubilant.common.{BasePageQuery, Page}
import com.jubilant.infra.db.po.PermissionPo
import com.jubilant.infra.db.repository.RoleQueryRepository
import com.jubilant.interfaces.dto.{PermissionDto, RoleDto}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RoleQueryService(roleQueryRepository: RoleQueryRepository) {

  def listRolesByPage(pageQuery: BasePageQuery): Future[Page[RoleDto]] =
    roleQueryRepository.listByPage(pageQuery).flatMap { rolePage =>
      val roleMap = rolePage.data.map(role => role.id -> role).toMap
      val roleIds = roleMap.keys.toSeq
      roleQueryRepository.findRolePermissionMap(roleIds).map { map =>
        val roleDtos = roleMap.map { tuple =>
          val permissions = map.get(tuple._1).map(ps => ps.map(PermissionDto.fromPo)).getOrElse(Nil)
          RoleDto.fromPo(tuple._2).copy(permissions = permissions)
        }
        rolePage.copy(data = roleDtos.toSeq)
      }
    }

  def listPermission(): Future[Seq[PermissionPo]] = roleQueryRepository.listPermissions();

}

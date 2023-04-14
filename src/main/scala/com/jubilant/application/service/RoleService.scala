package com.jubilant.application.service

import com.jubilant.application.command.{CreateRoleCommand, UpdateRoleCommand}
import com.jubilant.infra.inject.Module.{
  roleQueryRepository,
  roleRepository => roleAggregateRepository,
  userRepository => userAggregateRepository
}
import com.jubilant.domain._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object RoleService {

  def changeUserRole(userId: Long, roleId: Long): Future[Either[Errors, Unit]] = {
    val userRoleOpt = for {
      userOpt <- userAggregateRepository.get(userId)
      roleOpt <- roleAggregateRepository.get(roleId)
    } yield (userOpt, roleOpt)
    userRoleOpt.flatMap {
      case (Some(user), Some(r)) =>
        userAggregateRepository.save(user.changeRole(r)).map(_ => Right(()))
      case (_, None) => Future.successful(Left(NO_ROLE))
      case _         => Future.successful(Left(NO_USER))
    }
  }

  def createRole(request: CreateRoleCommand): Future[Either[Errors, Long]] =
    roleQueryRepository.findByCode(request.code) flatMap {
      case Some(_) => Future.successful(Left(ROLE_CODE_EXIST))
      case None    => roleAggregateRepository.save(request).map(id => Right(id))
    }

  def updateRole(request: UpdateRoleCommand): Future[Either[Errors, Unit]] =
    roleAggregateRepository.get(request.id) flatMap {
      case None => Future.successful(Left(NO_ROLE))
      case Some(role) =>
        roleAggregateRepository.save(role.update(request.name, request.permissions)).map(_ => Right(()))
    }

  def deleteRole(id: Int): Future[Either[Errors, Unit]] =
    roleAggregateRepository.get(id).flatMap {
      case None                            => Future.successful(Right(()))
      case Some(role) if role.beSuperAdmin => Future.successful(Left(CAN_NOT_DEL_SUPER_ADMIN))
      case _                               => roleAggregateRepository.remove(id).map(_ => Right(()))
    }

}

package com.jubilant.infra.db.repository.impl

import com.jubilant.common.Constant
import com.jubilant.domain.auth.RoleRepository
import com.jubilant.domain.user.{User, UserRepository}
import com.jubilant.infra.db.assembler.UserAssembler._
import com.jubilant.infra.db.po.RolePo.UserRoleTable
import com.jubilant.infra.db.po.UserPo.UserTable
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import com.jubilant.infra.db.slick_pg.PostgresProfile.backend.Database
import slick.lifted.TableQuery

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class UserRepositoryImpl(
  private val db: Database,
  private val roleAggregateRepository: RoleRepository
)(implicit ec: ExecutionContext)
    extends UserRepository {

  private val users = TableQuery[UserTable]

  private val userRoles = TableQuery[UserRoleTable]

  override def save(domain: User): Future[Long] =
    domain.id match {
      case Constant.domainCreateId => doInsert(domain)
      case _                       => doUpdate(domain)
    }

  override def get(id: Long): Future[Option[User]] =
    for {
      user <- db.run(users.filter(_.id === id).result.headOption).map(toDoOpt)
      role <- roleAggregateRepository.getByUser(id)
    } yield user.map(user => user.copy(role = role))

  override def getByName(username: String): Future[Option[User]] =
    db.run(users.filter(_.username === username).result.headOption).flatMap {
      case None         => Future.successful(None)
      case Some(userPo) => get(userPo.id)
    }

  override def remove(id: Long): Future[Unit] = db.run {
    for {
      del <- users.filter(_.id === id).delete
      _   <- userRoles.filter(_.userId === id).delete
    } yield ()
  }

  private def insertUserRole(userId: Long, roleId: Long): Future[Int] = db.run(userRoles.map(t => (t.userId, t.roleId)) += (userId, roleId))

  private def deleteUserRole(userId: Long): Future[Unit] = db.run(userRoles.filter(_.userId === userId).delete.map(_ => ()))

  private def doInsert(user: User): Future[Long] = {
    val insertUser = db.run((users returning users.map(_.id)) += user)

    insertUser.flatMap { userId =>
      user.role match {
        case None       => Future.successful(userId)
        case Some(role) => insertUserRole(userId, role.id).map(_ => userId)
      }
    }
  }

  private def doUpdate(user: User): Future[Long] = {
    val updateUser = db.run(users.filter(_.id === user.id).update(user.copy(updateAt = LocalDateTime.now())))

    updateUser.flatMap { _ =>
      user.role match {
        case None => Future.successful(user.id)
        case Some(role) =>
          deleteUserRole(user.id).flatMap(_ => insertUserRole(user.id, role.id).map(_ => user.id))
      }
    }
  }

}

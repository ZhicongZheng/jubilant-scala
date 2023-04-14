package com.jubilant.application.service

import com.jubilant.application.command.{ChangePasswordCommand, CreateUserCommand, LoginCommand, UpdateUserCommand}
import com.jubilant.domain.user.User
import com.jubilant.domain.{Errors, NO_USER, OLD_PWD_ERROR, USER_EXIST}
import com.jubilant.infra.inject.Module.{userQueryRepository, userRepository => userAggregateRepository}
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserService {

  def login(loginRequest: LoginCommand): Future[Either[Errors, User]] =
    userAggregateRepository.getByName(loginRequest.username) flatMap {
      case Some(user) =>
        Future.successful(user.login(loginRequest.password)((pwd, password) => BCrypt.checkpw(pwd, password)))
      case None => Future.successful(Left(NO_USER))
    }

  def deleteUser(id: Int): Future[Int] = userAggregateRepository.remove(id).map(_ => id)

  def createUser(request: CreateUserCommand): Future[Either[Errors, Long]] =
    userQueryRepository.findByUsername(request.username) flatMap {
      case Some(_) => Future.successful(Left(USER_EXIST))
      case None    => userAggregateRepository.save(request).map(id => Right(id))
    }

  def updateUser(command: UpdateUserCommand): Future[Either[Errors, Unit]] =
    userAggregateRepository.get(command.id) flatMap {
      case None       => Future.successful(Left(NO_USER))
      case Some(user) => userAggregateRepository.save(user.update(command)).map(_ => Right(()))
    }

  def changePwd(userId: Long, request: ChangePasswordCommand): Future[Either[Errors, Unit]] =
    userAggregateRepository.get(userId) flatMap {
      case None => Future.successful(Left(NO_USER))
      case Some(user) =>
        if (user.checkPwd(request.oldPassword)((oldPassword, password) => BCrypt.checkpw(oldPassword, password))) {
          userAggregateRepository.save(user.changePwd(request.newPassword)).map(_ => Right(()))
        } else Future.successful(Left(OLD_PWD_ERROR))
    }

}

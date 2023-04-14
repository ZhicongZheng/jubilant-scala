package com.jubilant.application.service

import com.jubilant.common.{BasePageQuery, Page}
import com.jubilant.infra.inject.Module.{roleQueryRepository, userQueryRepository}
import com.jubilant.interfaces.dto.UserDto

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserQueryService {

  def listUserByPage(pageQuery: BasePageQuery): Future[Page[UserDto]] =
    for {
      userPage <- userQueryRepository.listByPage(pageQuery).map(_.map(UserDto.fromPo))
      users = userPage.data
      userRoleMap <- roleQueryRepository.findUserRoleMap(users.map(_.id))
      result = userPage.copy(data = users.map(user => user.copy(role = Some(userRoleMap(user.id)))))
    } yield result

}

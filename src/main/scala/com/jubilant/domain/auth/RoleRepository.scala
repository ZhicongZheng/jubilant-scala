package com.jubilant.domain.auth

import com.jubilant.domain.AggregateRepository

import scala.concurrent.Future

/** 权限仓储
 */
trait RoleRepository extends AggregateRepository[Role] {

  def getByUser(userId: Long): Future[Option[Role]]
}

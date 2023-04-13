package com.jubilant.domain.user

import com.jubilant.domain.AggregateRepository

import scala.concurrent.Future

trait UserRepository extends AggregateRepository[User] {

  def getByName(username: String): Future[Option[User]]
}

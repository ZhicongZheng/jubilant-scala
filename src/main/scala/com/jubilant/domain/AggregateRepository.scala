package com.jubilant.domain

import scala.concurrent.Future

trait AggregateRepository[T] {

  def save(domain: T): Future[Long]

  def get(id: Long): Future[Option[T]]

  def remove(id: Long): Future[Unit]

}

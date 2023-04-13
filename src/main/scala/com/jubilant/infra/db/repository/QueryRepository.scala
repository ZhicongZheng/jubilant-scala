package com.jubilant.infra.db.repository

import com.jubilant.common.{Page, PageQuery}

import scala.concurrent.Future

trait QueryRepository[T] {

  def get(id: Long): Future[Option[T]]

  def list(): Future[Seq[T]]

  def count(): Future[Int]

  def listByPage(pageQuery: PageQuery): Future[Page[T]]

}

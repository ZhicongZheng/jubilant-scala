package com.jubilant.domain.article

import com.jubilant.domain.AggregateRepository

import scala.concurrent.Future

trait ArticleRepository extends AggregateRepository[Article] {

  def addTag(tag: ArticleTag): Future[Unit]

  def removeTag(id: Long): Future[Unit]

  def addCategory(category: ArticleCategory): Future[Unit]

  def removeCategory(id: Long): Future[Unit]

  def updateCategory(category: ArticleCategory): Future[Unit]

}

package com.jubilant.infra.db.repository.impl

import com.jubilant.common.{Page, PageQuery}
import com.jubilant.infra.db.po.UserPo
import com.jubilant.infra.db.po.UserPo.UserTable
import com.jubilant.infra.db.repository.UserQueryRepository
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class UserQueryRepositoryImpl(private val db: Database)(implicit ec: ExecutionContext) extends UserQueryRepository {

  private val users = TableQuery[UserTable]

  override def get(id: Long): Future[Option[UserPo]] = db.run(users.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[UserPo]] = db.run(users.result)

  override def count(): Future[Int] = db.run(users.size.result)

  override def listByPage(pageQuery: PageQuery): Future[Page[UserPo]] = db.run {
    for {
      userPos <- users.drop(pageQuery.offset).take(pageQuery.limit).result
      count   <- users.length.result
    } yield Page(pageQuery.page, pageQuery.size, count, userPos)
  }

  override def findByUsername(username: String): Future[Option[UserPo]] = db.run(users.filter(_.username === username).result.headOption)
}

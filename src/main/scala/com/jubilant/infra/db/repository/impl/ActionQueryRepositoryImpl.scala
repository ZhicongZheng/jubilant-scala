package com.jubilant.infra.db.repository.impl

import com.jubilant.common.{Page, PageQuery}
import com.jubilant.infra.db.po.ActionPo
import com.jubilant.infra.db.po.ActionPo.ActionTable
import com.jubilant.infra.db.repository.ActionQueryRepository
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import com.jubilant.infra.db.slick_pg.PostgresProfile.backend.Database
import slick.lifted.TableQuery

import scala.concurrent.Future

class ActionQueryRepositoryImpl(private val db: Database) extends ActionQueryRepository {

  private val actions = TableQuery[ActionTable]

  override def get(id: Long): Future[Option[ActionPo]] = db.run(actions.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[ActionPo]] = db.run(actions.result)

  override def count(): Future[Int] = db.run(actions.length.result)

  override def listByPage(pageQuery: PageQuery): Future[Page[ActionPo]] = ???
}

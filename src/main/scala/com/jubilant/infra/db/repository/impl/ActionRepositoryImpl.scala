package com.jubilant.infra.db.repository.impl

import com.jubilant.common.Constant
import com.jubilant.domain.action.{Action, ActionRepository}
import com.jubilant.infra.db.assembler.ActionAssembler._
import com.jubilant.infra.db.po.ActionPo.ActionTable
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import com.jubilant.infra.db.slick_pg.PostgresProfile.backend.Database
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class ActionRepositoryImpl(private val db: Database)(implicit ec: ExecutionContext) extends ActionRepository {

  private val actions = TableQuery[ActionTable]

  override def save(action: Action): Future[Long] =
    action.id match {
      case Constant.domainCreateId => db.run(actions returning actions.map(_.id) += action)
      case _                       => db.run(actions.filter(_.id === action.id).update(action).map(_.toLong))
    }

  override def get(id: Long): Future[Option[Action]] =
    db.run(actions.filter(_.id === id).result.headOption).map(opt => opt.map(toDo))

  override def remove(id: Long): Future[Unit] = db.run(actions.filter(_.id === id).delete).map(_ => ())

}

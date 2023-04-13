package com.jubilant.infra.db.repository.impl

import com.jubilant.common.Constant
import com.jubilant.domain.comment.{Comment, CommentRepository}
import com.jubilant.infra.db.assembler.CommentAssembler._
import com.jubilant.infra.db.po.CommentsPo.CommentTable
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import com.jubilant.infra.db.slick_pg.PostgresProfile.backend.Database
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class CommentRepositoryImpl(private val db: Database)(implicit ec: ExecutionContext) extends CommentRepository {

  private val comments = TableQuery[CommentTable]

  override def save(comment: Comment): Future[Long] =
    comment.id match {
      case Constant.domainCreateId => db.run(comments returning comments.map(_.id) += comment)
      case _                       => db.run(comments.filter(_.id === comment.id).update(comment)).map(_.toLong)
    }

  override def get(id: Long): Future[Option[Comment]] = ???

  override def remove(id: Long): Future[Unit] = {
    val delReplyAction =
      sqlu"""with recursive cte as (
                        select id, reply_to from public.comments where reply_to = $id
                        union
                        select a.id, a.reply_to from public.comments as a join cte on a.reply_to = cte.id
                    ) delete from public.comments where id in (select id from cte)"""
    db.run {
      for {
        delReply   <- delReplyAction
        delComment <- comments.filter(_.id === id).delete
      } yield ()
    }
  }
}

package com.jubilant.infra.db.repository

import com.jubilant.common.{Page, PageQuery}
import com.jubilant.infra.db.po.CommentsPo

import scala.concurrent.Future

trait CommentQueryRepository extends QueryRepository[CommentsPo] {

  def listReplyWithLimit(rootCommentIds: Seq[Long], limit: Option[Int]): Future[Map[Long, Seq[CommentsPo]]]

  def listReplyByPage(pageQuery: PageQuery, parent: Long): Future[Page[CommentsPo]]

  def replyCountMap(ids: Seq[Long]): Future[Map[Long, Int]]

  def listRecent(): Future[Seq[CommentsPo]]

}

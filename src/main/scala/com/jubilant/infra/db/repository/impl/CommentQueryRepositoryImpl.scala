package com.jubilant.infra.db.repository.impl

import com.jubilant.common.{Page, PageQuery}
import com.jubilant.infra.db.po.CommentsPo
import com.jubilant.infra.db.po.CommentsPo.CommentTable
import com.jubilant.infra.db.repository.CommentQueryRepository
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import com.jubilant.infra.db.slick_pg.PostgresProfile.backend.Database
import com.jubilant.interfaces.dto.CommentPageQuery

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class CommentQueryRepositoryImpl(private val db: Database)(implicit ec: ExecutionContext) extends CommentQueryRepository {

  private val comments = TableQuery[CommentTable]

  override def get(id: Long): Future[Option[CommentsPo]] = db.run(comments.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[CommentsPo]] = ???

  override def count(): Future[Int] = ???

  override def listByPage(pageQuery: PageQuery): Future[Page[CommentsPo]] = {
    def doQuery(query: CommentPageQuery): Future[Page[CommentsPo]] = {
      val finalQuery = comments
        .filterOpt(query.resourceId)(_.resourceId === _)
        .filterOpt(query.typ)(_.typ === _)
        .filterOpt(query.parent)(_.replyTo === _)

      db.run {
        for {
          pageResult <- finalQuery.sorted(_.createAt.desc).drop(query.offset).take(query.limit).result
          count      <- finalQuery.length.result
        } yield Page(query.page, query.size, count, pageResult)
      }
    }

    pageQuery match {
      case query: CommentPageQuery => doQuery(query)
      case _                       => Future.successful(Page.empty())
    }
  }

  /** 批量查询评论下面的回复，可以限制 group by top N order by creat_at
   *
   *  @param rootCommentIds
   *    父评论的id 集合
   *  @param limit
   *    每一个父评论下面子评论数量的限制
   *  @return
   *    按照父评论分组的回复列表集合
   */
  def listReplyWithLimit(rootCommentIds: Seq[Long], limit: Option[Int]): Future[Map[Long, Seq[CommentsPo]]] = {
    if (rootCommentIds.isEmpty) {
      return Future.successful(Map.empty)
    }
    val limitSql1 = limit.get
    val replyTo   = rootCommentIds.mkString(",")
    val sqlAction1 =
      sql"""
        WITH RECURSIVE cte AS (SELECT id, reply_to FROM comments WHERE reply_to = -1 AND id IN (#$replyTo)

                             UNION ALL

                             (SELECT c.id, c.reply_to FROM comments as c
                                 JOIN cte ON c.reply_to = cte.id  order by c.create_at LIMIT #$limitSql1)
        )
        SELECT id FROM cte;
         """.as[Long]

    db.run {
      for {
        replyIds <- sqlAction1
        pos      <- comments.filter(_.id inSet replyIds).sorted(_.createAt.desc).result
      } yield {
        val poMap    = pos.map(po => po.id -> po).toMap
        val replyMap = pos.filter(_.replyTo != -1).groupMap(_.replyTo)(_.id)

        def loop(parent: Long, sub: Set[Long]): Set[Long] =
          if (replyMap.contains(parent)) {
            val replies = replyMap(parent).toSet
            val allSub  = sub ++ replies
            replies.flatMap(r => loop(r, allSub))
          } else sub

        pos
          .filter(_.replyTo == -1)
          .map(po => po.id -> loop(po.id, Set.empty).map(poMap(_)).toSeq.sortBy(_.createAt)(Ordering[LocalDateTime].reverse))
          .toMap
      }
    }
  }

  override def listReplyByPage(pageQuery: PageQuery, parent: Long): Future[Page[CommentsPo]] = {
    // 构建 sql
    def buildQuery(select: String, page: Option[PageQuery] = None) = {
      val limitOffset = page.map(p => s"limit ${p.limit} offset ${p.offset}").getOrElse("")
      val order       = page.map(_ => s"order by create_at").getOrElse("")
      sql"""with recursive cte as (
                select id, reply_to from comments where reply_to = #$parent
                union
                select a.id, a.reply_to from comments as a join cte on a.reply_to = cte.id
            ) select #$select from comments where id in (select id from cte) #$order #$limitOffset"""
    }

    db.run {
      for {
        ids        <- buildQuery("id", Some(pageQuery)).as[Long]
        pageResult <- comments.filter(_.id inSet ids).sorted(_.createAt.desc).result
        count      <- buildQuery("count(1)", None).as[Int].head
      } yield Page(pageQuery.page, pageQuery.size, count, pageResult)
    }

  }

  override def replyCountMap(ids: Seq[Long]): Future[Map[Long, Int]] = {
    if (ids.isEmpty) {
      return Future.successful(Map.empty)
    }

    def buildCountSql(parent: Long) =
      sql"""
        WITH RECURSIVE cte AS (
          SELECT id, reply_to FROM comments WHERE id = #$parent
          UNION ALL
          SELECT comments.id, comments.reply_to FROM comments JOIN cte ON cte.id = comments.reply_to
      )
      SELECT COUNT(*) FROM cte where reply_to != -1;
         """.as[Int]

    db.run(DBIO.sequence(ids.map(buildCountSql))).map { counts =>
      ids.zip(counts.flatten).toMap
    }

  }

  override def listRecent(): Future[Seq[CommentsPo]] = db.run(comments.sorted(_.createAt.desc).drop(0).take(5).result)
}

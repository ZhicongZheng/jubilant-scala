package com.jubilant.application.service

import com.github.houbb.sensitive.word.core.SensitiveWordHelper
import com.jubilant.common.Page
import com.jubilant.infra.db.repository.CommentQueryRepository
import com.jubilant.interfaces.dto.{CommentDto, CommentPageQuery}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CommentQueryService(commentQueryRepository: CommentQueryRepository) {

  def listRootCommentByPage(pageQuery: CommentPageQuery): Future[Page[CommentDto]] = {
    val query = pageQuery.copy(parent = Some(-1))
    for {
      // 分页获取根评论数
      page <- commentQueryRepository.listByPage(query)
      // 根评论的id集合
      parentIds = page.data.filter(_.replyTo == -1).map(_.id)
      // 递归查询根评论下面的的子评论，限制每一个根评论只查询3条子评论，并且这三条子评论可以不在同一层级
      replies <- commentQueryRepository.listReplyWithLimit(parentIds, Some(3))
      // 递归查询根评论下面的所有子评论的数量
      replyCount <- commentQueryRepository.replyCountMap(parentIds)
    } yield page.map(CommentDto.fromPo).map { dto =>
      val replyList = replies.getOrElse(dto.id, Seq.empty)
      dto.copy(
        reply = replyList.map(po => po.copy(content = SensitiveWordHelper.replace(po.content))).map(CommentDto.fromPo),
        replyCount = replyCount.getOrElse(dto.id, 0),
        content = SensitiveWordHelper.replace(dto.content)
      )
    }
  }

  /** 在一个父评论下分页查询子评论，并且子评论可以不再同一层级
   *
   *  @param pageQuery
   *    分页查询参数
   */
  def listReplyByPage(pageQuery: CommentPageQuery): Future[Page[CommentDto]] =
    for {
      page <- commentQueryRepository.listReplyByPage(pageQuery, pageQuery.parent.get)
    } yield page.map(CommentDto.fromPo).map { dto =>
      dto.copy(content = SensitiveWordHelper.replace(dto.content))
    }

  def listRecentComment(): Future[Seq[CommentDto]] = commentQueryRepository.listRecent().map(_.map(CommentDto.fromPo))

}

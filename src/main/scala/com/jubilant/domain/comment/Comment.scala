package com.jubilant.domain.comment

import com.jubilant.domain.BaseEntity
import com.jubilant.domain.comment.Comment.Type

import java.time.LocalDateTime

final case class Comment(
  id: Long,
  typ: Int = Type.comment,
  // 评论内容
  content: String,
  // 评论用户名
  userName: String,
  // 用户邮箱
  userEmail: Option[String] = None,
  // 评论下面的回复
  reply: Seq[Comment] = Nil,
  // 回复的评论id
  replyTo: Long = -1,
  // 回复的用户
  replyUser: String = "",
  // 评论的资源（文章）
  resourceId: Long,
  // 评论用户的ip
  remoteIp: String,
  remoteAddress: String,
  // 评论有回复时是否允许通知
  allowNotify: Boolean = true,
  createAt: LocalDateTime = LocalDateTime.now()
) extends BaseEntity {

  def isReply: Boolean = replyTo != -1

  def canNotify: Boolean = allowNotify && userEmail.nonEmpty

}

object Comment {

  object Type {
    val comment = 1
    val message = 2
  }

}

package com.jubilant.interfaces.dto

import com.jubilant.domain.comment.Comment.Type
import com.jubilant.infra.db.po.CommentsPo
import io.scalaland.chimney.dsl.TransformerOps

import java.time.LocalDateTime
import scala.language.implicitConversions

case class CommentDto(
  id: Long,
  typ: Int = Type.comment,
  // 评论内容
  content: String,
  // 评论用户名
  userName: String,
  // 用户邮箱
  userEmail: Option[String] = None,
  // 评论下面的回复
  reply: Seq[CommentDto] = Nil,
  replyCount: Int = 0,
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
  allowNotify: Boolean = false,
  createAt: LocalDateTime = LocalDateTime.now()
)

object CommentDto {

  implicit def fromPo(po: CommentsPo): CommentDto = po.into[CommentDto].transform

}

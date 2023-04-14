package com.jubilant.application.service

import cats.effect.IO
import com.jubilant.application.command.CommentCommand
import com.jubilant.domain.comment.Comment
import com.jubilant.domain.{ARTICLE_NOT_EXIST, EMAIL_FORMAT_NOT_INCORRECT, Errors, REPLY_TO_NOT_EXIST}
import com.jubilant.infra.inject.Module.{articleQueryRepository, commentQueryRepository, commentRepository}
import org.http4s.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

object CommentService {

  def addComment(cmd: CommentCommand)(implicit request: Request[IO]): Future[Either[Errors, Unit]] = {
    if (!cmd.validateEmail) {
      return Future.successful(Left(EMAIL_FORMAT_NOT_INCORRECT))
    }
    // 如果是文章评论，校验文章是否存在
    val articleFuture = cmd.typ match {
      case Comment.Type.comment => articleQueryRepository.get(cmd.resourceId)
      case _                    => Future.successful(None)
    }
    // 如果是回复评论，校验回复的评论是否存在
    val replyToFuture = cmd.replyTo match {
      case None          => Future.successful(None)
      case Some(replyId) => commentQueryRepository.get(replyId)
    }
    val comment = CommentCommand.toDo(cmd)
    // 验证数据
    val validate = for {
      articleExist <- articleFuture
      replyToExist <- replyToFuture
    } yield (articleExist, replyToExist)

    // 保存评论
    val saveComment = validate.flatMap {
      // 文章不存在，直接返回
      case (None, _) => Future.successful(Left(ARTICLE_NOT_EXIST))
      // 评论回复Id 没有查询到，返回评论不存在
      case (Some(_), replyToOpt) if cmd.replyTo.nonEmpty && replyToOpt.isEmpty => Future.successful(Left(REPLY_TO_NOT_EXIST))
      case (Some(article), replyToOpt) => commentRepository.save(comment).map(_ => Right((article, replyToOpt)))
    }

    // 保存评论之后异步发送邮件
    saveComment.onComplete {
      case Success(Right((article, replyToOpt))) =>
        val allow = (!comment.isReply && comment.canNotify) || (comment.isReply && replyToOpt.nonEmpty && replyToOpt.get.allowNotify)
        if (allow) {
          // fixme
          //          val buildEmailInfo = CommentEmailInfo(article, replyToOpt, comment)
          //          // 开发中先去掉通知
          //          mailService.send(CommentMailBuilder.build(buildEmailInfo))
        }
      case _ => ()
    }
    saveComment.map(_ => Right(()))
  }

  def deleteComment(id: Long): Future[Long] = commentRepository.remove(id).map(_ => id)

}

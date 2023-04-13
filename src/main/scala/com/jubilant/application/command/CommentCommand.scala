package com.jubilant.application.command

import cats.effect.IO
import com.jubilant.common.Constant.domainCreateId
import com.jubilant.common.{Ip2Region, Regexps}
import com.jubilant.domain.comment.Comment
import com.jubilant.domain.comment.Comment.Type
import io.scalaland.chimney.dsl._
import org.http4s.Request

case class CommentCommand(
  id: Option[Long] = None,
  typ: Int = Type.comment,
  content: String,
  userName: String,
  userEmail: Option[String] = None,
  replyTo: Option[Long] = None,
  replyUser: Option[String] = None,
  resourceId: Long,
  allowNotify: Boolean = true
) {
  def validateEmail: Boolean = if (allowNotify && userEmail.isEmpty) {
    Regexps.validEmail(userEmail.get)
  } else true
}

object CommentCommand {

  implicit def toDo(cmd: CommentCommand)(implicit request: Request[IO]): Comment = cmd
    .into[Comment]
    .withFieldComputed(_.id, _.id.getOrElse(domainCreateId))
    .withFieldComputed(_.replyTo, _.replyTo.getOrElse(-1L))
    .withFieldComputed(_.replyUser, _.replyUser.getOrElse(""))
    .withFieldConst(_.remoteIp, Ip2Region.parseIp(request))
    .withFieldConst(_.remoteAddress, Ip2Region.parseAddress(request))
    .transform

}

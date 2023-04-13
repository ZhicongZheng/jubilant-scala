package com.jubilant.infra.db.po

import com.jubilant.domain.comment.Comment.Type
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.time.LocalDateTime

final case class CommentsPo(
  id: Long,
  typ: Int = Type.comment,
  content: String,
  userName: String = "",
  userEmail: Option[String] = None,
  replyTo: Long = -1L,
  replyUser: String = "",
  resourceId: Long,
  remoteIp: String,
  remoteAddress: String,
  allowNotify: Boolean = false,
  createAt: LocalDateTime = LocalDateTime.now()
)

object CommentsPo {

  class CommentTable(_tableTag: Tag) extends Table[CommentsPo](_tableTag, "comments") {
    def * =
      (id, typ, content, userName, userEmail, replyTo, replyUser, resourceId, remoteIp, remoteAddress, allowNotify, createAt).<>(
        (CommentsPo.apply _).tupled,
        CommentsPo.unapply
      )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

    val typ: Rep[Int] = column[Int]("typ")

    /** Database column content SqlType(varchar) */
    val content: Rep[String] = column[String]("content")

    /** Database column user_name SqlType(varchar), Default() */
    val userName: Rep[String] = column[String]("user_name", O.Default(""))

    /** Database column user_email SqlType(varchar), Default(None) */
    val userEmail: Rep[Option[String]] = column[Option[String]]("user_email", O.Default(None))

    /** Database column reply_to SqlType(int8), Default(-1) */
    val replyTo: Rep[Long] = column[Long]("reply_to", O.Default(-1L))

    val replyUser: Rep[String] = column[String]("reply_user", O.Default(""))

    /** Database column resource_id SqlType(int8) */
    val resourceId: Rep[Long] = column[Long]("resource_id")

    val remoteIp: Rep[String] = column[String]("remote_ip")

    /** Database column remote_address SqlType(inet), Length(2147483647,false) */
    val remoteAddress: Rep[String] = column[String]("remote_address")

    /** Database column allow_notify SqlType(bool), Default(false) */
    val allowNotify: Rep[Boolean] = column[Boolean]("allow_notify", O.Default(false))

    /** Database column create_at SqlType(timestamp) */
    val createAt: Rep[LocalDateTime] = column[LocalDateTime]("create_at")

  }
}

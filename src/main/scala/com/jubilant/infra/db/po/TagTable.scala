package com.jubilant.infra.db.po

import com.jubilant.domain.article.ArticleTag
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.time.LocalDateTime

class TagTable(_tableTag: Tag) extends Table[ArticleTag](_tableTag, "tags") {
  def * = (id, name, createAt).<>((ArticleTag.apply _).tupled, ArticleTag.unapply)

  /** Database column id SqlType(serial), AutoInc, PrimaryKey */
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

  /** Database column name SqlType(varchar), Length(256,true) */
  val name: Rep[String] = column[String]("name", O.Length(256, varying = true))

  /** Database column create_at SqlType(timestamp) */
  val createAt: Rep[LocalDateTime] = column[LocalDateTime]("create_at")
}

class ArticleTagTable(tag: Tag) extends Table[(Long, Long, Long)](tag, "article_tags") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def articleId = column[Long]("article_id")

  def tagId = column[Long]("tag_id")

  override def * = (id, articleId, tagId)
}

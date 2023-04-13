package com.jubilant.infra.db.po

import com.jubilant.domain.article.ArticleCategory
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.time.LocalDateTime

class CategoryTable(_tableTag: Tag) extends Table[ArticleCategory](_tableTag, "categories") with IdTable {
  def * = (id, name, parent, createAt).<>((ArticleCategory.apply _).tupled, ArticleCategory.unapply)

  /** Database column id SqlType(serial), AutoInc, PrimaryKey */
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

  /** Database column name SqlType(varchar), Length(255,true) */
  val name: Rep[String] = column[String]("name", O.Length(255, varying = true))

  /** Database column parent SqlType(int8), Default(-1) */
  val parent: Rep[Long] = column[Long]("parent", O.Default(-1L))

  /** Database column create_at SqlType(timestamp) */
  val createAt: Rep[LocalDateTime] = column[LocalDateTime]("create_at")
}

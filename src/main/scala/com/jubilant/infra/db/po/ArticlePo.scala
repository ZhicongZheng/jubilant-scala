package com.jubilant.infra.db.po

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}

import java.time.LocalDateTime

final case class ArticlePo(
  id: Long,
  title: String,
  introduction: String = "",
  frontCover: Option[String] = None,
  contentMd: String = "",
  contentHtml: String = "",
  status: Int = 0,
  category: Option[Long] = None,
  viewCount: Long = 0,
  likeCount: Long = 0,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
) extends Po

object ArticlePo {

  implicit def briefConvert(
    tuple: (Long, String, String, Option[String], Int, Option[Long], Long, Long, java.time.LocalDateTime, java.time.LocalDateTime)
  ): ArticlePo = ArticlePo(
    id = tuple._1,
    title = tuple._2,
    introduction = tuple._3,
    frontCover = tuple._4,
    status = tuple._5,
    category = tuple._6,
    viewCount = tuple._7,
    likeCount = tuple._8,
    createAt = tuple._9,
    updateAt = tuple._10
  )

  val selectFields = (article: ArticleTable) =>
    (
      article.id,
      article.title,
      article.introduction,
      article.frontCover,
      article.status,
      article.category,
      article.viewCount,
      article.likeCount,
      article.createAt,
      article.updateAt
    )

  /** Table description of table articles. Objects of this class serve as prototypes for rows in queries. */
  class ArticleTable(tag: Tag) extends Table[ArticlePo](tag, "articles") with BaseTable {

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

    /** Database column title SqlType(varchar) */
    val title: Rep[String] = column[String]("title")

    /** Database column introduction SqlType(varchar), Default() */
    val introduction: Rep[String] = column[String]("introduction", O.Default(""))

    /** Database column front_cover SqlType(varchar), Default(None) */
    val frontCover: Rep[Option[String]] = column[Option[String]]("front_cover", O.Default(None))

    /** Database column content_md SqlType(varchar), Default() */
    val contentMd: Rep[String] = column[String]("content_md", O.Default(""))

    /** Database column content_html SqlType(varchar), Default() */
    val contentHtml: Rep[String] = column[String]("content_html", O.Default(""))

    /** Database column status SqlType(int2), Default(0) */
    val status: Rep[Int] = column[Int]("status", O.Default(0))

    /** Database column category SqlType(int8), Default(None) */
    val category: Rep[Option[Long]] = column[Option[Long]]("category", O.Default(None))

    val viewCount = column[Long]("view_count", O.Default(0))

    val likeCount = column[Long]("like_count", O.Default(0))

    /** Database column create_by SqlType(int8), Default(0) */
    val createBy: Rep[Long] = column[Long]("create_by", O.Default(0L))

    /** Database column update_by SqlType(int8), Default(0) */
    val updateBy: Rep[Long] = column[Long]("update_by", O.Default(0L))

    /** Database column create_at SqlType(timestamp) */
    val createAt: Rep[LocalDateTime] = column[LocalDateTime]("create_at")

    /** Database column update_at SqlType(timestamp) */
    val updateAt: Rep[LocalDateTime] = column[LocalDateTime]("update_at")

    override def * : ProvenShape[ArticlePo] = (
      id,
      title,
      introduction,
      frontCover,
      contentMd,
      contentHtml,
      status,
      category,
      viewCount,
      likeCount,
      createBy,
      updateBy,
      createAt,
      updateAt
    ) <> ((ArticlePo.apply _).tupled, ArticlePo.unapply)

  }
}

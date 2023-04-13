package com.jubilant.domain.article

import com.jubilant.domain.BaseEntity
import com.jubilant.domain.article.Article.Status

import java.time.LocalDateTime

final case class Article(
  id: Long,
  // 标题
  title: String,
  // 简介
  introduction: String,
  // 封面
  frontCover: Option[String] = None,
  // 标签
  tags: Seq[ArticleTag] = Nil,
  // 分类
  category: Option[ArticleCategory] = None,
  // markdown 内容
  contentMd: String = "",
  // html 内容
  contentHtml: String = "",
  // 状态 0：草稿 -1: 删除 1: 发布
  status: Int = 0,
  // 浏览次数
  viewCount: Long = 0,
  // 点赞次数
  likeCount: Long = 0,
  beTop: Boolean = false,
  createBy: Long = 0L,
  updateBy: Long = 0L,
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
) extends BaseEntity {

  def updateBrief(title: String, introduction: String, frontCover: Option[String]): Article =
    this.copy(title = title, introduction = introduction, frontCover = frontCover, updateAt = LocalDateTime.now(), status = Status.DRAFT)

  def updateContent(contentMd: String, contentHtml: String): Article =
    this.copy(contentMd = contentMd, contentHtml = contentHtml, updateAt = LocalDateTime.now(), status = Status.DRAFT)

  def onView(): Article = this.copy(viewCount = viewCount + 1)

  def onLike(): Article = this.copy(likeCount = likeCount + 1)

  def onUnLike(): Article = this.copy(likeCount = likeCount - 1)

  def changeCategory(category: Option[ArticleCategory]): Article =
    category match {
      case None    => this
      case Some(_) => this.copy(category = category, updateAt = LocalDateTime.now())
    }

  def changeTags(tags: Seq[ArticleTag]): Article =
    tags match {
      case Nil                => this
      case _: Seq[ArticleTag] => this.copy(tags = tags, updateAt = LocalDateTime.now())
    }

  def release(): Article = this.copy(status = Status.RELEASE)

  def offline(): Article = this.copy(status = Status.DRAFT)

}

object Article {

  object Status {
    val DRAFT       = 0
    val DELETE: Int = -1
    val RELEASE     = 1
  }

  def apply(
    id: Long,
    title: String,
    introduction: String,
    frontCover: Option[String],
    contentMd: String,
    contentHtml: String,
    status: Int,
    viewCount: Long,
    likeCount: Long,
    beTop: Boolean,
    createBy: Long,
    updateBy: Long,
    createAt: LocalDateTime,
    updateAt: LocalDateTime
  ): Article =
    Article(
      id,
      title,
      introduction,
      frontCover,
      Nil,
      None,
      contentMd,
      contentHtml,
      status,
      viewCount,
      likeCount,
      beTop,
      createBy,
      updateBy,
      createAt,
      updateAt
    )

}

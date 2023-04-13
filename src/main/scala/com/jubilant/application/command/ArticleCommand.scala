package com.jubilant.application.command

import com.jubilant.common.Constant
import com.jubilant.domain.article.{Article, ArticleCategory, ArticleTag}

import scala.language.implicitConversions

case class ArticleCommand(
  id: Option[Long] = None,
  title: String,
  introduction: String,
  frontCover: Option[String] = None,
  tags: Seq[Long] = Nil,
  category: Option[Long] = None,
  contentMd: String = "",
  contentHtml: String = ""
)

object ArticleCommand {

  implicit def toDo(cmd: ArticleCommand): Article =
    Article(
      id = cmd.id.getOrElse(Constant.domainCreateId),
      title = cmd.title,
      introduction = cmd.introduction,
      frontCover = cmd.frontCover,
      tags = cmd.tags.map(ArticleTag.justId),
      category = cmd.category.map(ArticleCategory.justId),
      contentMd = cmd.contentMd,
      contentHtml = cmd.contentHtml
    )
}

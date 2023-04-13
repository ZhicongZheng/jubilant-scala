package com.jubilant.application.command

import com.jubilant.common.Constant
import com.jubilant.domain.article.ArticleCategory

import scala.language.implicitConversions

case class ArticleCategoryCommand(id: Option[Long] = None, parent: Long = -1, name: String)

object ArticleCategoryCommand {

  implicit def convert(cmd: ArticleCategoryCommand): ArticleCategory =
    ArticleCategory(cmd.id.getOrElse(Constant.domainCreateId), cmd.name, cmd.parent)
}

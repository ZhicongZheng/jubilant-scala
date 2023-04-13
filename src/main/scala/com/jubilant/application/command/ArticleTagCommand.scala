package com.jubilant.application.command

import com.jubilant.common.Constant
import com.jubilant.domain.article.ArticleTag

import scala.language.implicitConversions

case class ArticleTagCommand(id: Option[Long] = None, name: String)

object ArticleTagCommand {

  implicit def convert(cmd: ArticleTagCommand): ArticleTag = ArticleTag(cmd.id.getOrElse(Constant.domainCreateId), cmd.name)
}

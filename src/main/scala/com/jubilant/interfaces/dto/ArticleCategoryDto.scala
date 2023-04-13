package com.jubilant.interfaces.dto

import com.jubilant.domain.article.ArticleCategory
import io.scalaland.chimney.dsl.TransformerOps

import java.time.LocalDateTime
import scala.language.implicitConversions

case class ArticleCategoryDto(
  id: Long,
  name: String,
  parent: Long = -1,
  articleCount: Int = 0,
  children: Seq[ArticleCategoryDto] = Nil,
  createAt: LocalDateTime = LocalDateTime.now()
)

object ArticleCategoryDto {

  implicit def fromPo(po: ArticleCategory): ArticleCategoryDto = po.into[ArticleCategoryDto].transform

}

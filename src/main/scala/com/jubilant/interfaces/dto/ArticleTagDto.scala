package com.jubilant.interfaces.dto

import com.jubilant.domain.article.ArticleTag
import io.scalaland.chimney.dsl.TransformerOps

import java.time.LocalDateTime

case class ArticleTagDto(id: Long, name: String, articleCount: Int = 0, createAt: LocalDateTime = LocalDateTime.now())

object ArticleTagDto {

  def fromPo(po: ArticleTag): ArticleTagDto = po.into[ArticleTagDto].transform

}

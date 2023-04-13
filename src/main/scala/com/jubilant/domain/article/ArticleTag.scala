package com.jubilant.domain.article

import java.time.LocalDateTime

final case class ArticleTag(id: Long, name: String, createAt: LocalDateTime = LocalDateTime.now())

object ArticleTag {

  def justId(id: Long): ArticleTag = ArticleTag(id, "")
}

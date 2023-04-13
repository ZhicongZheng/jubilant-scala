package com.jubilant.domain.article

import java.time.LocalDateTime

final case class ArticleCategory(
  id: Long,
  name: String,
  parent: Long = -1,
  createAt: LocalDateTime = LocalDateTime.now()
)

object ArticleCategory {

  def justId(id: Long): ArticleCategory = ArticleCategory(id, "")

}

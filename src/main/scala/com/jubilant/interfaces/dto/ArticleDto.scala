package com.jubilant.interfaces.dto

import com.jubilant.domain.article.{Article, ArticleCategory, ArticleTag}
import com.jubilant.infra.db.po.ArticlePo
import io.scalaland.chimney.dsl.TransformerOps

import java.time.LocalDateTime
import scala.language.implicitConversions

case class ArticleDto(
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
  createAt: LocalDateTime = LocalDateTime.now(),
  updateAt: LocalDateTime = LocalDateTime.now()
)

object ArticleDto {

  implicit def fromPo(po: ArticlePo): ArticleDto =
    po.into[ArticleDto]
      .withFieldComputed(_.category, _.category.map(ArticleCategory.justId))
      .transform

  implicit def fromDo(domain: Article): ArticleDto = domain.into[ArticleDto].transform
}

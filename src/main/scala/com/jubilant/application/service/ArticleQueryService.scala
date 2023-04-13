package com.jubilant.application.service

import com.jubilant.common.Page
import com.jubilant.infra.inject.Module.articleQueryRepository
import com.jubilant.interfaces.dto.{ArticleCategoryDto, ArticleDto, ArticlePageQuery, ArticleTagDto}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ArticleQueryService {

  def listArticleByPage(pageQuery: ArticlePageQuery): Future[Page[ArticleDto]] =
    articleQueryRepository.listArticleByPage(pageQuery).map(_.map(ArticleDto.fromPo)).flatMap { articlePage =>
      // 文章分类id 的map
      val articleCategoryMap = articlePage.data.map(article => article.id -> article.category).toMap
      val tagsMapFuture      = articleQueryRepository.getArticleTagMap(articleCategoryMap.keySet.toSeq)
      // 分类的id
      val categoryIds = articleCategoryMap.values.filter(_.isDefined).map(_.get.id).toSeq
      val categoryMapFuture =
        articleQueryRepository.listCategoryByIds(categoryIds).map(seq => seq.map(category => category.id -> category).toMap)
      for {
        // 文章标签map
        tagsMap <- tagsMapFuture
        // 文章分类map
        categoryMap <- categoryMapFuture
      } yield articlePage.map { dto =>
        dto.category match {
          case Some(category) => dto.copy(category = categoryMap.get(category.id), tags = tagsMap.getOrElse(dto.id, Nil))
          case None           => dto.copy(tags = tagsMap.getOrElse(dto.id, Nil))
        }
      }
    }

  def listTags(): Future[Seq[ArticleTagDto]] =
    for {
      tags               <- articleQueryRepository.listTags()
      tagArticleCountMap <- articleQueryRepository.getArticleCountMapByTags(tags.map(_.id))
    } yield tags.map { tag =>
      val count = tagArticleCountMap.getOrElse(tag.id, 0)
      ArticleTagDto.fromPo(tag).copy(articleCount = count)
    }

  def listCategorises(): Future[Seq[ArticleCategoryDto]] =
    for {
      categories      <- articleQueryRepository.listCategorises().map(seq => seq.map(ArticleCategoryDto.fromPo))
      articleCountMap <- articleQueryRepository.getArticleCountMapByCategory(categories.map(_.id))
      result = categories.map { category =>
        category.copy(articleCount = articleCountMap.getOrElse(category.id, 0))
      }
    } yield result

}

package com.jubilant.application.service

import com.jubilant.application.command.ArticleCommand
import com.jubilant.application.command.ArticleCommand.toDo
import com.jubilant.common.Constant
import com.jubilant.domain._
import com.jubilant.domain.article.{Article, ArticleCategory, ArticleTag}
import com.jubilant.infra.inject.Module.{articleQueryRepository, articleRepository}
import com.jubilant.interfaces.dto.ArticleDto

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ArticleService {

  def getArticle(id: Long): Future[Either[Errors, ArticleDto]] =
    processArticle(id) { article =>
      val result = article.onView()
      articleRepository.save(result).map(_ => Right(result))
    }

  def createArticle(command: ArticleCommand): Future[Either[Errors, Long]] = {

    val categoryFuture = command.category match {
      case None     => Future.successful[Option[ArticleCategory]](None)
      case Some(id) => articleQueryRepository.getCategoryById(id)
    }

    val tagsFuture = command.tags match {
      case Nil            => Future.successful(Nil)
      case seq: Seq[Long] => articleQueryRepository.listTagsById(seq)
    }

    for {
      tags     <- tagsFuture
      category <- categoryFuture
      result <-
        if (tags.size != command.tags.size || (command.category.isDefined && category.isEmpty)) {
          Future.successful(Left(TAG_OR_CATEGORY_NOT_EXIST))
        } else {
          articleRepository.save(toDo(command).copy(tags = tags, category = category)).map(Right(_))
        }
    } yield result
  }

  def updateArticle(command: ArticleCommand): Future[Either[Errors, Long]] =
    // 如果没传id, 默认按照 -1 查找，返回 文章不存在
    processArticle(command.id.getOrElse(Constant.domainCreateId)) { article =>
      val updatedArticle: Article = article
        .updateBrief(command.title, command.introduction, command.frontCover)
        .updateContent(command.contentMd, command.contentHtml)
        .changeTags(command.tags.map(ArticleTag.justId))
        .changeCategory(command.category.map(ArticleCategory.justId))
      articleRepository.save(updatedArticle).map(id => Right(id))
    }

  def releaseArticle(id: Long): Future[Either[Errors, Unit]] =
    processArticle(id) { article =>
      articleRepository.save(article.release()).map(_ => Right(()))
    }

  def offlineArticle(id: Long): Future[Either[Errors, Unit]] =
    processArticle(id) { article =>
      articleRepository.save(article.offline()).map(_ => Right(()))
    }

  def likeArticle(id: Long, like: Boolean): Future[Either[Errors, Unit]] =
    processArticle(id) { article =>
      val likedArticle = if (like) article.onLike() else article.onUnLike()
      articleRepository.save(likedArticle).map(_ => Right(()))
    }

  def deleteArticle(id: Long): Future[Unit] = articleRepository.remove(id)

  def addTags(tag: ArticleTag): Future[Either[Errors, Unit]] =
    for {
      existTag <- articleQueryRepository.getTagByName(tag.name)
      result   <- if (existTag.isDefined) Future.successful(Left(TAG_EXIST)) else articleRepository.addTag(tag).map(_ => Right(()))
    } yield result

  def removeTag(id: Long): Future[Unit] = articleRepository.removeTag(id)

  def addCategory(category: ArticleCategory): Future[Either[Errors, Unit]] =
    for {
      existCategory <- articleQueryRepository.getCategoryByName(category.name)
      result <-
        if (existCategory.isDefined) Future.successful(Left(CATEGORY_EXIST))
        else articleRepository.addCategory(category).map(_ => Right(()))
    } yield result

  def updateCategory(category: ArticleCategory): Future[Either[Errors, Unit]] =
    articleQueryRepository.getCategoryById(category.id).flatMap {
      case None    => Future.successful(Left(TAG_OR_CATEGORY_NOT_EXIST))
      case Some(_) => articleRepository.updateCategory(category).map(_ => Right(()))
    }

  def removeCategory(id: Long): Future[Unit] = articleRepository.removeCategory(id)

  // 定义一个通用的处理函数 processArticle，将具体的文章操作函数作为参数传入
  // 返回文章处理结果：要么文章不存在，要么调用具体文章操作函数得到的结果
  private[this] def processArticle[T](id: Long)(action: Article => Future[Either[Errors, T]]): Future[Either[Errors, T]] =
    articleRepository.get(id).flatMap {
      case None          => Future.successful(Left(ARTICLE_NOT_EXIST))
      case Some(article) => action(article)
    }

}

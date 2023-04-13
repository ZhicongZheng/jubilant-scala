package com.jubilant.interfaces.routes

import cats.effect.IO
import com.jubilant.application.command.{ArticleCategoryCommand, ArticleCommand, ArticleTagCommand}
import com.jubilant.application.service.{ArticleQueryService, ArticleService}
import com.jubilant.domain.user.User
import com.jubilant.interfaces.dto.ArticlePageQuery
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRequest, AuthedRoutes, EntityDecoder, HttpRoutes, Response}

object ArticleRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  implicit val decoder: EntityDecoder[IO, ArticleCommand] = jsonOf[IO, ArticleCommand]

  private object beLikeQuery extends OptionalQueryParamDecoderMatcher[Boolean]("like")

  private object tagQuery extends OptionalQueryParamDecoderMatcher[Long]("tag")

  private object categoryQuery extends OptionalQueryParamDecoderMatcher[Long]("category")

  private object searchTitleQuery extends OptionalQueryParamDecoderMatcher[String]("searchTitle")

  def routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "articles" / IntVar(id) => okRes(ArticleService.getArticle(id))
      case GET -> Root / "tags"                  => jsonRes(ArticleQueryService.listTags())
      case GET -> Root / "categories"            => jsonRes(ArticleQueryService.listCategorises())
      case POST -> Root / "articles" / "like" / IntVar(id) :? beLikeQuery(like) =>
        createdRes(ArticleService.likeArticle(id, like.getOrElse(false)))
      case GET -> Root / "categories" => jsonRes(ArticleQueryService.listCategorises())
      case GET -> Root / "articles"
          :? pageQueryParam(page)
          +& pageSizeQueryParam(size)
          +& tagQuery(tag)
          +& categoryQuery(category)
          +& searchTitleQuery(searchTitle) =>
        val pageQuery = ArticlePageQuery(page, size, tag, category, searchTitle)
        jsonRes(ArticleQueryService.listArticleByPage(pageQuery))

    }

  def authedRoutes: AuthedRoutes[User, IO] =
    AuthedRoutes.of[User, IO] {
      case req @ POST -> Root / "articles" as _                    => createArticle(req)
      case DELETE -> Root / "articles" / IntVar(id) as _           => jsonRes(ArticleService.deleteArticle(id))
      case req @ PUT -> Root / "articles" as _                     => updateArticle(req)
      case POST -> Root / "articles" / "release" / IntVar(id) as _ => createdRes(ArticleService.releaseArticle(id))
      case POST -> Root / "articles" / "offline" / IntVar(id) as _ => createdRes(ArticleService.offlineArticle(id))
      case req @ POST -> Root / "tags" as _                        => addArticleTag(req)
      case DELETE -> Root / "tags" / IntVar(id) as _               => jsonRes(ArticleService.removeTag(id))
      case req @ POST -> Root / "categories" as _                  => addArticleCategory(req)
      case req @ PUT -> Root / "categories" as _                   => updateCatrgory(req)
      case DELETE -> Root / "categories" / IntVar(id) as _         => jsonRes(ArticleService.removeCategory(id))
    }

  private def createArticle(request: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- request.req.as[ArticleCommand]
      res <- createdRes(ArticleService.createArticle(cmd))
    } yield res

  private def updateArticle(request: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- request.req.as[ArticleCommand]
      res <- okRes(ArticleService.updateArticle(cmd))
    } yield res

  private def addArticleTag(request: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- request.req.as[ArticleTagCommand]
      res <- createdRes(ArticleService.addTags(cmd))
    } yield res

  def addArticleCategory(req: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- req.req.as[ArticleCategoryCommand]
      res <- createdRes(ArticleService.addCategory(cmd))
    } yield res

  def updateCatrgory(req: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- req.req.as[ArticleCategoryCommand]
      res <- okRes(ArticleService.updateCategory(cmd))
    } yield res

}

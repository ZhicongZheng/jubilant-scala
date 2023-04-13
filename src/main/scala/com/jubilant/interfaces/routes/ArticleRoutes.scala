package com.jubilant.interfaces.routes

import cats.effect.IO
import com.jubilant.application.service.{ArticleQueryService, ArticleService}
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object ArticleRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  def routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "/articles" / IntVar(id) => successEither(ArticleService.getArticle(id))
      case GET -> Root / "tags"                   => success(ArticleQueryService.listTags())
      case GET -> Root / "categories"             => success(ArticleQueryService.listCategorises())
    }

}

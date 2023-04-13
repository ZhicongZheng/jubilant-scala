package com.jubilant.interfaces.routes

import cats.effect.IO
import com.jubilant.application.service.ArticleQueryService
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext.Implicits.global

object ArticleRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  def routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "tags"       => Ok(IO.fromFuture(IO(ArticleQueryService.listTags().map(_.asJson))))
      case GET -> Root / "categories" => Ok(IO.fromFuture(IO(ArticleQueryService.listCategorises().map(_.asJson))))
    }

}

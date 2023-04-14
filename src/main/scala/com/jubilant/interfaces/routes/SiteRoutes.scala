package com.jubilant.interfaces.routes

import cats.effect.IO
import com.jubilant.application.command.ActionCommand
import com.jubilant.application.service.{ActionService, SiteQueryService}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

object SiteRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "site"           => jsonRes(SiteQueryService.getSiteInfo)
    case req @ POST -> Root / "actions" => onAction(req)
  }

  private def onAction(req: Request[IO]): IO[Response[IO]] =
    for {
      cmd <- req.as[ActionCommand]
      res <- jsonRes(ActionService.onAction(cmd)(req))
    } yield res

}

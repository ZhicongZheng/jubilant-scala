package com.jubilant

import cats.effect.IO
import com.comcast.ip4s._
import cats.implicits._
import com.jubilant.infra.auth.RequestAuthenticator
import com.jubilant.interfaces.api.ApiDocumentation
import com.jubilant.interfaces.routes.{ArticleRoutes, CommentRoutes, FileRoutes, RoleRoutes, SiteRoutes, UserRoutes}
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI

object JubilantScalaServer {

  def run: IO[Nothing] = {

//    val openApiRoutes: HttpRoutes[IO] =
//      Http4sServerInterpreter[IO]().toRoutes(SwaggerUI[IO](ApiDocumentation.openApiYaml))

    val routes =
//      openApiRoutes <+>
      ArticleRoutes.routes <+>
        UserRoutes.routes <+>
        SiteRoutes.routes <+>
        CommentRoutes.routes <+>
        FileRoutes.authRoutes <+>
        RequestAuthenticator(UserRoutes.authRoutes) <+>
        RequestAuthenticator(ArticleRoutes.authRoutes) <+>
        RequestAuthenticator(CommentRoutes.authRoutes) <+>
        RequestAuthenticator(RoleRoutes.authRoutes)

    val finalHttpApp = Logger.httpRoutes(logHeaders = false, logBody = false)(routes)
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"9080")
      .withHttpApp(finalHttpApp.orNotFound)
      .build
      .useForever
  }

}

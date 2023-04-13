package com.jubilant

import cats.effect.IO
import com.comcast.ip4s._
import cats.implicits._
import com.jubilant.infra.auth.RequestAuthenticator
import com.jubilant.interfaces.routes.{ArticleRoutes, UserRoutes}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger

object JubilantScalaServer {

  def run: IO[Nothing] = {

    val auth = new RequestAuthenticator

    val routes =
      ArticleRoutes.routes <+>
        UserRoutes.routes <+>
        UserRoutes.authRoutes(auth)

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

package com.jubilant.infra.auth

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.jubilant.common.Constant.superAdmin
import com.jubilant.common.{Constant, SystemSession}
import com.jubilant.domain.user.User
import com.jubilant.infra.auth.RequestAuthenticator.parsePermission
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthedRoutes, HttpRoutes, Request}
import org.slf4j.LoggerFactory
import io.circe.generic.auto._
import io.circe.parser._

import scala.util.matching.Regex

class RequestAuthenticator {

  private def authUser = Kleisli { req: Request[IO] =>
    OptionT.fromOption[IO] {
      for {
        cookie   <- SystemSession.parseCookie(req)
        userJson <- SystemSession.getVal(cookie, Constant.SESSION_USER)
        user     <- decode[User](userJson).toOption
        requirePermission = parsePermission(req)
        authedUser <- user.role match {
          case None                                  => Option.empty[User]
          case Some(role) if role.code == superAdmin => Some(user)
          case Some(role) =>
            role.permissions.filter(_.`type` == Constant.functionPermission).find(_.value == requirePermission).map(_ => user)
        }
      } yield authedUser
    }
  }

  // fixme 如果要实现自定义401返回提示，需要返回 Either[Errors,User],目前就先这样吧
  private val middleware = AuthMiddleware[IO, User](authUser)

  def apply(authedService: AuthedRoutes[User, IO]): HttpRoutes[IO] = middleware(authedService)

}
object RequestAuthenticator {

  private val logger = LoggerFactory.getLogger(getClass)

  private final case class WithoutAuthRoute(method: Regex, path: Regex) {
    def matches(request: Request[IO]): Boolean = {

      val method = request.method.name
      val path   = request.pathInfo.toString()

      this.path.matches(path) && this.method.matches(method)
    }
  }

  lazy private val withoutAuthRouteList: Seq[WithoutAuthRoute] = Seq(
    WithoutAuthRoute("POST".r, "/users/login.*".r),
    WithoutAuthRoute("POST".r, "/users/logout".r),
    WithoutAuthRoute(".*".r, "/docs/*".r),
    WithoutAuthRoute(".*".r, "/*.ico".r)
  )

  def withoutAuth(request: Request[IO]): Boolean =
    withoutAuthRouteList.map(_.matches(request)).reduce((a, b) => a && b)

  def parsePermission(request: Request[IO]) = ""

}

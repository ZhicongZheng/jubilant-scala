package com.jubilant.infra.auth

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.jubilant.common.Constant.superAdmin
import com.jubilant.common.Util.error2Json
import com.jubilant.common.{Constant, SystemSession}
import com.jubilant.domain.user.User
import com.jubilant.domain.{Errors, PERMISSION_DENIED, SERVER_ERROR, TOKEN_CHECK_ERROR}
import io.circe.generic.auto._
import io.circe.parser._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthedRequest, AuthedRoutes, HttpRoutes, Request}
import org.slf4j.LoggerFactory

object RequestAuthenticator {

  private val logger = LoggerFactory.getLogger(getClass)

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  private def authUser = Kleisli { req: Request[IO] =>
    IO {
      SystemSession.parseCookie(req).toRight[Errors](TOKEN_CHECK_ERROR).flatMap { cookie: String =>
        SystemSession.getVal(cookie, Constant.SESSION_USER).toRight[Errors](TOKEN_CHECK_ERROR).flatMap { userJson: String =>
          decode[User](userJson).toOption.toRight[Errors](SERVER_ERROR).flatMap { user: User =>
            verifyPermission(parsePermission(req), user)
          }
        }
      }
    }
  }

  private def onAuthFailure: AuthedRoutes[Errors, IO] = Kleisli { req: AuthedRequest[IO, Errors] =>
    OptionT.liftF {
      req.context match {
        case TOKEN_CHECK_ERROR => BadRequest(error2Json(TOKEN_CHECK_ERROR))
        case SERVER_ERROR      => InternalServerError(error2Json(SERVER_ERROR))
        case PERMISSION_DENIED => Forbidden(error2Json(PERMISSION_DENIED))
      }
    }
  }

  private def verifyPermission(requirePermission: String, user: User): Either[Errors, User] =
    user.role match {
      case None                                  => Left(PERMISSION_DENIED)
      case Some(role) if role.code == superAdmin => Right(user)
      case Some(role) =>
        role.permissions
          .filter(_.`type` == Constant.functionPermission)
          .find(_.value == requirePermission)
          .map(_ => user)
          .toRight(PERMISSION_DENIED)
    }

  private val middleware = AuthMiddleware[IO, Errors, User](authUser, onAuthFailure)

  def apply(authedService: AuthedRoutes[User, IO]): HttpRoutes[IO] = middleware(authedService)

  private def parsePermission(request: Request[IO]) = ""

}

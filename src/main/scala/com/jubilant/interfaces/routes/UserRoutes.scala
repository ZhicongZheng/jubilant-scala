package com.jubilant.interfaces.routes

import cats.effect.IO
import com.jubilant.application.command.{ChangePasswordCommand, CreateUserCommand, LoginCommand, UpdateUserCommand}
import com.jubilant.application.service.{UserQueryService, UserService}
import com.jubilant.common.{BasePageQuery, Constant, Kaptcha, SystemSession}
import com.jubilant.domain.LOGIC_CODE_ERR
import com.jubilant.domain.user.User
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.{jsonEncoder, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRequest, AuthedRoutes, EntityDecoder, HttpRoutes, Request, Response}

import java.io.ByteArrayOutputStream
import java.util.{Base64, UUID}
import javax.imageio.ImageIO
import scala.concurrent.Future
object UserRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  def routes: HttpRoutes[IO] = HttpRoutes
    .of[IO] {
      case req @ GET -> Root / "users" / "login-code" => loginCode(req)
      case req @ POST -> Root / "users" / "login"     => login(req)
      case req @ POST -> Root / "users" / "logout"    => logout(req)
    }

  def authRoutes: AuthedRoutes[User, IO] =
    AuthedRoutes.of {
      case GET -> Root / "users" / "current" as user     => Ok(user.asJson)
      case DELETE -> Root / "users" / LongVar(id) as _   => jsonRes(UserService.deleteUser(id.toInt))
      case req @ POST -> Root / "users" as _             => createUser(req)
      case req @ PUT -> Root / "users" as _              => updateUser(req)
      case req @ PUT -> Root / "users" / "password" as _ => updatePassword(req)
      case GET -> Root / "users"
          :? pageQueryParam(page)
          +& pageSizeQueryParam(pageSize) as _ =>
        val pageQuery = BasePageQuery(page, pageSize)
        jsonRes(UserQueryService.listUserByPage(pageQuery))
    }

  private def loginCode(request: Request[IO]): IO[Response[IO]] = {
    val code  = Kaptcha.createText
    val image = Kaptcha.createImage(code)
    val os    = new ByteArrayOutputStream()
    ImageIO.write(image, "png", os)

    val base64        = Base64.getEncoder.encode(os.toByteArray)
    val requestCookie = SystemSession.parseCookie(request).getOrElse(UUID.randomUUID().toString)

    // 验证码放入 session
    SystemSession.putVal(requestCookie, Constant.loginCode, code)

    Ok(base64).map { response =>
      // 没有 cookie 就创建一个
      SystemSession.parseCookie(request) match {
        case None    => response.addCookie(SystemSession.generatorCookie)
        case Some(_) => response
      }
    }
  }

  private def login(request: Request[IO]): IO[Response[IO]] = {
    implicit val loginCommandDecoder: EntityDecoder[IO, LoginCommand] = jsonOf[IO, LoginCommand]
    SystemSession
      .parseCookie(request)
      .fold(BadRequest(LOGIC_CODE_ERR.message)) { cookie =>
        val res = for {
          cmd <- request.as[LoginCommand]
          loginResult <- IO.fromFuture {
            IO {
              val codeMatch = SystemSession.getVal(cookie, Constant.loginCode).filter(code => code == cmd.code)
              codeMatch match {
                case Some(_) =>
                  SystemSession.rmVal(cookie, Constant.loginCode)
                  UserService.login(cmd)
                case None => Future.successful(Left(LOGIC_CODE_ERR))
              }
            }
          }
        } yield loginResult
        res.flatMap {
          case Left(err) => BadRequest(err.message)
          case Right(user) =>
            SystemSession.putVal(cookie, Constant.SESSION_USER, user.asJson.toString())
            Ok()
        }
      }
  }

  private def logout(request: Request[IO]) = {
    SystemSession.parseCookie(request).foreach(cookie => SystemSession.rmCookie(cookie))
    Ok()
  }

  private def createUser(req: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- req.req.as[CreateUserCommand]
      res <- createdRes(UserService.createUser(cmd))
    } yield res

  private def updateUser(req: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- req.req.as[UpdateUserCommand]
      res <- okRes(UserService.updateUser(cmd))
    } yield res

  private def updatePassword(req: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- req.req.as[ChangePasswordCommand]
      res <- okRes(UserService.changePwd(req.context.id, cmd))
    } yield res

}

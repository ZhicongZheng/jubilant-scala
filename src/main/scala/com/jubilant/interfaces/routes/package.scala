package com.jubilant.interfaces

import cats.effect.IO
import com.jubilant.common.Util.error2Json
import com.jubilant.domain.Errors
import io.circe.Encoder
import io.circe.syntax._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{Request, Response}

import scala.concurrent.Future

package object routes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  object pageQueryParam     extends QueryParamDecoderMatcherWithDefault[Int]("page", 1)
  object pageSizeQueryParam extends QueryParamDecoderMatcherWithDefault[Int]("page", 10)

  def okRes[T](
    future: Future[Either[Errors, T]]
  )(implicit encoder: Encoder[T], request: Option[Request[IO]] = None): IO[Response[IO]] =
    IO.fromFuture(IO(future)).flatMap {
      case Left(err)  => BadRequest(error2Json(err))
      case Right(res) => Ok(res.asJson)
    }

  def createdRes[T](
    future: Future[Either[Errors, T]]
  )(implicit encoder: Encoder[T], request: Option[Request[IO]] = None): IO[Response[IO]] =
    IO.fromFuture(IO(future)).flatMap {
      case Left(err)  => BadRequest(error2Json(err))
      case Right(res) => Created(res.asJson)
    }

  def jsonRes[T](future: Future[T])(implicit encoder: Encoder[T], request: Option[Request[IO]] = None): IO[Response[IO]] =
    IO.fromFuture(IO(future)).flatMap(res => Ok(res.asJson))
}

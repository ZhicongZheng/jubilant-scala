package com.jubilant.interfaces.routes

import cats.effect.IO
import com.jubilant.application.command.CommentCommand
import com.jubilant.application.service.{CommentQueryService, CommentService}
import com.jubilant.domain.user.User
import com.jubilant.interfaces.dto.CommentPageQuery
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import io.circe.generic.auto._
import org.http4s.{AuthedRoutes, HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl

object CommentRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  private object resourceIdQuery extends QueryParamDecoderMatcher[Long]("resourceId")

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "comments"     => addComment(req)
    case GET -> Root / "comments" / "recent" => jsonRes(CommentQueryService.listRecentComment())
    case GET -> Root / "comments"
        :? pageQueryParam(page)
        +& pageSizeQueryParam(pageSize)
        +& resourceIdQuery(resourceId) =>
      val pageQuery = CommentPageQuery(page, pageSize, resourceId = Some(resourceId))
      jsonRes(CommentQueryService.listRootCommentByPage(pageQuery))
    case GET -> Root / "comments" / LongVar(parent) / "replies"
        :? pageQueryParam(page)
        +& pageSizeQueryParam(pageSize) =>
      val pageQuery = CommentPageQuery(page, pageSize, None, parent = Some(parent))
      jsonRes(CommentQueryService.listReplyByPage(pageQuery))
  }

  def authRoutes: AuthedRoutes[User, IO] = AuthedRoutes.of[User, IO] { case DELETE -> Root / "comments" / LongVar(id) as _ =>
    jsonRes(CommentService.deleteComment(id))
  }

  private def addComment(req: Request[IO]): IO[Response[IO]] =
    for {
      cmd <- req.as[CommentCommand]
      res <- createdRes(CommentService.addComment(cmd)(req))
    } yield res

}

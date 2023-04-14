package com.jubilant.interfaces.routes

import cats.effect.IO
import com.jubilant.application.command.{CreateRoleCommand, UpdateRoleCommand}
import com.jubilant.application.service.{RoleQueryService, RoleService}
import com.jubilant.common.BasePageQuery
import com.jubilant.domain.user.User
import com.jubilant.infra.inject.Module.ec
import com.jubilant.interfaces.dto.PermissionDto
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRequest, AuthedRoutes, Response}

object RoleRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  def authRoutes: AuthedRoutes[User, IO] = AuthedRoutes.of[User, IO] {
    case POST -> Root / "users" / LongVar(userId) / "roles" / LongVar(roleId) as _ => createdRes(RoleService.changeUserRole(userId, roleId))
    case req @ POST -> Root / "roles" as _                                         => createRole(req)
    case req @ PUT -> Root / "roles" as _                                          => updateRole(req)
    case DELETE -> Root / "roles" / LongVar(id) as _                               => okRes(RoleService.deleteRole(id.toInt))
    case GET -> Root / "roles" / "permissions" as _ => jsonRes(RoleQueryService.listPermission().map(ps => ps.map(PermissionDto.fromPo)))
    case GET -> Root / "roles"
        :? pageQueryParam(page)
        +& pageSizeQueryParam(pageSize) as _ =>
      val pageQuery = BasePageQuery(page, pageSize)
      jsonRes(RoleQueryService.listRolesByPage(pageQuery))
  }

  private def createRole(req: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- req.req.as[CreateRoleCommand]
      res <- createdRes(RoleService.createRole(cmd))
    } yield res

  private def updateRole(req: AuthedRequest[IO, User]): IO[Response[IO]] =
    for {
      cmd <- req.req.as[UpdateRoleCommand]
      res <- createdRes(RoleService.updateRole(cmd))
    } yield res

}

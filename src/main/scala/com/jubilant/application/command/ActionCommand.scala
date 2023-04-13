package com.jubilant.application.command

import cats.effect.IO
import com.jubilant.common.{Constant, Ip2Region}
import com.jubilant.domain.action.Action
import org.http4s.Request

case class ActionCommand(
  id: Option[Long] = None,
  typ: Int,
  resourceId: Long,
  resourceInfo: String
)

object ActionCommand {

  implicit def toDo(cmd: ActionCommand)(implicit request: Request[IO]): Action = Action(
    cmd.id.getOrElse(Constant.domainCreateId),
    cmd.typ,
    cmd.resourceId,
    cmd.resourceInfo,
    Ip2Region.parseIp(request),
    Ip2Region.parseAddress(request)
  )
}

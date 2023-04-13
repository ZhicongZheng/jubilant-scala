package com.jubilant.application.service

import cats.effect.IO
import com.jubilant.application.command.ActionCommand
import com.jubilant.infra.inject.Module.actionRepository
import org.http4s.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ActionService {

  def onAction(command: ActionCommand)(implicit request: Request[IO]): Future[Unit] =
    actionRepository.save(ActionCommand.toDo(command)).map(_ => ())
}

package com.jubilant

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run: IO[Nothing] = JubilantScalaServer.run
}

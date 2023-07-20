package com.dv01.backendchallenge

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = Dv01Server.run[IO]
}

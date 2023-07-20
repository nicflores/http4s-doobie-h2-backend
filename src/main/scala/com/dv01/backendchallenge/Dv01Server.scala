package com.dv01.backendchallenge

import cats.effect.Async
import cats.data.Kleisli
import doobie.util.transactor.Transactor
import com.comcast.ip4s._
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.http4s.Request
import org.http4s.Response
import org.http4s.server.Router
import fs2.Stream
import cats.effect.kernel.Resource
import org.h2.tools.{Server => DBServer}
import org.http4s.server.{Server => WebServer}

object Dv01Server {

  def makeRouter[F[_]: Async](
      transactor: Transactor[F]
  ): Kleisli[F, Request[F], Response[F]] = {
    val route = Router[F](
      "/api/v1" -> Dv01Routes.dv01Routes[F](
        new LoanStatsImpl(transactor)
      )
    ).orNotFound
    Logger.httpApp(true, true)(route)
  }

  def microservices[F[_]: Async: Network]
      : Resource[F, (DBServer, Resource[F, WebServer])] =
    for {
      dbServer <- H2Server.start[F]
      xa <- Database.init[F]
      httpServer = EmberServerBuilder
        .default[F]
        .withHost(host"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(makeRouter(xa))
        .build
    } yield (dbServer, httpServer)

  def run[F[_]: Async: Network]: F[Unit] = {
    Stream
      .resource(microservices)
      .flatMap { case (db, web) =>
        Stream.eval(web.useForever).concurrently {
          Stream(
            db
          )
        }
      }
      .compile
      .drain
  }
}

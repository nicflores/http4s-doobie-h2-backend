package com.dv01.backendchallenge

import cats.Applicative
import cats.effect.kernel.Resource
import cats.implicits._
import org.h2.tools.Server

object H2Server {
  def start[F[_]: Applicative]: Resource[F, Server] =
    Resource.make[F, Server] {
      Server.createTcpServer("-tcpAllowOthers", "-ifNotExists").start().pure[F]
    } { server =>
      server.shutdown().pure[F]
    }
}

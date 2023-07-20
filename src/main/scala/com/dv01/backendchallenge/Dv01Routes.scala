package com.dv01.backendchallenge

import cats.effect.Sync
import cats.implicits._
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._

object Dv01Routes {

  def dv01Routes[F[_]: Sync](loanStats: LoanStats[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root => Ok("Hello DV01!")
      case GET -> Root / "tables" =>
        loanStats
          .getTables()
          .flatMap { case m => Ok(m) }
      case GET -> Root / "loan-status-count-all-states" =>
        loanStats.getLoanStatusCountAllStates().flatMap { case m =>
          Ok(m.asJson)
        }
      case GET -> Root / "loan-status-count-by-state" / state =>
        loanStats.getLoanStatusCountByState(state).flatMap { case m =>
          Ok(m.asJson)
        }
    }
  }
}

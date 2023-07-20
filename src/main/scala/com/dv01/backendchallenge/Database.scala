package com.dv01.backendchallenge

import doobie.h2.H2Transactor
import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway

object Database {
  private val uri = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"

  /** Use the below uri to persist the H2 database. This allows you to connect
    * to the H2 database from a client and see the same state of the database
    * the application sees.
    */
  // private val uri = "jdbc:h2:tcp://localhost:9092/~/test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"

  def init[F[_]: Async]: Resource[F, H2Transactor[F]] =
    ExecutionContexts
      .fixedThreadPool[F](32)
      .flatMap { ec =>
        H2Transactor.newH2Transactor[F](uri, "sa", "", ec)
      }
      .evalTap { h2tx =>
        h2tx.configure { ds =>
          Async[F].delay(
            Flyway.configure().dataSource(ds).load().migrate()
          )
        }
      }
}

package com.dv01.backendchallenge

import doobie.util.transactor
import cats.effect.MonadCancelThrow
import doobie.implicits._

trait LoanStats[F[_]] {
  def getTables(): F[Map[String, String]]
  def getLoanStatusCountByState(
      state: String
  ): F[Map[String, List[(String, Int)]]]
  def getLoanStatusCountAllStates(): F[Map[String, List[(String, Int)]]]
  def setupTable(): F[Int]
}

case class LoanStatsImpl[F[_]: MonadCancelThrow](
    tx: transactor.Transactor[F]
) extends LoanStats[F] {

  override def setupTable(): F[Int] =
    LoanStatsQuery.setupTable.run.transact(tx)

  override def getTables(): F[Map[String, String]] =
    LoanStatsQuery.getTables.toMap.transact(tx)

  override def getLoanStatusCountAllStates()
      : F[Map[String, List[(String, Int)]]] = {
    val queryAsMap = for {
      tups <- LoanStatsQuery.getLoanStatusCountAllStates
    } yield tups
      .groupBy(_._1)
      .view
      .mapValues(lt => lt.map(t => (t._2, t._3)))
      .toMap
    queryAsMap.transact(tx)
  }

  override def getLoanStatusCountByState(
      state: String
  ): F[Map[String, List[(String, Int)]]] = {
    val queryAsMap = for {
      tups <- LoanStatsQuery.getLoanStatusCountByState(state)
    } yield tups
      .groupBy(_._1)
      .view
      .mapValues(lt => lt.map(t => (t._2, t._3)))
      .toMap
    queryAsMap.transact(tx)
  }
}

package com.dv01.backendchallenge

import doobie.implicits._

object LoanStatsQuery {
  def getTables = {
    sql"show tables".query[(String, String)]
  }

  def setupTable = {
    sql"CREATE TABLE data AS SELECT * FROM CSVREAD('classpath:/LoanStats_securev1_2017Q4.csv')".update
  }

  def getLoanStatusCountByState(state: String) = {
    sql"""
        |SELECT
        |   ADDR_STATE,
        |   LOAN_STATUS,
        |   COUNT(LOAN_STATUS) AS LOAN_STATUS_COUNT
        |FROM
        |   DATA
        |WHERE
        |   ADDR_STATE IN
        |($state)
        |GROUP BY
        |   ADDR_STATE,
        |   LOAN_STATUS
      """.stripMargin
      .query[(String, String, Int)]
      .stream
      .compile
      .toList
  }

  def getLoanStatusCountAllStates = {
    sql"""
          |SELECT
          |    ADDR_STATE,
          |    LOAN_STATUS,
          |    COUNT(*) AS LOAN_STATUS_COUNT
          |FROM
          |    DATA
          |WHERE ADDR_STATE IS NOT NULL
          |GROUP BY
          |    ADDR_STATE,
          |    LOAN_STATUS;
      """.stripMargin
      .query[(String, String, Int)]
      .stream
      .compile
      .toList
  }
}

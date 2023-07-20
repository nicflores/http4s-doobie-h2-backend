CREATE TABLE DATA AS
SELECT
  *
FROM
  CSVREAD('classpath:/LoanStats_securev1_2017Q4.csv');

CREATE TABLE DATA AS
SELECT
  *
FROM
  CSVREAD('/Users/nicandroflores/Documents/github/dv01-backend-challenge/src/main/resources/LoanStats_securev1_2017Q4.csv');

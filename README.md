## Backend Challenge Assignment

### Project Setup
Within the directory of this project you must run:

`./setup.sh`

This will unzip the csv data file. Since it was bigger than 100M github wouldn't let me check it in.

### Running the project
From within the project execute:

`sbt run`

In another terminal window execute curl commands, for example:

`curl -vvv http://0.0.0.0:8080/api/v1/loan-status-count-by-state/CA`

and

`curl -vvv http://0.0.0.0:8080/api/v1/loan-status-count-all-states`

To stop the project from running just `ctrl-C`. This will automatically clean up the in memory database.

### The Tech Stack
This project is built using the following libraries:

`cats-effect`: Used to handle the `IO` effect system and manage the various resoures used in the project.
`http4s`: Used to design the the http server and its routes.
`circe`: Used for json serialization.
`fs2`: Used to stream data instead of caching everything in memeory and then sending it over the wire.
`doobie`: Used to define safe sql queries and to interact with the database.
`flyway`: Used to populate the in memory database at start up time.
`H2`: In memory database used to query LoadStats csv data.

### Api Documentation

`GET - /api/v1/tables`: Mainly used for debugging. Can be used to check the memory H2 database contains the `DATA` table needed for the rest of the other endpoints to execute properly.

Response Sample:

200
```json
{"DATA":"PUBLIC","flyway_schema_history":"PUBLIC"}
```

`GET - /api/v1/loan-status-count-by-state/{two-char-state-abbreviation}`: Returns loan type counts given a specific two character abbreviation of a US State (in upper case format).

Response Sample:

200
```json
{
  "KY": [
    ["Charged Off",114],
    ["Current",552],
    ["Fully Paid",427],
    ["In Grace Period",8],
    ["Late (16-30 days)",1],
    ["Late (31-120 days)",17]
  ]
}
```

`GET - /api/v1/loan-status-count-all-states`: Returns loan type counts for all US States.

200
```json
{
  "NM": [
    ["Charged Off",78],
    ["Current",279],
    ["Fully Paid",218],
    ["Late (31-120 days)",9]
  ],
  "OR": [
    ["Charged Off",110],
    ["Current",646],
    ["Fully Paid",573],
    ["In Grace Period",8],
    ["Late (16-30 days)",4],
    ["Late (31-120 days)",12]
  ],
  .
  .
  .
```


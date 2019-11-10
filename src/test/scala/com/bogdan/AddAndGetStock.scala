package com.bogdan

import io.gatling.core.Predef.{jsonPath, _}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddAndGetStock extends BaseSimulation {

  private def randomNumber() = scala.util.Random.nextInt(Integer.MAX_VALUE)

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .contentTypeHeader("application/json")
  val scn: ScenarioBuilder = scenario("Add and Get Stock")
    .exec(http("addStock")
      .post("/api/stocks")
      .body(StringBody(session => s"""{ "name": "name_${randomNumber()}", "currentPrice": 1.2345 }"""))
      .check(
        status is 201,
        headerRegex("Location", "/api/stocks/(.*)").ofType[String].saveAs("stockId"),
        jsonPath("$..name").ofType[String].saveAs("name")
      ))
    .exec(http("getStock")
      .get("/api/stocks/${stockId}")
      .check(
        status is 200,
        jsonPath("$..name") is "${name}"
      ))

  setUp(scn.inject(
    rampUsersPerSec(10) to 200 during (2 minutes)
  )).protocols(httpProtocol)
}
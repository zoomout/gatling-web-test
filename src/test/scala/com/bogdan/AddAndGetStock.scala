package com.bogdan

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddAndGetStock extends BaseSimulation {

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .contentTypeHeader("application/json")

  val scn: ScenarioBuilder = scenario("Add and Get Stock")
    .exec(http("addStock")
      .post("/api/stocks")
      .body(RawFileBody("payload/newStock.json"))
      .check(
        status is 201,
        headerRegex("Location", "/api/stocks/(.*)").ofType[String].saveAs("stockId")
      ))
    .exec(http("getStock")
      .get("/api/stocks/${stockId}")
      .check(
        status is 200
      ))

  setUp(scn.inject(
    rampUsersPerSec(10) to 200 during (2 minute)
  )).protocols(httpProtocol)
}
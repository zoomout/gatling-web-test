package com.bogdan

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddAndGetStock extends BaseSimulation {

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .contentTypeHeader("application/json")
  val name = s"name_${scala.util.Random.nextInt(1000000)}"
  val scn: ScenarioBuilder = scenario("Add and Get Stock")
    .exec(http("addStock")
      .post("/api/stocks")
      .body(StringBody("""{ "name": """" + name + """", "currentPrice": 1.22 }"""))
      .check(
        status is 201,
        headerRegex("Location", "/api/stocks/(.*)").ofType[String].saveAs("stockId")
      ))
    .exec(http("getStock")
      .get("/api/stocks/${stockId}")
      .check(
        status is 200,
        jsonPath("$..name") is name
      ))

  setUp(scn.inject(
    rampUsersPerSec(10) to 200 during (2 minutes)
  )).protocols(httpProtocol)
}
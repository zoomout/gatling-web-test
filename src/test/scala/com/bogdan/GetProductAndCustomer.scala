package com.bogdan

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class GetProductAndCustomer extends BaseSimulation {

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .contentTypeHeader("application/json")
    .shareConnections

  val scn: ScenarioBuilder = scenario("GetProductAndCustomer")
    .exec(http("getProduct")
      .get("/products/1")
      .check(
        status is 200
      ))
    .exec(http("getCustomer")
      .get("/customers/1")
      .check(
        status is 200
      )
    )

  setUp(scn.inject(
    rampUsersPerSec(10) to 1000 during (60 seconds),
  )).protocols(httpProtocol)
}
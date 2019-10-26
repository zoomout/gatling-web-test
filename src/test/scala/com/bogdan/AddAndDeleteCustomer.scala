package com.bogdan

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddAndDeleteCustomer extends BaseSimulation {

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .contentTypeHeader("application/json")

  val scn: ScenarioBuilder = scenario("AddAndDeleteCustomer")
    .exec(http("addCustomer")
      .post("/customers")
      .body(RawFileBody("payload/newCustomer.json"))
      .check(
        status is 201,
        headerRegex("Location", "/customers/(.*)").ofType[String].saveAs("customerId")
      ))
    .exec(http("deleteCustomer")
      .delete("/customers/${customerId}")
      .check(
        status is 204
      )
    )

  setUp(scn.inject(
    rampUsersPerSec(10) to 100 during (1 minute)
  )).protocols(httpProtocol)
}
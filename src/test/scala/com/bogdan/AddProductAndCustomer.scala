package com.bogdan

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddProductAndCustomer extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:10080")
    .contentTypeHeader("application/json")

  val scn: ScenarioBuilder = scenario("AddProductAndCustomer")
    .exec(http("addProduct")
      .post("/products")
      .body(RawFileBody("payload/newProduct.json"))
      .check(
        status is 201
      ))
    .exec(http("addCustomer")
      .post("/customers")
      .body(RawFileBody("payload/newCustomer.json"))
      .check(
        status is 201
      )
    )

  setUp(scn.inject(
    constantUsersPerSec(100) during (1 minute)
  )).protocols(httpProtocol)
}
package com.bogdan

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddAndDeleteProduct extends BaseSimulation {

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .contentTypeHeader("application/json")

  val scn: ScenarioBuilder = scenario("AddAndDeleteProduct")
    .exec(http("addProduct")
      .post("/products")
      .body(RawFileBody("payload/newProduct.json"))
      .check(
        status is 201,
        headerRegex("Location", "/products/(.*)").ofType[String].saveAs("productId")
      ))
    .exec(http("deleteProduct")
      .delete("/products/${productId}")
      .check(
        status is 204
      )
    )

  setUp(scn.inject(
    rampUsersPerSec(10) to 100 during (1 minute)
  )).protocols(httpProtocol)
}
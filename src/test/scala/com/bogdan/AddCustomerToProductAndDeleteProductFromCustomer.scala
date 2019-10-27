package com.bogdan

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddCustomerToProductAndDeleteProductFromCustomer extends BaseSimulation {

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .contentTypeHeader("application/json")

  val scn: ScenarioBuilder = scenario("AddCustomerToProduct_And_DeleteProductFromCustomer")
    .exec(http("addCustomer")
      .post("/customers")
      .body(RawFileBody("payload/newCustomer.json"))
      .check(
        status is 201,
        headerRegex("Location", "/customers/(.*)").ofType[String].saveAs("customerId")
      ))
    .exec(http("addProduct")
      .post("/products")
      .body(RawFileBody("payload/newProduct.json"))
      .check(
        status is 201,
        headerRegex("Location", "/products/(.*)").ofType[String].saveAs("productId")
      ))
    .exec(http("addCustomerToProduct")
      .put("/products/${productId}/customers")
      .body(StringBody("[{\"id\":\"${customerId}\"}]"))
      .check(
        status is 204
      )
    )
    .exec(http("deleteProductFromCustomer")
      .delete("/customers/${customerId}/products")
      .body(StringBody("[{\"id\":\"${productId}\"}]"))
      .check(
        status is 204
      )
    )

  setUp(scn.inject(
    rampUsersPerSec(10) to 200 during (1 minute)
  )).protocols(httpProtocol)
}
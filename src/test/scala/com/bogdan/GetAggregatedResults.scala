package com.bogdan

import io.gatling.core.Predef.{jsonPath, _}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class GetAggregatedResults extends BaseSimulation {

  private def randomNumber() = scala.util.Random.nextInt(Integer.MAX_VALUE)

  private val httpProtocol = http
    .baseUrl(baseUrl)

  val scn: ScenarioBuilder = scenario("Get Aggregated Results")
    .exec(http("get aggregation")
      .get("/aggregation?pricing=A,B,C,D,E&track=1,2,3,4,5&shipments=1,11,111,12,13")
      .check(
        status is 200,
        jsonPath("$..pricing.A").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.B").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.C").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.D").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.E").ofType[Double].gte("0.0"),
        jsonPath("$..track.1").notNull,
        jsonPath("$..track.2").notNull,
        jsonPath("$..track.3").notNull,
        jsonPath("$..track.4").notNull,
        jsonPath("$..track.5").notNull,
        jsonPath("$..shipments.11[*]").count.is(1),
        jsonPath("$..shipments.12[*]").count.is(2),
        jsonPath("$..shipments.111[*]").count.is(1),
        jsonPath("$..shipments.1[*]").count.is(1),
        jsonPath("$..shipments.13[*]").count.is(3),
      ))

  setUp(scn.inject(
    rampUsersPerSec(1) to 5 during (30 seconds)
  )).protocols(httpProtocol)
}
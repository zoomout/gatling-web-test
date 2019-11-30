package com.bogdan

import io.gatling.core.Predef.{jsonPath, _}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class GetAggregatedResults extends BaseSimulation {

  private def randomNumber() = 5+scala.util.Random.nextInt(11)

  private val httpProtocol = http
    .baseUrl(baseUrl)

  val scn: ScenarioBuilder = scenario("Get Aggregated Results")
    .exec(http("get only pricing")
      .get(session => s"""/aggregation?pricing=A,B,C,D,E""")
      .check(
        status is 200,
        jsonPath("$..pricing.A").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.B").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.C").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.D").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.E").ofType[Double].gte("0.0"),
        jsonPath("$..track.1").notExists,
        jsonPath("$..track.2").notExists,
        jsonPath("$..track.3").notExists,
        jsonPath("$..track.4").notExists,
        jsonPath("$..track.5").notExists,
        jsonPath("$..shipments.1").notExists,
        jsonPath("$..shipments.2").notExists,
        jsonPath("$..shipments.3").notExists,
        jsonPath("$..shipments.4").notExists,
        jsonPath("$..shipments.5").notExists,
      ))
    .exec(http("get only track")
      .get(session => s"""/aggregation?track=1,2,3,4,5""")
      .check(
        status is 200,
        jsonPath("$..pricing.A").notExists,
        jsonPath("$..pricing.B").notExists,
        jsonPath("$..pricing.C").notExists,
        jsonPath("$..pricing.D").notExists,
        jsonPath("$..pricing.E").notExists,
        jsonPath("$..track.1").exists,
        jsonPath("$..track.2").exists,
        jsonPath("$..track.3").exists,
        jsonPath("$..track.4").exists,
        jsonPath("$..track.5").exists,
        jsonPath("$..shipments.1").notExists,
        jsonPath("$..shipments.2").notExists,
        jsonPath("$..shipments.3").notExists,
        jsonPath("$..shipments.4").notExists,
        jsonPath("$..shipments.5").notExists,
      ))
    .exec(http("get only shipments")
      .get(session => s"""/aggregation?shipments=1,2,3,4,5""")
      .check(
        status is 200,
        jsonPath("$..pricing.A").notExists,
        jsonPath("$..pricing.B").notExists,
        jsonPath("$..pricing.C").notExists,
        jsonPath("$..pricing.D").notExists,
        jsonPath("$..pricing.E").notExists,
        jsonPath("$..track.1").notExists,
        jsonPath("$..track.2").notExists,
        jsonPath("$..track.3").notExists,
        jsonPath("$..track.4").notExists,
        jsonPath("$..track.5").notExists,
        jsonPath("$..shipments.1[*]").count.is(1),
        jsonPath("$..shipments.2[*]").count.is(2),
        jsonPath("$..shipments.3[*]").count.is(3),
        jsonPath("$..shipments.4[*]").count.is(4),
        jsonPath("$..shipments.5[*]").count.is(5),
      ))
    .exec(http("get all aggregation")
      .get(session => s"""/aggregation?pricing=A,B,C,D,E&track=1,2,3,4,5&shipments=1,2,3,4,5""")
      .check(
        status is 200,
        jsonPath("$..pricing.A").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.B").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.C").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.D").ofType[Double].gte("0.0"),
        jsonPath("$..pricing.E").ofType[Double].gte("0.0"),
        jsonPath("$..track.1").exists,
        jsonPath("$..track.2").exists,
        jsonPath("$..track.3").exists,
        jsonPath("$..track.4").exists,
        jsonPath("$..track.5").exists,
        jsonPath("$..shipments.1[*]").count.is(1),
        jsonPath("$..shipments.2[*]").count.is(2),
        jsonPath("$..shipments.3[*]").count.is(3),
        jsonPath("$..shipments.4[*]").count.is(4),
        jsonPath("$..shipments.5[*]").count.is(5),
      ))

  setUp(scn.inject(
    rampUsersPerSec(1) to 100 during (10 seconds)
  )).protocols(httpProtocol)
}
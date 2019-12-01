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
        jsonPath("$..pricing.*").count.is(5),
        jsonPath("$..pricing.A").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.B").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.C").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.D").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.E").ofType[Double].gte("0.01"),
        jsonPath("$..track").isNull,
        jsonPath("$..shipments").isNull,
      ))
    .exec(http("get only track")
      .get(session => s"""/aggregation?track=1,2,3,4,5""")
      .check(
        status is 200,
        jsonPath("$..pricing").isNull,
        jsonPath("$..track.*").count.is(5),
        jsonPath("$..track.1").exists,
        jsonPath("$..track.2").exists,
        jsonPath("$..track.3").exists,
        jsonPath("$..track.4").exists,
        jsonPath("$..track.5").exists,
        jsonPath("$..shipments").isNull,
      ))
    .exec(http("get only shipments")
      .get(session => s"""/aggregation?shipments=1,2,3,4,5""")
      .check(
        status is 200,
        jsonPath("$..pricing").isNull,
        jsonPath("$..track").isNull,
        jsonPath("$..shipments.*").count.is(5),
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
        jsonPath("$..pricing.*").count.is(5),
        jsonPath("$..pricing.A").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.B").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.C").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.D").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.E").ofType[Double].gte("0.01"),
        jsonPath("$..track.1").exists,
        jsonPath("$..track.2").exists,
        jsonPath("$..track.3").exists,
        jsonPath("$..track.4").exists,
        jsonPath("$..track.5").exists,
        jsonPath("$..shipments.*").count.is(5),
        jsonPath("$..shipments.1[*]").count.is(1),
        jsonPath("$..shipments.2[*]").count.is(2),
        jsonPath("$..shipments.3[*]").count.is(3),
        jsonPath("$..shipments.4[*]").count.is(4),
        jsonPath("$..shipments.5[*]").count.is(5),
      ))
    .exec(http("get overlapping partial pricing A,B,C")
      .get(session => s"""/aggregation?pricing=A,B,C""")
      .check(
        status is 200,
        jsonPath("$..pricing.A").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.B").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.C").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.D").notExists,
        jsonPath("$..pricing.E").notExists,
        jsonPath("$..track").isNull,
        jsonPath("$..shipments").isNull,
      ))
    .exec(http("get overlapping partial pricing C,D,E")
      .get(session => s"""/aggregation?pricing=C,D,E""")
      .check(
        status is 200,
        jsonPath("$..pricing.A").notExists,
        jsonPath("$..pricing.B").notExists,
        jsonPath("$..pricing.C").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.D").ofType[Double].gte("0.01"),
        jsonPath("$..pricing.E").ofType[Double].gte("0.01"),
        jsonPath("$..track").isNull,
        jsonPath("$..shipments").isNull,
      ))

  setUp(scn.inject(
    rampUsersPerSec(1) to 50 during (10 seconds)
  )).protocols(httpProtocol)
}
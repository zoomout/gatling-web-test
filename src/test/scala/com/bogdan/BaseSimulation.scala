package com.bogdan

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._

class BaseSimulation extends Simulation {
  val baseUrl: String = ConfigFactory.load().getString("baseUrl")
}

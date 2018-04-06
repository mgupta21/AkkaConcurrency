package org.scala.akka.avionics

import akka.actor.SupervisorStrategy._
import akka.actor.{AllForOneStrategy, OneForOneStrategy, SupervisorStrategy}

import scala.concurrent.duration.Duration

trait SupervisionStrategyFactory {
  def makeStrategy(maxNrRetries: Int, withinTimeRange: Duration)(decider: Decider): SupervisorStrategy
}

trait OneForOneStrategyFactory extends SupervisionStrategyFactory {
  def makeStrategy(maxNrRetries: Int, withinTimeRange: Duration)(decider: Decider): SupervisorStrategy = OneForOneStrategy(maxNrRetries, withinTimeRange)(decider)
}

trait AllForOneStrategyFactory extends SupervisionStrategyFactory {
  def makeStrategy(maxNrRetries: Int, withinTimeRange: Duration)(decider: Decider): SupervisorStrategy = AllForOneStrategy(maxNrRetries, withinTimeRange)(decider)
}
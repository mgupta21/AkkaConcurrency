package org.scala.akka.avionics

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{ActorInitializationException, ActorKilledException}

import scala.concurrent.duration._

abstract class IsolatedStopSupervisor(maxNrRetries: Int = -1, withinTimeRange: Duration = Duration.Inf) extends IsolatedLifeCycleSupervisor {
  this: SupervisionStrategyFactory =>

  override val supervisorStrategy = makeStrategy(
    maxNrRetries, withinTimeRange) {
    case _: ActorInitializationException => Stop
    case _: ActorKilledException => Stop
    case _: Exception => Stop
    case _ => Escalate
  }
}


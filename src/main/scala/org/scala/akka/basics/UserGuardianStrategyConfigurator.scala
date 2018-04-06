package org.scala.akka.basics

import akka.actor.SupervisorStrategy._
import akka.actor.{OneForOneStrategy, SupervisorStrategyConfigurator}

// if the user guardian's children throw an exception, by default they're going to restart but this can be modified by a change to the configuration.
class UserGuardianStrategyConfigurator
  extends SupervisorStrategyConfigurator {
  def create(): SupervisorStrategy = {
    OneForOneStrategy() {
      case _ => Resume
    }
  }
}


// Add below to configuration file
/*
akka {
  actor {
  guardian-supervisor-strategy =
  zzz.akka.UserGuardianStrategyConfigurator
}
}*/

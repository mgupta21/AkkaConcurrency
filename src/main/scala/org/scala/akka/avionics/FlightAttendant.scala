package org.scala.akka.avionics

import akka.actor.Actor

import scala.concurrent.duration._

// This trait allows us to create different Flight Attendants with different levels of responsiveness.
trait AttendantResponsiveness {
  val maxResponseTimeMS: Int

  def responseDuration =
    scala.util.Random.nextInt(maxResponseTimeMS).millis
}

object FlightAttendant {

  val maxResponseTimeMS = 300000

  case class GetDrink(drinkname: String)

  case class Drink(drinkname: String)

  // By default we will make attendants that respond within 5 minutes
  // companion object provides the default abstract val
  def apply() = new FlightAttendant with AttendantResponsiveness {
    val maxResponseTimeMS = 300000
  }
}

class FlightAttendant extends Actor {
  // FlightAttendant self-types to the AttendantResponsiveness, but the abstract val maxResponseTimeMS is not defined here
  // Scala looks to companion object to resolve it
  this: AttendantResponsiveness =>

  import FlightAttendant._

  // bring the execution context into implicit scope for the scheduler below
  implicit val ec = context.dispatcher

  def receive = {
    case GetDrink(drinkname) =>
      // We don't respond right away, but use the scheduler to ensure we do eventually
      // context.system helps us access the actor context access the system
      context.system.scheduler.scheduleOnce(responseDuration, sender, Drink(drinkname))
  }
}

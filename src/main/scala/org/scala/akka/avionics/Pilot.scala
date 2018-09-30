package org.scala.akka.avionics

import akka.actor.{Actor, ActorRef}

// The flight attendants are direct children of the lead flight attendant that created them, 
// but the pilots will discover each other when we've reached a stable point after creation.
object Pilots {

  case object ReadyToGo

  case object RelinquishControl

  case object Controls

}

class Pilot extends Actor {

  import Pilots._
  import Plane._

  var controls: ActorRef = context.system.deadLetters
  var copilot: ActorRef = context.system.deadLetters
  var autopilot: ActorRef = context.system.deadLetters
  val copilotName = context.system.settings.config.getString("zzz.akka.avionics.flightcrew.copilotName")

  // To cement the pilot relationships, therefore, we'll need to look them up using the actor paths Akka creates for us. 
  // Although we can find actors using either the ActorContext or ActorSystem, in the case of the pilots, we'll use the ActorContext.
  def receive = {
    case ReadyToGo =>
      context.parent ! GiveMeControl
      // it can ask the actor's context for its siblings
      copilot = context.child("../" + copilotName).get
      autopilot = context.child("../Autopilot").get
    case Controls(controlSurfaces) =>
      controls = controlSurfaces
  }
}

// The copilot is very similar to the pilot, except that he has no interest in grabbing the plane's controls when it's ready to go.
class Copilot extends Actor {

  import Pilots._

  var controls: ActorRef = context.system.deadLetters
  var pilot: ActorRef = context.system.deadLetters
  var autopilot: ActorRef = context.system.deadLetters
  val pilotName = context.system.settings.config.getString("zzz.akka.avionics.flightcrew.pilotName")

  // Due to the real-life aspects of concurrency, Akka has made a conscious decision to return an ActorRef for all actorFor requests, 
  // regardless of whether or not it can find the actor instance for the request; it will never throw an exception.
  // context.child("../" + doesntExist).get
  def receive = {
    case ReadyToGo =>
      pilot = context.child("../" + pilotName).get
      autopilot = context.child("../Autopilot").get
  }
}

class Autopilot extends Actor {
  override def receive: Receive = Actor.emptyBehavior
}

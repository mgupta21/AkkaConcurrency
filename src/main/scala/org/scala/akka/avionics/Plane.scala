package org.scala.akka.avionics

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.scala.akka.avionics.Altimeter.AltitudeUpdate

object Plane {

  // Returns the control surface to the Actor that asks for them
  case object GiveMeControl

  case class Controls(controls: ActorRef)

}

// We want the Plane to own the Altimeter and we're going to do that by passing in a specific factory we can use to build the Altimeter
class Plane extends Actor with ActorLogging {

  import Plane._

  val cfgstr = "zzz.akka.avionics.flightcrew"

  // to create a supervised child actor from within an actor use context.actorOf
  // to create a top level actor from the ActorSystem use system.actorOf
  // Since we are using context.actorOf to create actors instead of system.actorOf these actors are child of plane actor
  val altimeter = context.actorOf(Props(Altimeter()), "Altimeter")
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")
  val config = context.system.settings.config
  val pilot = context.actorOf(Props[Pilot], config.getString(s"$cfgstr.pilotName"))
  val copilot = context.actorOf(Props[Copilot], config.getString(s"$cfgstr.copilotName"))
  val autopilot = context.actorOf(Props[Autopilot], "Autopilot")
  val flightAttendant = context.actorOf(Props(LeadFlightAttendant()), config.getString(s"$cfgstr.leadAttendantName"))

  def receive = {
    case GiveMeControl =>
      log info ("Plane giving control.")
      // Bad idea to directly send ActorRef, wrap it in the case class
      sender ! Controls(controls)
    case AltitudeUpdate(altitude) =>
      log info (s"Altitude is now: $altitude")
  }

  override def preStart() {
    // Register ourself with the Altimeter to receive updates
    // on our altitude
    altimeter ! EventSource.RegisterListener(self)
    List(pilot, copilot) foreach {
      _ ! Pilots.ReadyToGo
    }
  }

}

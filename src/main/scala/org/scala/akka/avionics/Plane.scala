package org.scala.akka.avionics

import akka.actor.{Actor, ActorLogging, Props}
import org.scala.akka.avionics.Altimeter.AltitudeUpdate
import org.scala.akka.avionics.EventSource.RegisterListener

object Plane {

  // Returns the control surface to the Actor that asks for them
  case object GiveMeControl

}

// We want the Plane to own the Altimeter and we're going to do that by passing in a specific factory we can use to build the Altimeter
class Plane extends Actor with ActorLogging {

  import Plane._

  // to create a supervised child actor from within an actor use context.actorOf
  // to create a top level actor from the ActorSystem use system.actorOf
  /*val altimeter = context.actorOf(
    Props[Altimeter], "Altimeter")*/
  val altimeter = context.actorOf(
    Props(Altimeter()), "Altimeter")

  val controls = context.actorOf(
    Props(new ControlSurfaces(altimeter)), "ControlSurfaces")

  def receive = {
    case GiveMeControl =>
      log info ("Plane giving control.")
      sender ! controls
    case AltitudeUpdate(altitude) =>
      log info (s"Altitude is now: $altitude")
  }

  override def preStart() {
    altimeter ! RegisterListener(self)
  }

}

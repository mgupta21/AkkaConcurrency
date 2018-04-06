package org.scala.akka.avionics

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.scala.akka.avionics.Altimeter.AltitudeUpdate
import org.scala.akka.avionics.IsolatedLifeCycleSupervisor.WaitForStart

import scala.concurrent.Await
import scala.concurrent.duration._

object Plane {

  // Returns the control surface to the Actor that asks for them
  case object GiveMeControl

  case class Controls(controls: ActorRef)

}

// We want the Plane to own the Altimeter and we're going to do that by passing in a specific factory we can use to build the Altimeter
class Plane extends Actor with ActorLogging {

  this: AltimeterProvider with PilotProvider with LeadFlightAttendantProvider =>

  import Plane._

  val cfgstr = "zzz.akka.avionics.flightcrew"

  // to create a supervised child actor from within an actor use context.actorOf
  // to create a top level actor from the ActorSystem use system.actorOf
  // Since we are using context.actorOf to create actors instead of system.actorOf these actors are child of plane actor

  val altimeter = context.actorOf(Props(newAltimeter), "Altimeter")
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")
  val autopilot = context.actorOf(Props(newAutopilot), "Autopilot")

  // There's going to be a couple of asks below and a timeout is necessary for that.
  implicit val askTimeout = Timeout(1.second)

  def startEquipment() {
    val controls = context.actorOf(Props(new IsolatedResumeSupervisor with OneForOneStrategyFactory {
      def childStarter() {
        val alt = context.actorOf(Props(newAltimeter), "Altimeter")
        // These children get implicitly added to the hierarchy
        context.actorOf(Props(newAutopilot), "Autopilot")
        context.actorOf(Props(new ControlSurfaces(alt)), "ControlSurfaces")
      }
    }), "Equipment")
    // starting actors is an asynchronous process, hence wait
    Await.result(controls ? WaitForStart, 1.second)
  }

  def startPeople() {
    val people = context.actorOf(Props(new IsolatedStopSupervisor with OneForOneStrategyFactory {
      def childStarter() {
        // These children get implicitly added to the hierarchy
        context.actorOf(Props(newPilot), config.getString(s"$cfgstr.pilotName"))
        context.actorOf(Props(newCopilot), config.getString(s"$cfgstr.copilotName"))
      }
    }), "Pilots")
    // Use the default strategy here, which restarts indefinitely
    context.actorOf(Props(newLeadFlightAttendant), config.getString(s"$cfgstr.leadAttendantName"))
    Await.result(people ? WaitForStart, 1.second)
  }

  val config = context.system.settings.config
  val pilot = context.actorOf(Props(newPilot), config.getString(s"$cfgstr.pilotName"))
  val copilot = context.actorOf(Props(newCopilot), config.getString(s"$cfgstr.copilotName"))
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

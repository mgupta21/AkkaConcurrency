package org.scala.akka.avionics

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
// The futures created by the ask syntax need an execution context on which to run,
// and we will use the default global instance for that context
import scala.concurrent.ExecutionContext.Implicits.global

object Avionics {
  // needed for '?' below
  implicit val timeout = Timeout(5.seconds)

  import ControlSurfaces._

  val system = ActorSystem("PlaneSimulation")
  val plane = system.actorOf(Props[Plane], "Plane")

  def main(args: Array[String]) {
    // Grab the controls a.k.a ActorRef of ControlSurfaces
    // the future only knows about the type that is returned as an Any, we must use the future's mapTo facility to coerce it down to the type we're expecting
    val control: ActorRef = Await.result((plane ? Plane.GiveMeControl).mapTo[ActorRef], 5.seconds)

    // Takeoff!
    system.scheduler.scheduleOnce(200.millis) {
      control ! StickBack(1f)
    }

    // Level out
    system.scheduler.scheduleOnce(2.seconds) {
      control ! StickBack(0f)
    }
    // Climb
    system.scheduler.scheduleOnce(3.seconds) {
      control ! StickBack(0.5f)
    }
    // Level out
    system.scheduler.scheduleOnce(4.seconds) {
      control ! StickBack(0f)
    }
    // Shut down
    system.scheduler.scheduleOnce(5.seconds) {
      system.terminate()
    }
  }
}

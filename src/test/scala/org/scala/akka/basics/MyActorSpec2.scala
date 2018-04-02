package org.scala.akka.basics

import akka.actor.{ActorRef, ActorSystem, Props}
import org.scalatest._

// we've gained full isolation between tests and the ability to run our tests completely in parallel
// The ActorSys is a fine spot for definition of a shared fixture.  For example, instead of having mutable data at the spec level, you could put it in the ActorSys and let the constructor initialize the data appropriately.
class MyActorSpec2 extends WordSpec with MustMatchers with ParallelTestExecution {

  def makeActor(sys: ActorSystem): ActorRef = sys.actorOf(Props[MyActor], "MyActor")

  "My Actor" should {
    "throw when made with the wrong name" in new ActorSys {
      val a = system.actorOf(Props[MyActor])
    }

    "contruct without exception" in new ActorSys {
      val a = makeActor(system)
      // The throw will cause the test to fail
    }

    "respond with a Pong to a Ping" in new ActorSys {
      val a = makeActor(system)
    }
  }

}

package org.scala.akka.basics

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.fixture

// this is a helper class that will handle the ActorSystem to get true isolation between tests

object ActorSys {
  val uniqueId = new AtomicInteger(0)
}

class ActorSys(name: String) extends TestKit(ActorSystem(name)) with ImplicitSender with fixture.NoArg {

  // It hides the ActorSystem from us and gives us a no-arg constructor that takes no parameters.
  def this() = this("TestSystem%05d".format(ActorSys.uniqueId.getAndIncrement()))

  def shutdown(): Unit = system.terminate()

  // move test code out of the constructor and into a delayedInit function called by super.apply (defined in NoArg).
  // We now no longer need to worry about shutting down the ActorSystem after all of the tests are complete.
  // In this context, there's only one test, and we can therefore shut the ActorSystem down after that test completes.
  override def apply() {
    try super.apply()
    finally shutdown()
  }
}

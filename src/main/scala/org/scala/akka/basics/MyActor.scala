package org.scala.akka.basics

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, OneForOneStrategy, Props, Terminated}

import scala.concurrent.duration._

// Gamma g = new Gamma("string")
case class Gamma(g: String)

case class Beta(b: String, g: Gamma)

case class Alpha(b1: Beta, b2: Beta)

abstract class SomeOtherActor extends Actor {}

class MyActor extends Actor {

  // This is default preRestart and postRestart implementation
  // Once an actor is restarted after injury, in preRestart its children will be stopped by default. Its children then can
  // be constructed in the constructor of the actor or the postRestart method will be called on this new actor instance and If you're creating children in your preStart method, they will get created at this time.

  override def preRestart(reason: Throwable, message: Option[Any]) {
    context.children foreach context.stop
    postStop()
  }

  override def postRestart(reason: Throwable) {
    preStart()
  }

  override def preStart(): Unit = {
    // Perform any initialization setup here
    // Often this is a good spot to send yourself a message
    // such as: self ! Initialize

    //An actor can watch any other actor for death. When an actor being watched (someOtherActor) dies, then the Terminated case of the actor watching will be processed
    // SomeOtherActor is child of MyActor
    context.watch(context.actorOf(Props[SomeOtherActor]))

  }

  override def postStop(): Unit = {
    // Perform any cleanup here.  The message pump is shut down
    // so any message you send to yourself will only go to the
    // dead letter office, but if you'd like to clean up any
    // resources, such as Database sessions, now's the time to
    // do it.
  }

  override val supervisorStrategy =
    OneForOneStrategy(5, 1 minute) {
      case _ => Restart
    }

  def receive2 = {
    // Do your usual processing here.  For example:
    //case Initialize =>
    // Call your own post start initialization function here
    //postStartInitialization()

    // When terminated message is processed, and the dead actor's ActorRef will be included in the message
    // The Terminated(child) message will come from one of two situations:
    //1. The child finally gave up its ghost due to exceptions.
    //2. The child was specifically stopped during this actor's restart.
    // When the second case presents itself to this piece of code, the terminated child has already been replaced by MyActor's constructor, so re-creating it is a very bad idea. Every time we restart, we create yet another child.
    case Terminated(deadActor) =>
      println(deadActor.path.name + " has died")
    // re-create the failed child <-- BUG (Solved in MyActor2)
  }

  def receive = {
    // Literal String match
    case "Hello" =>
      println("Hi")

    // Literal Int match
    case 42 =>
      println("I don't know the question." +
        "Go ask the Earth Mark II.")

    // Matches any string at all
    case s: String =>
      println(s"You sent me a string: $s")

    // Match a more complex case class structure
    case Alpha(Beta(b1, Gamma(g1)), Beta(b2, Gamma(g2))) =>
      println(s"beta1: $b1, beta2: $b2, gamma1: $g1, gamma2: $g2")

    // Catch all. Matches any message type
    case _ =>
      println("Huh?")
  }
}

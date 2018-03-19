package org.scala.akka.basics

import akka.actor.{ActorSystem, Props}

object BadShakespeareanMain extends App {
  val system = ActorSystem("BadShakespearean")
  // The Props class lets us modify some of the structure that surrounds the actor, such as its execution context
  val actorRef = system.actorOf(Props[BadShakespeareanActor], "BadShake")
  actorRef ! "Good Morning"
  actorRef ! "You're terrible"
  system.terminate()
}

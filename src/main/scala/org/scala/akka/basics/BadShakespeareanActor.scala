package org.scala.akka.basics

import akka.actor.Actor

class BadShakespeareanActor extends Actor {
  override def receive = {
    case "Good Morning" => println("Hello Mayank")
    case "You're terrible" => println("Yup")
  }
}

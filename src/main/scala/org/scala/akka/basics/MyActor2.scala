package org.scala.akka.basics

import akka.actor.{Actor, Terminated}

//We have successfully saved the children. They aren't going to be stopped as part of this actor's restart life cycle.
// The old behaviour of preRestart was to stop the children, which we've now removed.
// We've also ensured that when MyActor is truly started (i.e., not restarted) that its children get created, but when it is restarted they don't.
// Note : Any child that survives the actor's preRestart gets restarted.
// This is different than having a child stopped and then recreated, of course, since the ActorRef for that child remains the same.
class MyActor2 extends Actor {
  def initialize() {
    // Do your initialization here
  }

  override def preStart() {
    initialize()
    // Start your children here
  }

  override def preRestart(reason: Throwable,
                          message: Option[Any]) {
    // The default behaviour was to stop the children
    // here but we don't want to do that

    // We still want to postStop() however.
    postStop()
  }

  override def postRestart(reason: Throwable) {
    // The default behaviour was to call preStart()
    // but we don't want to do that, since that's
    // where children get started
    initialize()
  }

  def receive = {
    case Terminated(child) =>
    // re-create the failed child.  Now it's OK,
    // since the only reason we can get this message
    // is because the child really died without
    // our help
  }
}

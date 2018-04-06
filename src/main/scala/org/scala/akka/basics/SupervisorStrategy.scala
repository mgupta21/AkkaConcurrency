package org.scala.akka.basics

class SupervisorStrategy {

  import akka.actor.SupervisorStrategy._
  import akka.actor.{ActorInitializationException, ActorKilledException, OneForOneStrategy}

  // OneForOneStrategy : the decision made regarding an actor's failure will apply only to that one failed actor
  // AllForOneStrategy : applies the decision regarding a single  actor's failure to all children.
  // This is the default strategy of the parent actor which can be overridden and customized
  final val supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: ActorKilledException => Stop
    case _: Exception => Restart
    case _ => Escalate
  }

  // the OneForOneStrategy and AllForOneStrategy accept a restart threshold on its constructor that says just how many restarts, within a specific range of time, it's willing to tolerate.
  // For the Duration DSL
  // Akka will keep track of how many restarts have been called within the range of time specified, and as soon as it goes over the threshold the actor will stop and its postStop method will be called.
  // override val supervisorStrategy = OneForOneStrategy(5, 1.minute)

}

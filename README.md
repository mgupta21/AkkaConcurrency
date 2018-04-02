# Our view of the actor and its components.
When you send a message to an actor, you're only sending it to its ActorRef; you never get to interact with the actor in any direct way.
The ActorRef will then contact the dispatcher and use it to queue the message onto the actor's mailbox.
Once that's done, the dispatcher will put the mailbox onto a thread and when the mailbox executes, it will dequeue one or more messages and send them to your actor's receive method for processing.

# Dead Letter:
Dead letter office We've seen this already, but now it's clear that the dead letter office is an actor, structurally no different from any other actor we've seen.
Any time a message is destined for an actor that either doesn't exist or is not running, it goes to the dead letter office.

# User guardian actor:
User guardian actor We know that no actor we create can exist without a parent—something has to own it.
The user guardian actor is the parent of all actors we create from the ActorSystem.

# System Guardian actor:
System guardian actor For internal actors that Akka creates to assist you, there is the system guardian actor. It serves the same purpose as the user guardian actor, but for "system" actors.

# Scheduler:
Scheduler We've met the Scheduler before and, while you could always instantiate one for yourself, the default one lives as a child of the ActorSystem.

# Event Stream:
EventStream We've never seen the EventStream in its bare form before, but we use it every time we write a log message. The EventStream has more uses than just logging.

# Settings
Settings Akka uses a new configuration system that's useful for configuring Akka and your application. You can access it from the ActorSystem.

# ACTORCONTEXT
Every actor has a context member that helps it do a lot of its work. The context is one of the things that decouples your internal actor logic from the rest of Akka that's managing it.

1) Actor creation Just like the actorOf methods that exist on ActorSystem, we have the ability to create actors from the context as well.
The difference here, of course, is that these newly created actors are children of the current actor instead of the user guardian.

2) System access: The ActorSystem at the root of this actor's hierarchy is accessible as well, which lets us access goodies like the scheduler and the settings that we've seen previously.

3) System access The ActorSystem at the root of this actor's hierarchy is accessible as well, which lets us access goodies like the scheduler and the settings that we've seen previously.

4) Relationship access The context knows who our parent is, who our children are, and gives us the ability to find other actors in the ActorSystem

5) State When accessing self or sender from the actor, we're actually getting that information from the ActorContext;


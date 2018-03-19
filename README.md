# Our view of the actor and its components.
When you send a message to an actor, you're only sending it to its ActorRef; you never get to interact with the actor in any direct way.
The ActorRef will then contact the dispatcher and use it to queue the message onto the actor's mailbox.
Once that's done, the dispatcher will put the mailbox onto a thread and when the mailbox executes, it will dequeue one or more messages and send them to your actor's receive method for processing.
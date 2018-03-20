package org.scala.akka.avionics

// TestKit: Gives us the basic framework we need to work with actors.
// This includes access to the ActorSystem as well as helper methods for dealing with responses from actors under test.

// TestActorRef: Gives us access to the underlying actor we have written.
// Everything that's publicly accessible on our actor is now available to our test.

// ImplicitSender: This nice part of the kit lets us receive responses to messages that we may send to our actor under test directly in our test code.
import akka.actor.{Actor, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

// We can't test a "trait" very easily, so we're going to
// create a specific EventSource derivation that conforms to
// the requirements of the trait so that we can test the
// production code.

// Since TestEventSource is an Actor it can implement Self Type Actor trait 'ProductionEventSource'
class TestEventSource extends Actor with ProductionEventSource {
  def receive = eventSourceReceive
}

// "class"Spec is a decent convention we'll be following
class EventSourceSpec extends TestKit(ActorSystem("EventSourceSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  import EventSource._

  override def afterAll() {
    system.terminate()
  }

  "EventSource" should {
    "allow us to register a listener" in {
      // When we need to access the functions/fields inside the actor then we do .underlyingActor
      // eg: here we need to access actor field 'listener'
      // real is like ActorRef but TestActorRef
      val real: TestEventSource = TestActorRef[TestEventSource].underlyingActor
      // testActor, which is a full-fledged ActorRef
      real.receive(RegisterListener(testActor))
      // Since listeners is not declared private in ProductionEventSource its public by default
      real.listeners should contain(testActor)
    }
    "allow us to unregister a listener" in {
      val real = TestActorRef[TestEventSource].underlyingActor
      real.receive(RegisterListener(testActor))
      real.receive(UnregisterListener(testActor))
      real.listeners.size should be(0)
    }
    
    //When a message gets sent, the sending actor hitches a ride along with the message so that the receiver can know who sent it and can then reply to it.
    // Behind the scenes
    /*
    trait ImplicitSender { this: TestKit =>
      implicit def self = testActor
    }*/
    "send the event to our test actor" in {
      // provides a test reference to our actor, so that we can go through the front door and the back door to test our EventSource
      // testA :: FrontDoor, testA.underlyingActor :: BackDoor
      val testA: TestActorRef[TestEventSource] = TestActorRef[TestEventSource]
      // we have the testActor available to send to EventSource, we also have the testActor as the sender of the message
      testA ! RegisterListener(testActor)
      testA.underlyingActor.sendEvent("Fibonacci")
      //This nice part of the kit lets us receive responses to messages that we may send to our actor under test directly in our test code.
      //The ImplicitSender is what allows your test code to react directly to (messages that are sent from your code under test)
      // one line of code will use our testActor's receive method to receive messages from our EventSource
      expectMsg("Fibonacci")
    }
  }
}

// Normally, Akka ensures that nothing can access your actor's internals by hiding things behind a location-neutral, type-independent ActorRef.
// This is a huge benefit to coding in the actor paradigm and is key to helping you deliver scalable and reliable applications
// For Unit testing, Akka provides you with the TestActorRef that gives you a magic key to the back door of the fortress


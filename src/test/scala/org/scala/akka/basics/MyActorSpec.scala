package org.scala.akka.basics

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest._

/**
  * Created by mgupta on 3/22/18.
  */
// It is helpful to create a base class for your tests
class TestKitSpec(actorSystem: ActorSystem) extends
  TestKit(actorSystem)
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll

// Ideally we create one ActorSystem across tests but sometimes existence of a single ActorSystem across all tests in a given specification can be a problem.
// That single context may require that all of our tests run sequentially, since the single instance is effectively "shared state" across all of our tests
// ParallelTestExecution ensures tests run in parallel
class MyActorSpec extends TestKitSpec(ActorSystem("MyActorSpec")) /*with ParallelTestExecution*/ with BeforeAndAfterEach {

  override def afterAll() {
    system.terminate()
  }

  def makeActor(): ActorRef = system.actorOf(Props[MyActor], "MyActor")

  "My Actor" should {
    "throw if constructed with the wrong name" in {
      val a = system.actorOf(Props[MyActor])
      /*evaluating {
        // use a generated name
      } should produce[Exception]*/
    }

    "construct without exception" in {
      val a = makeActor()
      // The throw will cause the test to fail
    }

    "respond with a Pong to a Ping" in {
      val a = makeActor()
      /*val a = makeActor()
      a ! Ping
      expectMsg(Pong)*/
    }

  }

  // Run tests sequentially (remove parallelism trait) in order to give them an environment in which they can run safely.
  // This would require shutting down the actors that we create between tests
  override def afterEach() {
    system.stop(makeActor())
    //Await.result(gracefulStop(makeActor(), 5.seconds)(system), 6.seconds)
  }
}
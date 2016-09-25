package scala_samples

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

case class WhoToGreet(who: String)
case class Greeting(message: String)
case class Greet(recipient: ActorRef)

class Greeter extends Actor {
  
  var greeting = ""

  override def receive = {
    case WhoToGreet(who)   => greeting = s"Hello $who!"
    case Greet(recipient)  => recipient ! Greeting(greeting)
  }

}

class GreetingPrinter extends Actor {

  override def receive = {
    case Greeting(message) => println(message)
  }

}

object Main extends App {

  val system = ActorSystem("hello_thales")
  val greeter = system.actorOf(Props[Greeter], "greeter")
  val printer = system.actorOf(Props[GreetingPrinter], "printer")

  greeter ! WhoToGreet("Thales")
  Thread.sleep(1000)
  greeter ! Greet(printer)
  Thread.sleep(1000)
  system.terminate()

}

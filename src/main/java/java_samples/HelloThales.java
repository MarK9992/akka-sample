package java_samples;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.io.Serializable;

public final class HelloThales {

    public static class WhoToGreet implements Serializable {

        public final String who;

        public WhoToGreet(final String who) {
            this.who = who;
        }
    }

    public static class Greeting implements Serializable {

        public final String message;

        public Greeting(String message) {
            this.message = message;
        }
    }

    public static class Greet implements Serializable {

        public final ActorRef recipient;

        public Greet(final ActorRef recipient) {
            this.recipient = recipient;
        }
    }

    public static class Greeter extends UntypedActor {

        String greeting = "";

        @Override
        public void onReceive(Object message) throws Throwable {
            if (message instanceof WhoToGreet) {
                greeting = "Hello " + ((WhoToGreet) message).who + "!";
            }
            else if (message instanceof Greet) {
                ((Greet) message).recipient.tell(new Greeting(greeting), self());
            }
        }

    }

    public static class GreetingPrinter extends UntypedActor {

        @Override
        public void onReceive(Object message) throws Throwable {
            if (message instanceof Greeting) {
                System.out.println(((Greeting) message).message);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final ActorSystem system = ActorSystem.create("hello_thales");
        final ActorRef greeter = system.actorOf(Props.create(Greeter.class), "greeter");
        final ActorRef printer = system.actorOf(Props.create(GreetingPrinter.class), "printer");

        greeter.tell(new WhoToGreet("Thales"), ActorRef.noSender());
        Thread.sleep(1000);
        greeter.tell(new Greet(printer), ActorRef.noSender());
        Thread.sleep(1000);
        system.terminate();
    }
}

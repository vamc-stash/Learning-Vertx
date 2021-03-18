package example05;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class StandardVerticle extends AbstractVerticle {
    private static final int instances = 2;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        /*
        * The setInstances() method set up the number of instance (or event loops) for a verticle.
        * The setConfig() call shows how to pass information from the parent to the verticle instance.
        * The config object is a JsonObject.
        * */
        JsonObject configObj = new JsonObject().put("sleep", 5000);
        DeploymentOptions opts = new DeploymentOptions().setInstances(instances).setConfig(configObj);
        vertx.deployVerticle("example05.StandardVerticle", opts, res -> {
           if(res.succeeded()) {
               System.out.println("Standard verticle is deployed");
               EventBus eventBus = vertx.eventBus();
               /*
               * "publish"(one-to-many): A message is sent to one or multiple listener. All handlers listening against the address will be
               * notified. No answer is expected from handlers.
               * Note:- If we define an Event Bus consumer on the event channel with two verticle instances, the published
               * message will be received by both instances. This may not be an expected behavior, so you have either to
               * restrict your instances number to only one or to use another verticle type.
               * */
               eventBus.publish("event.address", "Hello from event01");
               /*
               * "send"(one-to-one): A message is sent to one and only one handler registered against the event bus address. If multiple
               * handlers are registered, only one will be notified. The receiver will be selected by a "round-robin algorithm"
               *  as per the docs. The receiver can answer the message, this answer can be empty or contain a response body.
               * A response timeout can also be specified.
               * */
               eventBus.send("event.address", "Hello from event02");
               eventBus.send("event.address", "Hello from event03");
               //System.out.println(Thread.activeCount());
           } else {
               System.out.println("Error while deploying a verticle: " + res.cause().getMessage());
           }
        });
    }

    @Override
    public void start(Promise<Void> promise) {
        System.out.println("Thread: " + Thread.currentThread().getName());

        long sleep = config().getInteger("sleep");
        vertx.eventBus().consumer("event.address", msg -> {
           System.out.println(Thread.currentThread().getName() + ": " + msg.body());
           /*
           * Below try-catch block shows us how bad it is to block an event loop.
           *
            */
           try {
               Thread.sleep(sleep);
           } catch (Exception e) {
               //deal with exception here...
           }
        });
        promise.complete();
    }
}
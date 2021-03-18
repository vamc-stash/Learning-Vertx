package example05;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class WorkerVerticle extends AbstractVerticle {
    private static final int instances = 2;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        /*
         * A 'worker' verticle is just like a standard verticle, but it's executed using a thread from the Vert.x worker
         * thread pool, rather than using an event loop.
         * The setInstances() method set up the number of instance (or event loops) for a verticle.
         * The setConfig() call shows how to pass information from the parent to the verticle instance.
         * The config object is a JsonObject.
         * */
        JsonObject configObj = new JsonObject().put("sleep", 5000);
        DeploymentOptions opts = new DeploymentOptions().setInstances(instances).setWorker(true).setConfig(configObj);
        vertx.deployVerticle("example05.WorkerVerticle", opts, res -> {
            if(res.succeeded()) {
                System.out.println("Worker verticle is deployed");
                EventBus eventBus = vertx.eventBus();
                eventBus.publish("event.address", "Hello from event01");
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
           try {
               Thread.sleep(sleep);
           } catch (Exception e) {
               //deal with exception here...
           }
        });
        promise.complete();
    }
}

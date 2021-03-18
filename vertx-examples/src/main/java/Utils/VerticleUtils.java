package Utils;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class VerticleUtils {
    public static Future<Vertx> deployVerticle(String verticle, int instances) {
        Promise<Vertx> promise = Promise.promise();
        Vertx vertx = Vertx.vertx();
        DeploymentOptions opts = new DeploymentOptions().setInstances(instances);
        vertx.deployVerticle(verticle, opts, res -> {
            if(res.succeeded()) {
                System.out.println("Verticle: " + verticle + " is successfully deployed");
                promise.complete(vertx);
            } else {
                System.out.println("Error: " + res.cause().getMessage() + " while deploying a verticle: " + verticle);
                promise.fail(res.cause().getMessage());
            }
        });
        return promise.future();
    }

    public static Future<Vertx> deployVerticle(String verticle) {
        Promise<Vertx> promise = Promise.promise();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(verticle, res -> {
            if(res.succeeded()) {
                System.out.println("Verticle: " + verticle + " is successfully deployed");
                promise.complete(vertx);
            } else {
                System.out.println("Error: " + res.cause().getMessage() + " while deploying a verticle: " + verticle);
                promise.fail(res.cause().getMessage());
            }
        });
        return promise.future();
    }
}

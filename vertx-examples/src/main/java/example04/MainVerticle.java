package example04;

import Utils.VerticleUtils;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {
    final int httpPort = 8080;

    @Override
    public void start() {
        vertx.deployVerticle("example04.GreetVerticle", res -> {
            if(res.succeeded()) {
                EventBus eventBus = vertx.eventBus();
                eventBus.send("greet.address", "Hello Vertx!!");
            } else {
                System.out.println("Error: " + res.cause().getMessage() + " while deploying a verticle: GreetVerticle");
            }
        });

        DeploymentOptions opts = new DeploymentOptions().setInstances(2);
        vertx.deployVerticle("example04.GreetHttpVerticle", opts, res -> {
            if(res.succeeded()) {
                final Router router = Router.router(vertx);
                router.get("/api/v1/greet").handler(this::greet);
                router.get("/api/v1/greet/:name").handler(this::greetName);

                vertx.createHttpServer().requestHandler(router).listen(httpPort, handler -> {
                    if (handler.succeeded()) {
                        System.out.println("Server is listening at port: " + httpPort );
                    } else {
                        System.out.println("Server is failed to start at port: " + httpPort );
                    }
                });
            } else {
                System.out.println("Error: " + res.cause().getMessage() + " while deploying a verticle: GreetHttpVerticle");
            }
        });
    }

    private void greet(RoutingContext ctx) {
        vertx.eventBus().request("greet.http.address", "", res -> {
           ctx.response().end(res.result().body().toString());
        });
    }

    private void greetName(RoutingContext ctx) {
        vertx.eventBus().request("greet.name.http.address", ctx.pathParam("name"), res -> {
            ctx.response().end(res.result().body().toString());
        });
    }

    @Override
    public void stop() {
        System.out.println("Shutting down application");
    }
}

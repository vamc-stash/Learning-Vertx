package example02;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {
    private final int httpPort = 8080;

    @Override
    public void start(Promise<Void> promise) {
        /*
        * Vert.x core API allows to start HTTP servers and listen for incoming connections, but it does not provide any
        * facility to, say, have different handlers depending on the requested URL or processing request bodies.
        * This is the role of a router(provided by Vert.x web API) as it dispatches requests to different processing handlers
        * depending on the URL, the HTTP method, etc.
        */
        final Router router = Router.router(vertx);

        router.get("/api/v1/greet").handler(this::greet);
        router.get("/api/v1/greet/:name").handler(this::greetName);

        vertx.createHttpServer().requestHandler(router).listen(httpPort, res -> {
            if (res.succeeded()) {
                System.out.println("Server is listening at port: " + httpPort );
                promise.complete();
            } else {
                System.out.println("Server is failed to start at port: " + httpPort );
                promise.fail(res.cause());
            }
        });
    }

    private void greet(RoutingContext ctx) {
        ctx.response().end("Hello vert.x!!");
    }

    private void greetName(RoutingContext ctx) {
        ctx.response().end("Hello " + ctx.pathParam("name"));
    }

    @Override
    public void stop() {
        System.out.println("Shutting down application");
    }
}

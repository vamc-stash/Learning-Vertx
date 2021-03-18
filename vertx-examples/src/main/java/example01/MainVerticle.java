package example01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> promise) {
        vertx.createHttpServer().requestHandler(req -> {
            req.response().end("Hello vert.x!!!");
        }).listen(8080, res -> {
            if (res.succeeded()) {
                promise.complete();
            } else {
                promise.fail(res.cause());
            }
        });
    }

    @Override
    public void stop() {
        System.out.println("Shutting down application");
    }
}

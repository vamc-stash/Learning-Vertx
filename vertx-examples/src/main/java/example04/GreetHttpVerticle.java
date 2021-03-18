package example04;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import java.util.UUID;

public class GreetHttpVerticle extends AbstractVerticle {
    String fakeVerticleId = UUID.randomUUID().toString();

    @Override
    public void start() {
        vertx.eventBus().consumer("greet.http.address").handler(msg -> {
            msg.reply(fakeVerticleId + ": " + "Hello Vertx!!");
        });

        vertx.eventBus().consumer("greet.name.http.address").handler(msg -> {
            msg.reply(fakeVerticleId + ": " + "Hello " + msg.body().toString());
        });
    }
}

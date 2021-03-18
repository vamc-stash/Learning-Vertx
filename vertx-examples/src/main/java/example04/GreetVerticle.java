package example04;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import java.util.UUID;

public class GreetVerticle extends AbstractVerticle {
    String fakeVerticleId = UUID.randomUUID().toString();

    @Override
    public void start() {
        vertx.eventBus().consumer("greet.address").handler(msg -> {
            System.out.println(fakeVerticleId + ": " + msg.body().toString());
        });
    }
}

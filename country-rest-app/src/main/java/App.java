import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("verticle.MainVerticle", res -> {
            if(res.succeeded()) {
                LOGGER.info(res.result());
            } else {
                LOGGER.error(res.cause());
            }
        });
    }
}

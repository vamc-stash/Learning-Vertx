package example03;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class RouterUtils {

    public static Future<Router> setUpRouter1(Vertx vertx) {
        Promise<Router> promise = Promise.promise();
        Router router = Router.router(vertx);

        router.get("/path1").handler(ctx -> {
            ctx.response().end("You're at path1");
        });
        promise.complete(router);
        return promise.future();
    }

    public static Future<Router> setUpRouter2(Vertx vertx) {
        Promise<Router> promise = Promise.promise();
        Router router = Router.router(vertx);

        router.get("/path2").handler(ctx -> {
            ctx.response().end("You're at path2");
        });
        promise.complete(router);
        return promise.future();
    }
}

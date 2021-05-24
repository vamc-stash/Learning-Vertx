package api.router;

import api.handler.InitHandler;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class InitRouter {
    private final Vertx vertx;
    private final InitHandler initHandler;

    public InitRouter(Vertx vertx, InitHandler initHandler) {
        this.vertx = vertx;
        this.initHandler = initHandler;
    }

    public Future<Router> buildInitRouter() {
        Promise<Router> promise = Promise.promise();
        Router router = Router.router(vertx);
        router.route("/").handler(initHandler::home);
        promise.complete(router);
        return promise.future();
    }
}

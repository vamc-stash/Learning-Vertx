package api.router;

import api.handler.AuthHandler;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;

public class AuthRouter {
    private final Vertx vertx;
    private final AuthHandler authHandler;
    private final SessionHandler sessionHandler;

    public AuthRouter(Vertx vertx, AuthHandler authHandler) {
        this.vertx = vertx;
        this.authHandler = authHandler;
        sessionHandler = SessionHandler.create(SessionStore.create(vertx));
    }

    public Future<Router> buildAuthRouter() {
        Promise<Router> promise = Promise.promise();
        Router router = Router.router(vertx);
        router.route("/*").handler(BodyHandler.create());
        router.post("/signup").handler(authHandler::signup);
        router.post("/login").handler(sessionHandler).handler(authHandler::login);
        router.get("/logout").handler(sessionHandler).handler(authHandler::logout);

        promise.complete(router);
        return promise.future();
    }
}

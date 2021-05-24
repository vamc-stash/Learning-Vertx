package verticle;

import api.handler.AuthHandler;
import api.handler.InitHandler;
import api.router.AuthRouter;
import api.router.InitRouter;
import api.service.AuthService;
import api.service.ConfigService;
import api.service.DbService;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class MainVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    private int httpPort;
    @Override
    public void start(Promise<Void> promise) {
        ConfigRetriever retriever = ConfigService.getConfigRetriever(vertx);
        DbService.prepareDatabase(retriever, vertx)
                .compose(this::setupRouters)
                .onSuccess(router -> {
                    Handler<AsyncResult<JsonObject>> handler = asyncResult -> this.buildHttpServer(promise, router, asyncResult);
                    retriever.getConfig(handler);
                })
                .onFailure(handler -> {
                    LOGGER.error(handler.getCause().getMessage());
                });
    }

    private Future<Router> setupRouters(MySQLPool client) {
        Promise<Router> promise = Promise.promise();
        CompositeFuture.all(
                new AuthRouter(vertx, new AuthHandler(new AuthService(client)))
                        .buildAuthRouter(),
                new InitRouter(vertx, new InitHandler())
                        .buildInitRouter()
        ).onSuccess(routers -> {
            Router router = Router.router(vertx);
            router.mountSubRouter("/", routers.resultAt(0));
            router.mountSubRouter("/", routers.resultAt(1));
            promise.complete(router);
        }).onFailure(handler -> {
            promise.fail(handler.getCause());
        });

        return promise.future();
    }

    private void buildHttpServer(Promise<Void> promise, Router router, AsyncResult<JsonObject> asyncResult) {
        if (asyncResult.succeeded()) {
            JsonObject http = asyncResult.result().getJsonObject("http");
            httpPort = http.getInteger("port");

            vertx.createHttpServer().requestHandler(router).listen(httpPort, res -> {
                if (res.succeeded()) {
                    LOGGER.info("Server is started at port: " + httpPort);
                } else {
                    LOGGER.error("Server is failed to start port: " + httpPort);
                }
            });
            promise.complete();
        } else {
            promise.fail(asyncResult.cause().getMessage());
        }
    }

    @Override
    public void stop(Promise<Void> promise) {
        LOGGER.info("Shutting down the application");
        promise.complete();
    }
}

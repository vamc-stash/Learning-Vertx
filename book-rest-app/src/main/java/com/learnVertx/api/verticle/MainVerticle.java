package com.learnVertx.api.verticle;

import com.learnVertx.api.api.handler.BookHandler;
import com.learnVertx.api.api.router.BookRouter;
import com.learnVertx.api.api.service.BookService;
import com.learnVertx.api.utils.ConfigUtils;
import com.learnVertx.api.utils.DbUtils;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    private int httpPort;

    @Override
    public void start(Promise<Void> promise) {
        ConfigRetriever retriever = ConfigUtils.configRetriever(vertx);

        /* When the future of prepareDatabase() completes successfully, then setUpRouter() is called which eventually consumes the
        * MySQLPool object returned by prepareDatabase() and then only after it completes successfully, will call buildHttpServer().
        * setUpRouter is never called if prepareDatabase raises an error  in which case the future is in a failed state and
        * becomes completed with the exception describing the error. This mechanism applies recursively to the next steps as well
        */
        DbUtils.prepareDatabase(retriever, vertx)
                .compose(this::setUpRouter)
                .onSuccess(router -> {
                    Handler<AsyncResult<JsonObject>> handler = asyncResult -> this.buildHttpServer(promise, router, asyncResult);
                    retriever.getConfig(handler);
                })
                .onFailure(handler -> {
                    System.out.println(handler.getMessage());
                });
    }

    private Future<Router> setUpRouter(MySQLPool client) {
        Promise<Router> promise = Promise.promise();
        BookService bookService = new BookService(client);
        BookHandler bookHandler = new BookHandler(bookService);
        Router router = new BookRouter(vertx, bookHandler).setRouter();
        promise.complete(router);
        return promise.future();
    }

    private void buildHttpServer(Promise<Void> promise, Router router, AsyncResult<JsonObject> asyncResult) {
        if (asyncResult.succeeded()) {
            JsonObject http = asyncResult.result().getJsonObject("http");
            httpPort = http.getInteger("port");

            vertx.createHttpServer().requestHandler(router).listen(httpPort, res -> {
                if (res.succeeded()) {
                    LOGGER.info("Server is started at port: {0}", String.valueOf(httpPort));
                } else {
                    LOGGER.error("Server is failed to start port: {0}", String.valueOf(httpPort));
                }
            });
            promise.complete();
        } else {
            promise.fail(asyncResult.cause().getMessage());
        }
    }

    @Override
    public void stop() {
        LOGGER.info("Shutting down application");
    }
}

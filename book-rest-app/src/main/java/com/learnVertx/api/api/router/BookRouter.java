package com.learnVertx.api.api.router;

import com.learnVertx.api.api.handler.BookHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class BookRouter {
    private final Vertx vertx;
    private final BookHandler bookHandler;

    public BookRouter(Vertx vertx, BookHandler bookHandler) {
        this.vertx = vertx;
        this.bookHandler = bookHandler;
    }

    public Router setRouter() {
        Router router = Router.router(vertx);
        return router.mountSubRouter("/api/v1", buildBookRouter());
    }

    private Router buildBookRouter() {
        /*
        * The router object can be used as a HTTP server handler, which then dispatches to other handler
        * */
        final Router router = Router.router(vertx);

        // Create a BodyHandler to have the capability to retrieve the body
        router.route("/books*").handler(BodyHandler.create());
        /*
        * Above line of code makes all HTTP POST requests go through a first handler, here io.vertx.ext.web.handler.BodyHandler.
        * This handler automatically decodes the body from the HTTP requests (e.g., form submissions), which can then be manipulated
        * as Vert.x buffer objects.
        * */
        router.get("/books").handler(bookHandler::getAll);
        router.get("/books/:id").handler(bookHandler::getOne);
        router.post("/books").handler(bookHandler::addOne);
        router.put("/books/:id").handler(bookHandler::updateOne);
        router.delete("/books/:id").handler(bookHandler::deleteOne);

        return router;
    }
}

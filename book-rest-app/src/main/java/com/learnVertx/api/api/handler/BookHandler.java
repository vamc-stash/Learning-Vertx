package com.learnVertx.api.api.handler;

import com.learnVertx.api.api.service.BookService;
import io.vertx.ext.web.RoutingContext;

public class BookHandler {

    private final BookService bookService;

    public BookHandler(BookService bookService) {
        this.bookService = bookService;
    }

    public void getAll(RoutingContext ctx) {
        bookService.getAll(ctx);
    }

    public void getOne(RoutingContext ctx) {
        bookService.getOne(ctx);
    }

    public void addOne(RoutingContext ctx) {
        bookService.addOne(ctx);
    }

    public void updateOne(RoutingContext ctx) {
        bookService.updateOne(ctx);
    }

    public void deleteOne(RoutingContext ctx) {
        bookService.deleteOne(ctx);
    }
}

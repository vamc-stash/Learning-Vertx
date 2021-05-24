package api.service;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.NoSuchElementException;

public class HttpResponseService {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static void sendOkResponse(RoutingContext ctx, Object response) {
        ctx.response()
                .setStatusCode(200)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(response));
    }

    public static void sendCreatedResponse(RoutingContext ctx, Object response) {
        ctx.response()
                .setStatusCode(201)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(response));
    }

    public static void sendNoContentResponse(RoutingContext ctx) {
        ctx.response()
                .setStatusCode(204)
                .end();
    }

    public static void sendErrorResponse(RoutingContext ctx, Throwable throwable) {
        final int status;
        final String message;

        if (throwable instanceof IllegalArgumentException || throwable instanceof IllegalStateException || throwable instanceof NullPointerException) {
            status = 400;
            message = throwable.getMessage();
        } else if (throwable instanceof NoSuchElementException) {
            status = 404;
            message = throwable.getMessage();
        } else {
            status = 500;
            message = INTERNAL_SERVER_ERROR;
        }

        ctx.response()
                .setStatusCode(status)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(new JsonObject().put("ERROR", message).encodePrettily());
    }

    public static void sendErrorResponse(RoutingContext ctx, String message) {
        ctx.response()
                .setStatusCode(404)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(new JsonObject().put("ERROR", message).encodePrettily());
    }
}

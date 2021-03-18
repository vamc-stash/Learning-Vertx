package com.learnVertx.api.api.service;

import com.learnVertx.api.api.model.Book;
import com.learnVertx.api.utils.ConvertUtils;
import com.learnVertx.api.utils.QueryUtils;
import com.learnVertx.api.utils.ResponseUtils;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class BookService {
    private final MySQLPool client;

    public BookService(MySQLPool client) {
        this.client = client;
    }

    public void getAll(RoutingContext ctx) {
        client.query(QueryUtils.SQL_SELECT_ALL_BOOKS).execute(ar -> {
            if(ar.failed()) {
                ResponseUtils.sendErrorResponse(ctx, ar.cause());
            } else {
                RowSet<Row> rows = ar.result();
                JsonArray result = ConvertUtils.sqlRowsToJsonArray(rows);
                ResponseUtils.sendOkResponse(ctx, result);
            }
        });
    }

    public void getOne(RoutingContext ctx) {
        String id = ctx.request().getParam("id");
        int idAsInt = Integer.parseInt(id);
        client.preparedQuery(QueryUtils.SQL_SELECT_ONE_BOOK).execute(Tuple.of(idAsInt), ar -> {
            if(ar.failed()) {
                ResponseUtils.sendErrorResponse(ctx, ar.cause());
            } else {
                RowSet<Row> rows = ar.result();
                if(rows.size() < 1) {
                    ResponseUtils.sendNoContentResponse(ctx);
                } else {
                    JsonArray result = ConvertUtils.sqlRowsToJsonArray(rows);
                    ResponseUtils.sendOkResponse(ctx, result);
                }
            }
        });
    }

    public void addOne(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();
        client.preparedQuery(QueryUtils.SQL_INSERT_ONE_BOOK).execute(Tuple.of(body.getString("name"), body.getString("author"), body.getInteger("price")), ar -> {
            if(ar.failed()) {
                ResponseUtils.sendErrorResponse(ctx, ar.cause());
            } else {
                RowSet<Row> rows = ar.result();
                Long lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID);
                client.preparedQuery(QueryUtils.SQL_SELECT_ONE_BOOK).execute(Tuple.of(lastInsertId), ar2 -> {
                    if(ar2.failed()) {
                        ResponseUtils.sendErrorResponse(ctx, ar.cause());
                    } else {
                        JsonArray result = ConvertUtils.sqlRowsToJsonArray(ar2.result());
                        ResponseUtils.sendCreatedResponse(ctx, result);
                    }
                });
            }
        });
    }

    public void updateOne(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();
        String id = ctx.request().getParam("id");
        int idAsInt = Integer.parseInt(id);
        client.preparedQuery(QueryUtils.SQL_UPDATE_ONE_BOOK).execute(Tuple.of(body.getInteger("price"), idAsInt), ar -> {
            if(ar.failed()) {
                ResponseUtils.sendErrorResponse(ctx, ar.cause());
            } else {
                ResponseUtils.sendNoContentResponse(ctx);
            }
        });
    }

    public void deleteOne(RoutingContext ctx) {
        String id = ctx.request().getParam("id");
        int idAsInt = Integer.parseInt(id);
        client.preparedQuery(QueryUtils.SQL_DELETE_ONE_BOOK).execute(Tuple.of(idAsInt), ar -> {
            if(ar.failed()) {
                ResponseUtils.sendErrorResponse(ctx, ar.cause());
            } else {
                ResponseUtils.sendNoContentResponse(ctx);
            }
        });
    }
}

package com.learnVertx.api.utils;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class DbUtils {
    public static Future<MySQLPool> prepareDatabase(ConfigRetriever retriever, Vertx vertx) {
        Promise<MySQLPool> promise = Promise.promise();
        retriever.getConfig(asyncResult -> {
            JsonObject dbProps = asyncResult.result().getJsonObject("db");
            MySQLConnectOptions opts = new MySQLConnectOptions()
                    .setPort(dbProps.getInteger("port"))
                    .setHost(dbProps.getString("host"))
                    .setDatabase(dbProps.getString("database"))
                    .setUser(dbProps.getString("user"))
                    .setPassword(dbProps.getString("password"));

            PoolOptions poolOpts = new PoolOptions().setMaxSize(5); //specifies the number of concurrent connections
            MySQLPool client = MySQLPool.pool(vertx, opts, poolOpts);
            client.getConnection(handler -> {
                if (handler.succeeded()) {
                    client.query(QueryUtils.SQL_CREATE_BOOKS_TABLE).execute(ar -> {
                        if(ar.succeeded()) {
                            promise.complete(client);
                        } else {
                            promise.fail(ar.cause());
                        }
                    });
                } else {
                    promise.fail(handler.cause());
                }
            });
        });
        return promise.future();
    }
}

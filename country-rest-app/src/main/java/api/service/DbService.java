package api.service;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import utils.QueryUtils;

public class DbService {
    public static Future<MySQLPool> prepareDatabase(ConfigRetriever retriever, Vertx vertx) {
        Promise<MySQLPool> promise = Promise.promise();

        retriever.getConfig(ar -> {
            JsonObject dbProps = ar.result().getJsonObject("db");

            MySQLConnectOptions sqlConnectOptions = new MySQLConnectOptions()
                    .setPort(dbProps.getInteger("port"))
                    .setHost(dbProps.getString("host"))
                    .setDatabase(dbProps.getString("database"))
                    .setUser(dbProps.getString("user"))
                    .setPassword(dbProps.getString("password"));
            PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
            MySQLPool client = MySQLPool.pool(vertx, sqlConnectOptions, poolOptions);

            client.getConnection(conn -> {
                if(conn.succeeded()) {
                    CompositeFuture.all(
                            execQuery(client, QueryUtils.CREATE_USER_TABLE),
                            execQuery(client, QueryUtils.CREATE_ROLE_TABLE)
                    ).compose(handler -> {
                        return //CompositeFuture.all(
                                execQuery(client, QueryUtils.CREATE_USER_ROLE_TABLE);
                                //execQuery(client, QueryUtils.INSERT_PERMITTED_ROLES)
                        //);
                    }).onSuccess(handler -> {
                        promise.complete(client);
                    }).onFailure(handler -> {
                        promise.fail(handler.getCause());
                    });
                } else {
                    promise.fail(conn.cause());
                }
            });
        });
        return promise.future();
    }

    public static Future<Void> execQuery(MySQLPool client, String query) {
        Promise<Void> promise = Promise.promise();
        client.query(query).execute(ar -> {
            if(ar.succeeded()) {
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    public static Future<RowSet<Row>> execPreparedQuery(MySQLPool client, String query, Tuple tuple) {
        Promise<RowSet<Row>> promise = Promise.promise();
        client.preparedQuery(query).execute(tuple, ar -> {
            if(ar.succeeded()) {
                promise.complete(ar.result());
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }
}

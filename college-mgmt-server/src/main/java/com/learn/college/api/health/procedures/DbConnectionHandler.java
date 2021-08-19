package com.learn.college.api.health.procedures;

import com.learn.college.common.config.ApplicationConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

import static com.learn.college.common.config.Constants.CONNECTION;
import static com.learn.college.common.config.Constants.POOL;

public class DbConnectionHandler implements Handler<Promise<Status>> {

  private final MySQLPool pool;

  public DbConnectionHandler(JsonObject options) {
    MySQLConnectOptions connectOptions =
        new MySQLConnectOptions(options.getJsonObject(CONNECTION));
    PoolOptions poolOptions = new PoolOptions(options.getJsonObject(POOL));
    pool = MySQLPool.pool(Vertx.currentContext().owner(), connectOptions, poolOptions);
  }

  @Override
  public void handle(Promise<Status> event) {

    JsonObject obj = new JsonObject();

    pool.getConnection(
        connection -> {
          if (connection.succeeded()) {
            obj.put("connection", "ACTIVE");
            event.complete(Status.OK(obj));
          } else {
            obj.put("connection", "INACTIVE");
            event.complete(Status.KO(obj));
          }
        });
  }
}

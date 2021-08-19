package com.learn.college.common.util;

import com.learn.college.common.interfaces.BaseRowMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** The type Db Utils */
@Slf4j
public class DbUtils {

  private static final String QUERY_EXECUTION_SUCCESS = "Query Execution Success";

  private static final String QUERY_EXECUTION_FAILED = "Error: Query Execution Failed";

  private static final String EXECUTING_QUERY_TUPLE = "Executing Query: {} \n Tuple: {}";

  private static final String EXECUTING_QUERY_PARAMS = "Executing Query: {} \n Params: {}";

  private final MySQLPool client;

  private static final String CONNECTION = "connection";
  private static final String POOL = "pool";

  public DbUtils(Vertx vertx, JsonObject dbConfig) {
    JsonObject connectionConfig = dbConfig.getJsonObject(CONNECTION);
    JsonObject poolConfig = dbConfig.getJsonObject(POOL);
    MySQLConnectOptions connectOptions = new MySQLConnectOptions(connectionConfig);
    PoolOptions poolOptions = new PoolOptions(poolConfig);
    client = MySQLPool.pool(vertx, connectOptions, poolOptions);
    log.info("DB Client initialized successfully");
  }

  public void getRowSet(String sql, Tuple tuple, Handler<AsyncResult<RowSet<Row>>> handler) {
    log.debug(EXECUTING_QUERY_TUPLE, sql, tuple);

    Objects.requireNonNull(sql); // throws NullPointerException if Null
    Objects.requireNonNull(tuple);

    client
        .preparedQuery(sql)
        .execute(
            tuple,
            ar -> {
              if (ar.succeeded()) {
                log.info(QUERY_EXECUTION_SUCCESS);
                RowSet<Row> rows = ar.result();
                handler.handle(Future.succeededFuture(rows));
              } else {
                log.error(QUERY_EXECUTION_FAILED, ar.cause());
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
  }

  /**
   * Exec get
   *
   * @param <T> </T> the type parameter
   * @param sql prepared sql stmt
   * @param tuple tuple
   * @param mapper mapper class
   * @param handler the handler
   */
  public <T> void execGet(
      String sql, Tuple tuple, BaseRowMapper<T> mapper, Handler<AsyncResult<List<T>>> handler) {
    log.debug(EXECUTING_QUERY_TUPLE, sql, tuple);

    Objects.requireNonNull(sql);
    Objects.requireNonNull(tuple);

    client
        .preparedQuery(sql)
        .execute(
            tuple,
            ar -> {
              if (ar.succeeded()) {
                log.info(QUERY_EXECUTION_SUCCESS);
                RowSet<Row> rows = ar.result();
                List<T> result = new ArrayList<>();
                for (Row row : rows) {
                  result.add(mapper.mapRow(row));
                }
                handler.handle(Future.succeededFuture(result));
              } else {
                log.error(QUERY_EXECUTION_FAILED);
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
  }

  /**
   * Exec get
   *
   * @param <T> </T> the type parameter
   * @param sql prepared sql stmt
   * @param tuple tuple
   * @param mapper mapper class
   * @param handler the handler
   */
  public <T> void execRow(
      String sql, Tuple tuple, BaseRowMapper<T> mapper, Handler<AsyncResult<T>> handler) {
    log.debug(EXECUTING_QUERY_TUPLE, sql, tuple);

    Objects.requireNonNull(sql); // throws NullPointerException if Null
    Objects.requireNonNull(tuple);

    client
        .preparedQuery(sql)
        .execute(
            tuple,
            ar -> {
              if (ar.succeeded()) {
                log.info(QUERY_EXECUTION_SUCCESS);
                RowSet<Row> rows = ar.result();
                if (rows.iterator().hasNext()) {
                  T result = mapper.mapRow(rows.iterator().next());
                  handler.handle(Future.succeededFuture(result));
                } else {
                  handler.handle(Future.succeededFuture());
                }
              } else {
                log.error(QUERY_EXECUTION_FAILED);
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
  }

  /**
   * Exec get
   *
   * @param sql prepared sql stmt
   * @param tuple tuple
   * @param handler the handler
   */
  public void execGet(String sql, Tuple tuple, Handler<AsyncResult<List<JsonObject>>> handler) {
    log.debug(EXECUTING_QUERY_TUPLE, sql, tuple);

    Objects.requireNonNull(sql);
    Objects.requireNonNull(tuple);

    client
        .preparedQuery(sql)
        .execute(
            tuple,
            ar -> {
              if (ar.succeeded()) {
                log.info(QUERY_EXECUTION_SUCCESS);
                convertRowSetToList(ar, handler);
              } else {
                log.error(QUERY_EXECUTION_FAILED);
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
  }

  private void convertRowSetToList(
      AsyncResult<RowSet<Row>> ar, Handler<AsyncResult<List<JsonObject>>> handler) {
    RowSet<Row> rows = ar.result();
    try {
      List<String> columns = rows.columnsNames();
      List<JsonObject> result = new ArrayList<>();

      for (Row row : rows) {
        JsonObject obj = new JsonObject();
        for (String col : columns) {
          if (row.getValue(col) instanceof String[]) {
            obj.put(col, Collections.singletonList(row.getValue(col)));
          } else if (row.getValue(col) instanceof OffsetDateTime
              || row.getValue(col) instanceof LocalDate) {
            obj.put(col, row.getValue(col).toString());
          } else {
            obj.put(col, row.getValue(col));
          }
        }
        result.add(obj);
      }
      handler.handle(Future.succeededFuture(result));
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * Exec update
   *
   * @param sql prepared sql stmt
   * @param tuple tuple
   * @param handler the handler
   */
  public void execUpdate(String sql, Tuple tuple, Handler<AsyncResult<Void>> handler) {
    log.debug(EXECUTING_QUERY_TUPLE, sql, tuple);

    Objects.requireNonNull(sql);
    Objects.requireNonNull(tuple);

    client
        .preparedQuery(sql)
        .execute(
            tuple,
            ar -> {
              if (ar.succeeded()) {
                log.info(QUERY_EXECUTION_SUCCESS);
                handler.handle(Future.succeededFuture());
              } else {
                log.error(QUERY_EXECUTION_FAILED);
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
  }

  /**
   * Exec insert
   *
   * @param sql prepared sql stmt
   * @param tuple tuple
   * @param handler the handler
   */
  public void execInsert(String sql, Tuple tuple, Handler<AsyncResult<Long>> handler) {
    log.debug(EXECUTING_QUERY_TUPLE, sql, tuple);

    Objects.requireNonNull(sql);
    Objects.requireNonNull(tuple);

    client
        .preparedQuery(sql)
        .execute(
            tuple,
            ar -> {
              if (ar.succeeded()) {
                log.info(QUERY_EXECUTION_SUCCESS);
                RowSet<Row> rows = ar.result();
                Long lastInsertedId = rows.property(MySQLClient.LAST_INSERTED_ID);
                log.debug("Last Inserted Id: {}", lastInsertedId);
                handler.handle(Future.succeededFuture(lastInsertedId));
              } else {
                log.error(QUERY_EXECUTION_FAILED);
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
  }

  /**
   * Exec statements in batch
   *
   * @param sql prepared sql stmt
   * @param tupleList List of tuple
   * @param handler the handler
   */
  public void execBatch(String sql, List<Tuple> tupleList, Handler<AsyncResult<Void>> handler) {
    Objects.requireNonNull(sql);
    Objects.requireNonNull(tupleList);
    client
        .preparedQuery(sql)
        .executeBatch(
            tupleList,
            ar -> {
              if (ar.succeeded()) {
                log.info(QUERY_EXECUTION_SUCCESS);
                handler.handle(Future.succeededFuture());
              } else {
                log.error(QUERY_EXECUTION_FAILED);
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
  }
}

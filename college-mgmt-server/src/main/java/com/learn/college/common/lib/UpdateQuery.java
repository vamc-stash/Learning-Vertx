package com.learn.college.common.lib;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;

import java.util.*;

import static com.learn.college.common.config.Constants.*;

public class UpdateQuery {

  private StringBuilder updateQuery;
  private WhereQuery whereQuery = null;
  private Tuple tuple;
  private String finalUpdateQuery = null;
  private final String tableName;

  public UpdateQuery(String tableName) {
    this.updateQuery = new StringBuilder();
    this.tableName = tableName;
    this.tuple = Tuple.tuple();
  }

  public UpdateQuery add(String param, Integer value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addInteger(value);

    return this;
  }

  public UpdateQuery add(String param, Long value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addLong(value);

    return this;
  }

  public UpdateQuery add(String param, String value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addString(value);

    return this;
  }

  public UpdateQuery add(String param, Boolean value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addBoolean(value);

    return this;
  }

  public UpdateQuery add(String param, Float value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addFloat(value);

    return this;
  }

  public UpdateQuery add(String param, JsonObject value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addValue(value);

    return this;
  }

  public UpdateQuery add(String param, Map<String, Object> value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addValue(JsonObject.mapFrom(value));
    return this;
  }

  public UpdateQuery add(String param, JsonArray value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addValue(value);

    return this;
  }

  public UpdateQuery add(String param, String[] value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addValue(new JsonArray(Arrays.asList(value)));

    return this;
  }

  public UpdateQuery add(String param, List<String> value) {
    if (Objects.isNull(value)) {
      return this;
    }

    updateQuery.append(param);
    updateQuery.append(EQUAL_VALUE);
    updateQuery.append(COMMA_SPACE);
    tuple.addArrayOfString(value.toArray(new String[0]));

    return this;
  }

  public Tuple getTuple() {
    return tuple;
  }

  public WhereQuery where() {
    this.whereQuery = new WhereQuery(this.tuple);
    return this.whereQuery;
  }

  public UpdateQuery build() {
    if (updateQuery.length() > 0) {
      updateQuery.deleteCharAt(updateQuery.lastIndexOf(COMMA));
      String whereQuery = this.whereQuery.build();
      if (Objects.nonNull(whereQuery)) {
        updateQuery.append(whereQuery);
        updateQuery.append(SEMICOLON);
        finalUpdateQuery = UPDATE_SPACE + tableName + SPACE_SET_SPACE + updateQuery.toString();
      }
    }
    return this;
  }

  public String getFinalUpdateQuery() {
    return this.finalUpdateQuery;
  }
}

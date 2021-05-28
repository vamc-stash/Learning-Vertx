package com.learn.college.common.lib;

import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Tuple;

import java.util.Objects;

import static com.learn.college.common.config.Constants.*;

public class WhereQuery {

  private StringBuilder whereQuery;
  private Tuple tuple;

  public WhereQuery(Tuple tuple) {
    this.whereQuery = new StringBuilder();
    this.tuple = tuple;
  }

  public WhereQuery eq(String param, Integer value) {
    if (Objects.isNull(value)) {
      return this;
    }
    whereQuery.append(param);
    whereQuery.append(EQUAL_VALUE);

    tuple.addInteger(value);
    return this;
  }

  public WhereQuery eq(String param, Long value) {
    if (Objects.isNull(value)) {
      return this;
    }
    whereQuery.append(param);
    whereQuery.append(EQUAL_VALUE);

    tuple.addLong(value);
    return this;
  }

  public WhereQuery eq(String param, String value) {
    if (Objects.isNull(value)) {
      return this;
    }
    whereQuery.append(param);
    whereQuery.append(EQUAL_VALUE);

    tuple.addString(value);
    return this;
  }

  public WhereQuery eq(String param, Boolean value) {
    if (Objects.isNull(value)) {
      return this;
    }
    whereQuery.append(param);
    whereQuery.append(EQUAL_VALUE);

    tuple.addBoolean(value);
    return this;
  }

  public WhereQuery and() {
    whereQuery.append(SPACE_AND_SPACE);
    return this;
  }

  public WhereQuery or() {
    whereQuery.append(SPACE_OR_SPACE);
    return this;
  }

  public WhereQuery in(String param, JsonArray values) {
    if (Objects.isNull(values) || values.isEmpty()) {
      return this;
    }
    whereQuery.append(param);
    whereQuery.append(SPACE_IN_SPACE_BRACKET_BEGIN);
    for (Object value : values) {
      whereQuery.append(SPACE_VALUE);
      whereQuery.append(COMMA);

      tuple.addValue(value);
    }
    whereQuery.deleteCharAt(whereQuery.length() - 1);
    whereQuery.append(SPACE_BRACKET_END);
    return this;
  }

  public WhereQuery andAdd(String param, Object value) {
    if (Objects.isNull(value)) {
      return this;
    }

    if (value instanceof Integer) {
      return this.and().eq(param, (Integer) value);
    } else if (value instanceof Long) {
      return this.and().eq(param, (Long) value);
    } else if (value instanceof String) {
      return this.and().eq(param, (String) value);
    } else if (value instanceof Boolean) {
      return this.and().eq(param, (Boolean) value);
    } else if (value instanceof JsonArray) {
      return this.and().in(param, (JsonArray) value);
    } else {
      return this;
    }
  }

  public WhereQuery orAdd(String param, Object value) {
    if (Objects.isNull(value)) {
      return this;
    }

    if (value instanceof Integer) {
      return this.or().eq(param, (Integer) value);
    } else if (value instanceof Long) {
      return this.or().eq(param, (Long) value);
    } else if (value instanceof String) {
      return this.or().eq(param, (String) value);
    } else if (value instanceof Boolean) {
      return this.or().eq(param, (Boolean) value);
    } else if (value instanceof JsonArray) {
      return this.or().in(param, (JsonArray) value);
    } else {
      return this;
    }
  }

  public WhereQuery andBetween(String param, Object from, Object to) {
    if (Objects.isNull(from) || Objects.isNull(to)) {
      return this;
    }
    whereQuery.append(SPACE_AND_SPACE);
    whereQuery.append(param);
    whereQuery.append(GREATER_THAN_EQUAL_VALUE);
    whereQuery.append(SPACE_AND_SPACE);
    whereQuery.append(param);
    whereQuery.append(LESS_THAN_EQUAL_VALUE);

    tuple.addValue(from);
    tuple.addValue(to);
    return this;
  }

  public String build() {
    if (whereQuery.length() > 0) {
      whereQuery.delete(1, 4);
      return SPACE_WHERE_SPACE + whereQuery.toString();
    }
    return null;
  }
}

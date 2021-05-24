package com.learn.college.common.interfaces;

import io.vertx.sqlclient.Row;

public interface BaseRowMapper<T> {
  T mapRow(Row rs);
}

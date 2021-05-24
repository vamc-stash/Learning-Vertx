package com.learn.college.common.lib;

import com.learn.college.common.util.DbUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/** The type Database resource */
public class DatabaseResource {

  private DatabaseResource() {
    // do nothing
  }

  private static DbUtils piData;

  /**
   * Initialize Primary data resource
   *
   * @param vertx Vertx instance
   * @param dbConfig the db config
   */
  public static void initializePIDataResource(Vertx vertx, JsonObject dbConfig) {
    piData = new DbUtils(vertx, dbConfig);
  }

  /**
   * Method gets pi data resource
   *
   * @return piData
   */
  public static DbUtils getPiDataResource() {
    return piData;
  }
}

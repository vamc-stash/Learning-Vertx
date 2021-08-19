package com.learn.college.api.health;

import com.learn.college.api.health.procedures.DbConnectionHandler;
import com.learn.college.api.health.procedures.DiskSpaceAvailHandler;
import com.learn.college.common.config.EnumConstants;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;

public class HealthProcedureHandlerFactory {

  private HealthProcedureHandlerFactory() {}

  public static Handler<Promise<Status>> getHealthProcedureHandler(
      EnumConstants.HEALTH_PROCEDURE healthProcedure, JsonObject options) {
    Handler<Promise<Status>> healthProcedureHandler = null;

    switch (healthProcedure) {
      case DB_CONNECTION_STATUS:
        healthProcedureHandler = new DbConnectionHandler(options);
        break;
      case DISK_SPACE_AVAILABILITY_STATUS:
        healthProcedureHandler = new DiskSpaceAvailHandler(options);
      default:
    }
    return healthProcedureHandler;
  }
}

package com.learn.college.api.health;

import com.learn.college.common.config.ApplicationConfiguration;
import com.learn.college.common.config.EnumConstants;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;

import static com.learn.college.common.config.Constants.PATH_PING;
import static com.learn.college.common.config.Constants.PROCEDURES;

public class HealthService {

  private static final HealthService healthService = new HealthService();

  public static HealthService getInstance() {
    return healthService;
  }

  public void registerProcedures(HealthCheckHandler healthCheckHandler) {
    JsonObject procedures = ApplicationConfiguration.getHealthOptions().getJsonObject(PROCEDURES);

    if (procedures != null) {
      procedures
          .getMap()
          .keySet()
          .forEach(
              procedure -> {
                EnumConstants.HEALTH_PROCEDURE healthProcedure = EnumConstants.HEALTH_PROCEDURE.valueOf(procedure);
                Handler<Promise<Status>> procedureHandler =
                    HealthProcedureHandlerFactory.getHealthProcedureHandler(
                        healthProcedure, procedures.getJsonObject(healthProcedure.toString()));
                if (procedureHandler != null) {
                  healthCheckHandler.register(healthProcedure.toString(), procedureHandler);
                }
              });
    } else {
      healthCheckHandler.register(PATH_PING, promise -> promise.complete(Status.OK()));
    }
  }
}

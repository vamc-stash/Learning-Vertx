package com.learn.college.common.config;

import com.learn.college.api.helper.RoleManagementHelper;
import com.learn.college.common.lib.DatabaseResource;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** The type Application Startup */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationStartup {

  /**
   * Start.
   *
   * @param vertx Vertx instance
   * @param verticle Verticle name
   * @param startUpHandler the handler
   */
  public static void start(
      Vertx vertx, String verticle, Handler<AsyncResult<Void>> startUpHandler) {
    try {
      ApplicationConfiguration.initialize(
          vertx,
          configHandler -> {
            if (configHandler.succeeded()) {
              DatabaseResource.initializePIDataResource(
                  vertx, ApplicationConfiguration.getPrimaryResourceConfig());

              // loading resources, roles, role_permissions data
              RoleManagementHelper.loadRolePermissionData()
                  .onSuccess(success -> deployVerticle(vertx, verticle, startUpHandler))
                  .onFailure(
                      failure ->
                          startUpHandler.handle(
                              Future.failedFuture(failure.getCause().getMessage())));
            } else {
              startUpHandler.handle(
                  Future.failedFuture(
                      "Config Retriever failed " + configHandler.cause().getMessage()));
            }
          });
    } catch (RuntimeException e) {
      startUpHandler.handle(
          Future.failedFuture(new RuntimeException("Application Startup failed")));
    }
  }

  /**
   * Responsible for deploying verticle
   *
   * @param vertx Vertx instance
   * @param verticle Verticle to be deployed
   * @param startUpHandler the handler
   */
  private static void deployVerticle(
      Vertx vertx, String verticle, Handler<AsyncResult<Void>> startUpHandler) {
    vertx.deployVerticle(
        verticle,
        ApplicationConfiguration.getDeploymentOptions(),
        deploymentHandler -> {
          if (deploymentHandler.succeeded()) {
            startUpHandler.handle(Future.succeededFuture());
          } else {
            startUpHandler.handle(
                Future.failedFuture(
                    "Verticle Deployment Failed," + deploymentHandler.cause().getMessage()));
          }
        });
  }
}

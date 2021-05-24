package com.learn.college;

import com.learn.college.api.verticles.UserVerticle;
import com.learn.college.common.config.ApplicationConfiguration;
import com.learn.college.common.config.ApplicationStartup;
import io.vertx.core.Vertx;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Starting Point for the CollegeServer */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CollegeServerMain {

  /**
   * The entry point of application
   *
   * @param args input arguments
   */
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(e -> log.error("Exception occurred ", e));

    ApplicationStartup.start(
        vertx,
        UserVerticle.class.getName(),
        startUpHandler -> {
          if (startUpHandler.succeeded()) {
            log.info(
                "\nApplicationName: {}\n Version: {}\n Port: {}\n Status: {}",
                ApplicationConfiguration.getApplicationName(),
                ApplicationConfiguration.getApplicationVersion(),
                ApplicationConfiguration.getHttpServerOptions().getPort(),
                "Running");
          } else {
            log.error(
                "\nApplicationName: {}\n Version: {}\n Port: {}\n Status: {}, {}",
                ApplicationConfiguration.getApplicationName(),
                ApplicationConfiguration.getApplicationVersion(),
                ApplicationConfiguration.getHttpServerOptions().getPort(),
                "Failed",
                startUpHandler.cause());
          }
        });
  }
}

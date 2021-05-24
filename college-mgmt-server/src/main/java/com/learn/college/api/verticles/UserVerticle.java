package com.learn.college.api.verticles;

import com.learn.college.api.auth.AuthenticateHandler;
import com.learn.college.api.auth.AuthorizeHandler;
import com.learn.college.api.rest.CollegeRest;
import com.learn.college.api.rest.UserRest;
import com.learn.college.common.config.ApplicationConfiguration;
import com.learn.college.common.util.RestUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.openapi.RouterBuilderOptions;
import lombok.extern.slf4j.Slf4j;

import static com.learn.college.common.config.Constants.*;

/** The type UserRest */
@Slf4j
public class UserVerticle extends AbstractVerticle {

  private AuthenticateHandler authenticateHandler;
  private AuthorizeHandler authorizeHandler;

  private static final UserRest userRest = UserRest.getInstance();
  private static final CollegeRest collegeRest = CollegeRest.getInstance();

  private HttpServer httpServer;

  @Override
  public void start(Promise<Void> startFuture) {
    vertx.exceptionHandler(e -> log.error("Exception occurred ", e));
    log.info("Swagger " + SWAGGER);
    // OpenAPI3 contract
    RouterBuilder.create(
        vertx,
        SWAGGER,
        ar -> {
          if (ar.succeeded()) {
            log.info("Swagger loaded successfully");

            // prepare router builder
            RouterBuilder routerBuilder = ar.result();
            RouterBuilderOptions builderOptions =
                new RouterBuilderOptions().setMountNotImplementedHandler(false);
            routerBuilder.setOptions(builderOptions);

            userRest.addHandlerByOperationId(routerBuilder);
            collegeRest.addHandlerByOperationId(routerBuilder);

            final Router router = routerBuilder.createRouter();

            router
                .get(PATH_PING)
                .handler(ctx -> ctx.response().setStatusCode(200).end("College Server is running"));

            router
                .getRoutes()
                .forEach(route -> log.info("{}-{}", route.methods(), route.getPath()));

            authenticateHandler = AuthenticateHandler.create(vertx);
            authorizeHandler = AuthorizeHandler.create();

            router.route().handler(BodyHandler.create());
            RestUtils.enableCorsSupport(router);

            // adding authenticate and authorize handlers
            router.route(PATH_COLLEGE_BASE).handler(authenticateHandler).handler(authorizeHandler);

            userRest.mountRouter(vertx, router);
            collegeRest.mountRouter(vertx, router);

            httpServer = vertx.createHttpServer(ApplicationConfiguration.getHttpServerOptions());
            httpServer
                .requestHandler(router)
                .listen(
                    serverHandler -> {
                      if (serverHandler.succeeded()) {
                        log.info("College Server is running");
                        startFuture.complete();
                      } else {
                        log.error("College server is failed to start");
                        startFuture.fail(
                            "Server initialization is failed. " + serverHandler.cause());
                      }
                    });

          } else {
            log.error("OpenAPI3 Router Builder Failed");
            startFuture.fail(ar.cause());
          }
        });
  }

  @Override
  public void stop() {
    if (httpServer != null) {
      httpServer.close();
      log.info("College Server closed");
    }
    vertx.close();
  }
}

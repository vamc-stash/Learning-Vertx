package com.learn.college.api.rest;

import com.learn.college.common.interfaces.BaseRest;
import com.learn.college.common.util.RestUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import lombok.extern.slf4j.Slf4j;

import static com.learn.college.common.config.Constants.*;

@Slf4j
public class CollegeRest implements BaseRest {

  private static final CollegeRest collegeRest = new CollegeRest();

  /**
   * Gets Singleton instance
   *
   * @return CollegeRest instance
   */
  public static CollegeRest getInstance() {
    return collegeRest;
  }

  @Override
  public void mountRouter(Vertx vertx, Router router) {
    router.mountSubRouter(PATH_COLLEGE_MOUNT_POINT, getRouter(vertx));
  }

  @Override
  public void addHandlerByOperationId(RouterBuilder routerBuilder) {
    routerBuilder.operation(USER_SIGN_IN_PING).handler(RoutingContext::next);
  }

  @Override
  public Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.get(PATH_PING).handler(this::testRoute).failureHandler(RestUtils::handleError);

    return router;
  }

  /**
   * This method is used for testing sign in
   *
   * @param context RoutingContext
   */
  private void testRoute(RoutingContext context) {
    if (context.user() == null
        || context.user().principal() == null
        || context.user().principal().getString(DB_USER_ID) == null) {
      log.error("User id not found");
      RestUtils.handleBadRequest(context, new Throwable("user id not found"));
    }
    JsonObject json = context.user().principal();
    RestUtils.handleSuccess(context, json);
  }
}

package com.learn.college.api.rest;

import com.learn.college.api.service.UserService;
import com.learn.college.common.config.EnumConstants;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.interfaces.BaseRest;
import com.learn.college.common.util.CommonUtils;
import com.learn.college.common.util.RestUtils;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.learn.college.common.config.Constants.*;

/** The type User Rest */
@Slf4j
public class UserRest implements BaseRest {

  private static final UserRest userRest = new UserRest();

  private static final UserService userService = UserService.getInstance();

  /**
   * Gets Singleton instance
   *
   * @return UserRest instance
   */
  public static UserRest getInstance() {
    return userRest;
  }

  /**
   * Mount router to mount point
   *
   * @param vertx Vertx instance
   * @param router Router
   */
  @Override
  public void mountRouter(Vertx vertx, Router router) {
    router.mountSubRouter(PATH_COLLEGE_AUTH_BASE, getRouter(vertx));
  }

  /**
   * Add swagger operations
   *
   * @param routerBuilder RouterBuilder
   */
  @Override
  public void addHandlerByOperationId(RouterBuilder routerBuilder) {
    routerBuilder.operation(USER_SIGN_UP).handler(RoutingContext::next);
    routerBuilder.operation(USER_SIGN_IN).handler(RoutingContext::next);
  }

  /**
   * Adds handlers to API paths and returns {@link Router}
   *
   * @param vertx Vertx instance
   */
  @Override
  public Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    router
        .post(PATH_SIGN_UP)
        .consumes(APPLICATION_JSON)
        .handler(RestUtils.parseBody(UserDTO.class))
        .handler(this::handleSignUp)
        .handler(RestUtils::handleError);
    router
        .post(PATH_SIGN_IN)
        .consumes(APPLICATION_JSON)
        .handler(RestUtils.parseBody(UserDTO.class))
        .handler(this::handleSignIn)
        .handler(RestUtils::handleError);
    return router;
  }

  /**
   * Method responsible for handling User SignUp request
   *
   * @param context RoutingContext
   */
  private void handleSignUp(RoutingContext context) {
    UserDTO userDTO = context.get(BODY);
    if (CommonUtils.isValidEmail(userDTO.getEmail())
        && CommonUtils.isValidPassword(userDTO.getPassword())) {
      userService.findUserByEmail(
          userDTO.getEmail(),
          handler -> {
            if (handler.succeeded()) {
              UserDTO user = handler.result();
              if (Objects.nonNull(user)) {
                log.error("User with given email id already exists");
                context.fail(new Throwable("User with given email id already exists"));
                context.next();
              }
              userService.insertUser(
                  userDTO,
                  userHandler -> {
                    if (userHandler.succeeded()) {
                      log.debug("User is inserted into users table");
                      userService.insertUserRole(
                          userDTO.getUserId(),
                          EnumConstants.ROLES.ADMIN.getValue(),
                          userRoleHandler -> {
                            if (userRoleHandler.succeeded()) {
                              log.debug("Admin User signup success");
                              RestUtils.handleSuccess(context, ADMIN_USER_SIGN_UP_SUCCESS);
                            } else {
                              log.error("Admin User signup failure");
                              RestUtils.handleBadRequest(context, userRoleHandler.cause());
                            }
                          });
                    } else {
                      RestUtils.handleBadRequest(context, userHandler.cause());
                    }
                  });
            } else {
              context.fail(handler.cause());
              context.next();
            }
          });
    } else {
      context.fail(new Throwable("Email/Password is not valid"));
      context.next();
    }
  }

  /**
   * Method responsible for handling User SignIn request
   *
   * @param context RoutingContext
   */
  private void handleSignIn(RoutingContext context) {
    UserDTO userDTO = context.get(BODY);
    userService.verifyUserByEmail(userDTO, RestUtils.dataHandler(context));
  }
}

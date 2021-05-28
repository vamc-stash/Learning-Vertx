package com.learn.college.api.rest;

import com.learn.college.api.helper.UserServiceHelper;
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

import static com.learn.college.common.config.Constants.*;

/** The type User Rest */
@Slf4j
public class UserRest implements BaseRest {

  private static final UserRest userRest = new UserRest();

  private static final UserService userService = UserService.getInstance();

  private static final String EMAIL_OR_PASSWORD_NOT_VALID = "Email/Password is not valid";
  private static final String PASSWORD_CHANGE_SUCCESS = "Password is changed successfully";

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
    routerBuilder.operation(RESET_PASS).handler(RoutingContext::next);
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
    router
        .post(PATH_RESET_PASS)
        .consumes(APPLICATION_JSON)
        .handler(RestUtils.parseBody(UserDTO.class))
        .handler(this::handleResetPassword)
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
    userDTO.setRoleId(EnumConstants.ROLES.ADMIN.getValue());
    if (CommonUtils.isValidEmail(userDTO.getEmail())
        && CommonUtils.isValidPassword(userDTO.getPassword())) {
      UserServiceHelper.addUser(userDTO, context);
    } else {
      context.fail(new Throwable(EMAIL_OR_PASSWORD_NOT_VALID));
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

  /**
   * Method allows to change password
   *
   * @param context RoutingContext
   */
  private void handleResetPassword(RoutingContext context) {
    UserDTO userDTO = context.get(BODY);
    userService.handleResetPassword(
        userDTO, RestUtils.voidHandler(context, PASSWORD_CHANGE_SUCCESS));
  }
}

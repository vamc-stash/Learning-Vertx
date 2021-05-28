package com.learn.college.api.helper;

import com.learn.college.api.service.UserService;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.util.RestUtils;
import io.vertx.ext.web.RoutingContext;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.learn.college.common.config.Constants.USER_SIGN_UP_SUCCESS;

/** Class responsible for inserting user data */
@UtilityClass
@Slf4j
public class UserServiceHelper {

  private static final UserService userService = UserService.getInstance();

  private static final String USER_EMAIL_ALREADY_EXISTS = "User with given Email ID already exists";
  private static final String USER_INSERTED = "User is inserted into users table";

  public static void addUser(UserDTO userDTO, RoutingContext context) {
    userService.findUserByEmail(
        userDTO.getEmail(),
        handler -> {
          if (handler.succeeded()) {
            UserDTO user = handler.result();
            if (Objects.nonNull(user)) {
              log.error(USER_EMAIL_ALREADY_EXISTS);
              context.fail(new Throwable(USER_EMAIL_ALREADY_EXISTS));
              context.next();
            }
            userService.insertUser(
                userDTO,
                userHandler -> {
                  if (userHandler.succeeded()) {
                    log.debug(USER_INSERTED);
                    userService.insertUserRole(
                        userDTO.getUserId(),
                        userDTO.getRoleId(),
                        userRoleHandler -> {
                          if (userRoleHandler.succeeded()) {
                            log.debug("User of roleId: {} signup success", userDTO.getRoleId());
                            RestUtils.handleSuccess(context, USER_SIGN_UP_SUCCESS);
                          } else {
                            log.error("User of roleId: {} signup failure", userDTO.getRoleId());
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
  }
}

package com.learn.college.api.service.impl;

import com.learn.college.api.auth.AuthService;
import com.learn.college.api.dao.UserDAO;
import com.learn.college.api.dao.UserRoleDAO;
import com.learn.college.api.service.UserService;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.util.CommonUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

import static com.learn.college.common.config.Constants.PERMISSION_ROLE_ID;
import static com.learn.college.common.config.Constants.VALIDATION_SUCCESS;

public class UserServiceImpl implements UserService {

  private static final UserDAO userDAO = UserDAO.getInstance();

  private static final UserRoleDAO userRoleDAO = UserRoleDAO.getInstance();

  private static final AuthService authService = AuthService.getInstance();

  private static final String USER_DOES_NOT_EXIST = "User does not exist";

  @Override
  public void findUserByEmail(String email, Handler<AsyncResult<UserDTO>> handler) {
    userDAO.findUserByEmail(email, handler);
  }

  @Override
  public void insertUser(UserDTO userDTO, Handler<AsyncResult<Long>> handler) {
    userDTO.setUserId(CommonUtils.generateUUID().toString());
    userDTO.setPassword(authService.generatePasswordHash(userDTO.getPassword()));
    userDAO.insertUser(userDTO, handler);
  }

  @Override
  public void insertUserRole(String userId, int roleId, Handler<AsyncResult<Long>> handler) {
    userDAO.insertUserRole(userId, roleId, handler);
  }

  @Override
  public void verifyUserByEmail(UserDTO inputUserDTO, Handler<AsyncResult<UserDTO>> handler) {
    userDAO.findUserByEmail(
        inputUserDTO.getEmail(),
        ar -> {
          if (ar.succeeded()) {
            UserDTO userDTO = ar.result();
            if (Objects.isNull(userDTO)) {
              handler.handle(Future.failedFuture(USER_DOES_NOT_EXIST));
            } else {
              validateUser(userDTO, inputUserDTO.getPassword(), handler);
            }
          } else {
            handler.handle(Future.failedFuture(ar.cause()));
          }
        });
  }

  private void validateUser(
      UserDTO userDTO, String password, Handler<AsyncResult<UserDTO>> handler) {
    String passwordValidation = authService.validatePassword(userDTO, password);

    if (!VALIDATION_SUCCESS.equals(passwordValidation)) {
      handler.handle(Future.failedFuture(passwordValidation));
      return;
    }
    userRoleDAO.getRoleIdByUserId(
        userDTO.getUserId(),
        ar -> {
          if (ar.succeeded()) {
            List<JsonObject> result = ar.result();
            if (result.isEmpty()) {
              handler.handle(Future.failedFuture("Failed to fetch Role information for the User"));
            } else {
              Integer roleId = result.get(0).getInteger(PERMISSION_ROLE_ID);
              userDTO.setRoleId(roleId);
              userDTO.setAuthToken(authService.generateAuthToken(userDTO));
              handler.handle(Future.succeededFuture(userDTO));
            }
          } else {
            handler.handle(Future.failedFuture(ar.cause()));
          }
        });
  }
}

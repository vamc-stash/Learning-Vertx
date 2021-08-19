package com.learn.college.api.service;

import com.learn.college.api.service.impl.UserServiceImpl;
import com.learn.college.common.dto.UserDTO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/** The UserService Interface */
public interface UserService {

  /**
   * Method gets the instance of UserServiceImpl
   *
   * @return {@link UserServiceImpl}
   */
  static UserServiceImpl getInstance() {
    return UserServiceImpl.getInstance();
  }

  /**
   * Method checks whether User Email exists or not
   *
   * @param email email
   * @param handler the handler
   */
  void findUserByEmail(String email, Handler<AsyncResult<UserDTO>> handler);

  /**
   * Method inserts new User
   *
   * @param userDTO {@link UserDTO}
   * @param handler the handler
   */
  void insertUser(UserDTO userDTO, Handler<AsyncResult<Long>> handler);

  /**
   * Method inserts new UserRole mapping
   *
   * @param userId UserID
   * @param roleId RoleID
   * @param handler the handler
   */
  void insertUserRole(String userId, Integer roleId, Handler<AsyncResult<Long>> handler);

  /**
   * Method verifies user credentials
   *
   * @param inputUserDTO {@link UserDTO}
   * @param handler the handler
   */
  void verifyUserByEmail(UserDTO inputUserDTO, Handler<AsyncResult<UserDTO>> handler);

  /**
   * Method changes password
   *
   * @param inputUserDTO {@link UserDTO}
   * @param handler the handler
   */
  void handleResetPassword(UserDTO inputUserDTO, Handler<AsyncResult<Void>> handler);

  /**
   * Method updates user details
   *
   * @param userDTO {@link UserDTO}
   * @param handler the handler
   */
  void updateUser(UserDTO userDTO, Handler<AsyncResult<Void>> handler);

  /**
   * Method gets all details based on logged in user
   *
   * @param <T> type
   * @param userId user id
   * @param roleId role id
   * @param handler the handler
   */
  <T> void getInfo(String userId, Integer roleId, Handler<AsyncResult<T>> handler);
}

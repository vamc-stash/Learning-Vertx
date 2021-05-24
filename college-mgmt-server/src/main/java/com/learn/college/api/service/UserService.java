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
    return new UserServiceImpl();
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
   * @param userDTO UserDTO
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
  void insertUserRole(String userId, int roleId, Handler<AsyncResult<Long>> handler);

  /**
   * Method verifies user credentials
   *
   * @param userDTO UserDTO
   * @param handler the handler
   */
  void verifyUserByEmail(UserDTO userDTO, Handler<AsyncResult<UserDTO>> handler);
}

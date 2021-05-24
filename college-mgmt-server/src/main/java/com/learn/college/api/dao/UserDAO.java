package com.learn.college.api.dao;

import com.learn.college.api.mapper.UserDTOMapper;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.lib.DatabaseResource;
import com.learn.college.common.util.DbUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/** The type UserDAO */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDAO {

  // Instance for fetching data from DB
  private static final DbUtils primaryDataResource = DatabaseResource.getPiDataResource();

  // Singleton instance of UserDTOMapper
  private static final UserDTOMapper userDTOMapper = UserDTOMapper.getInstance();

  // Singleton instance
  private static final UserDAO userDAO = new UserDAO();

  /**
   * Gets the Singleton instance of UserDAO
   *
   * @return UserDAO instance
   */
  public static UserDAO getInstance() {
    return userDAO;
  }

  /* Query to find user by email */
  private static final String USER_GET_BY_EMAIL = "SELECT * FROM users WHERE email = ?";

  /* Query to insert user */
  private static final String USER_INSERT =
      "INSERT INTO users (user_id, first_name, last_name, email, password) VALUES (?, ?, ?, ?, ?)";

  /* Query to map user to its role */
  private static final String USER_ROLE_INSERT =
      "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

  /**
   * Gets the User by email
   *
   * @param email email
   * @param handler async handler
   */
  public void findUserByEmail(String email, Handler<AsyncResult<UserDTO>> handler) {
    Tuple tuple = Tuple.tuple();
    tuple.addString(email);
    primaryDataResource.execRow(USER_GET_BY_EMAIL, tuple, userDTOMapper, handler);
  }

  /**
   * Inserts user
   *
   * @param userDTO UserDTO
   * @param handler async handler
   */
  public void insertUser(UserDTO userDTO, Handler<AsyncResult<Long>> handler) {
    Tuple tuple = Tuple.tuple();
    tuple.addString(userDTO.getUserId());
    if (StringUtils.isNotBlank(userDTO.getFirstName())) {
      tuple.addString(userDTO.getFirstName());
    } else {
      tuple.addString("");
    }
    if (StringUtils.isNotBlank(userDTO.getLastName())) {
      tuple.addString(userDTO.getLastName());
    } else {
      tuple.addString("");
    }
    tuple.addString(userDTO.getEmail());
    tuple.addString(userDTO.getPassword());
    primaryDataResource.execInsert(USER_INSERT, tuple, handler);
  }

  /**
   * Inserts user role mapping
   *
   * @param userId user id
   * @param roleId role id
   * @param handler async handler
   */
  public void insertUserRole(String userId, int roleId, Handler<AsyncResult<Long>> handler) {
    Tuple tuple = Tuple.tuple();
    tuple.addString(userId);
    tuple.addInteger(roleId);
    primaryDataResource.execInsert(USER_ROLE_INSERT, tuple, handler);
  }
}

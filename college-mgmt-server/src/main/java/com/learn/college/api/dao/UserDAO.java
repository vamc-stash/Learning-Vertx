package com.learn.college.api.dao;

import com.learn.college.api.mapper.UserDTOMapper;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.lib.DatabaseResource;
import com.learn.college.common.lib.UpdateQuery;
import com.learn.college.common.lib.WhereQuery;
import com.learn.college.common.util.DbUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.learn.college.common.config.Constants.*;

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

  /* Query to select users based on role */
  private static final String SELECT_USERS_BY_ROLE =
      "SELECT * FROM users u WHERE u.user_id in (SELECT ur.user_id FROM user_roles ur WHERE ur.role_id = ?)";

  /* Query to select user based on Id */
  private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";

  /* Query to select students under a teacher */
  private static final String SELECT_STUDENTS_BY_TEACHER_ID =
      "SELECT * FROM users WHERE user_id IN "
          + "(SELECT student_id FROM student_courses WHERE course_id IN "
          + "(SELECT course_id FROM courses WHERE teacher_id = ?))";

  /**
   * /** Gets the User by email
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
  public void insertUserRole(String userId, Integer roleId, Handler<AsyncResult<Long>> handler) {
    Tuple tuple = Tuple.tuple();
    tuple.addString(userId);
    tuple.addInteger(roleId);
    primaryDataResource.execInsert(USER_ROLE_INSERT, tuple, handler);
  }

  /**
   * Updated user details
   *
   * @param userDTO {@link UserDTO}
   * @param handler async handler
   */
  public void updateUserDetails(UserDTO userDTO, Handler<AsyncResult<Void>> handler) {
    UpdateQuery updateQuery = new UpdateQuery(USERS_TABLE);
    if (StringUtils.isNotBlank(userDTO.getFirstName())) {
      updateQuery.add(DB_FIRST_NAME, userDTO.getFirstName());
    }
    if (StringUtils.isNotBlank(userDTO.getLastName())) {
      updateQuery.add(DB_LAST_NAME, userDTO.getLastName());
    }
    if (StringUtils.isNotBlank(userDTO.getResetPassword())) {
      updateQuery.add(DB_PASSWORD, userDTO.getResetPassword());
    }

    WhereQuery whereQuery = updateQuery.where();
    whereQuery.andAdd(DB_USER_ID, userDTO.getUserId());
    updateQuery.build();
    primaryDataResource.execUpdate(
        updateQuery.getFinalUpdateQuery(), updateQuery.getTuple(), handler);
  }

  /**
   * Gets All Users By roleId
   *
   * @param roleId role id
   * @return future
   */
  public Future<List<UserDTO>> getUsersByRole(Integer roleId) {
    Promise<List<UserDTO>> promise = Promise.promise();
    Tuple tuple = Tuple.tuple();
    tuple.addInteger(roleId);
    primaryDataResource.execGet(
        SELECT_USERS_BY_ROLE,
        tuple,
        userDTOMapper,
        handler -> {
          if (handler.succeeded()) {
            promise.complete(handler.result());
          } else {
            promise.fail(handler.cause());
          }
        });
    return promise.future();
  }

  /**
   * Gets User details
   *
   * @param userId user id
   * @return future
   */
  public Future<UserDTO> getUsersById(String userId) {
    Promise<UserDTO> promise = Promise.promise();
    Tuple tuple = Tuple.tuple();
    tuple.addString(userId);
    primaryDataResource.execGet(
        SELECT_USER_BY_ID,
        tuple,
        userDTOMapper,
        handler -> {
          if (handler.succeeded()) {
            promise.complete(handler.result().get(0));
          } else {
            promise.fail(handler.cause());
          }
        });
    return promise.future();
  }

  /**
   * Gets All Students under a teacher
   *
   * @param userId teacher id
   * @return future
   */
  public Future<List<UserDTO>> getUsersByTeacherId(String userId) {
    Promise<List<UserDTO>> promise = Promise.promise();
    Tuple tuple = Tuple.tuple();
    tuple.addString(userId);
    primaryDataResource.execGet(
        SELECT_STUDENTS_BY_TEACHER_ID,
        tuple,
        userDTOMapper,
        handler -> {
          if (handler.succeeded()) {
            promise.complete(handler.result());
          } else {
            promise.fail(handler.cause());
          }
        });
    return promise.future();
  }
}

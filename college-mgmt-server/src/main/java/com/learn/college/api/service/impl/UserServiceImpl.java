package com.learn.college.api.service.impl;

import com.learn.college.api.auth.AuthService;
import com.learn.college.api.dao.CourseDAO;
import com.learn.college.api.dao.UserDAO;
import com.learn.college.api.dao.UserRoleDAO;
import com.learn.college.api.service.UserService;
import com.learn.college.common.config.EnumConstants;
import com.learn.college.common.dto.*;
import com.learn.college.common.util.CommonUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
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

  private static final CourseDAO courseDao = CourseDAO.getInstance();

  private static final AuthService authService = AuthService.getInstance();

  private static final String USER_DOES_NOT_EXIST = "User does not exist";
  private static final String USER_ROLE_GET_FAILED =
      "Failed to fetch Role information for the User";

  /**
   * {@inheritDoc}
   *
   * @param email email
   * @param handler the handler
   */
  @Override
  public void findUserByEmail(String email, Handler<AsyncResult<UserDTO>> handler) {
    userDAO.findUserByEmail(email, handler);
  }

  /**
   * {@inheritDoc}
   *
   * @param userDTO {@link UserDTO}
   * @param handler the handler
   */
  @Override
  public void insertUser(UserDTO userDTO, Handler<AsyncResult<Long>> handler) {
    userDTO.setUserId(CommonUtils.generateUUID().toString());
    userDTO.setPassword(authService.generatePasswordHash(userDTO.getPassword()));
    userDAO.insertUser(userDTO, handler);
  }

  /**
   * {@inheritDoc}
   *
   * @param userId UserID
   * @param roleId RoleID
   * @param handler the handler
   */
  @Override
  public void insertUserRole(String userId, Integer roleId, Handler<AsyncResult<Long>> handler) {
    userDAO.insertUserRole(userId, roleId, handler);
  }

  /**
   * {@inheritDoc}
   *
   * @param inputUserDTO {@link UserDTO}
   * @param handler the handler
   */
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
              handler.handle(Future.failedFuture(USER_ROLE_GET_FAILED));
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

  /**
   * {@inheritDoc}
   *
   * @param inputUserDTO {@link UserDTO}
   * @param handler the handler
   */
  @Override
  public void handleResetPassword(UserDTO inputUserDTO, Handler<AsyncResult<Void>> handler) {
    userDAO.findUserByEmail(
        inputUserDTO.getEmail(),
        ar -> {
          if (ar.succeeded()) {
            UserDTO userDTO = ar.result();
            if (Objects.isNull(userDTO)) {
              handler.handle(Future.failedFuture(USER_DOES_NOT_EXIST));
            } else {
              String passwordValidation =
                  authService.validatePassword(userDTO, inputUserDTO.getPassword());
              if (!VALIDATION_SUCCESS.equals(passwordValidation)) {
                handler.handle(Future.failedFuture(passwordValidation));
                return;
              }
              inputUserDTO.setUserId(userDTO.getUserId());
              inputUserDTO.setResetPassword(
                  authService.generatePasswordHash(inputUserDTO.getResetPassword()));
              userDAO.updateUserDetails(inputUserDTO, handler);
            }
          } else {
            handler.handle(Future.failedFuture(ar.cause()));
          }
        });
  }

  /**
   * {@inheritDoc}
   *
   * @param userDTO {@link UserDTO}
   * @param handler the handler
   */
  @Override
  public void updateUser(UserDTO userDTO, Handler<AsyncResult<Void>> handler) {
    userDAO.updateUserDetails(userDTO, handler);
  }

  /**
   * {@inheritDoc}
   *
   * @param <T> type
   * @param userId user id
   * @param roleId role id
   * @param handler the handler
   */
  @Override
  public <T> void getInfo(String userId, Integer roleId, Handler<AsyncResult<T>> handler) {
    Future<UserDTO> userFuture = userDAO.getUsersById(userId);

    if (roleId.equals(EnumConstants.ROLES.ADMIN.getValue())) {
      AdminDTO adminDTO = new AdminDTO();

      Future<List<UserDTO>> studentsFuture =
          userDAO.getUsersByRole(EnumConstants.ROLES.STUDENT.getValue());
      Future<List<UserDTO>> teachersFuture =
          userDAO.getUsersByRole(EnumConstants.ROLES.TEACHER.getValue());
      Future<List<CourseDTO>> coursesFuture = courseDao.getAllCourses();

      CompositeFuture.all(userFuture, coursesFuture, studentsFuture, teachersFuture)
          .onSuccess(
              success -> {
                adminDTO.setFirstName(userFuture.result().getFirstName());
                adminDTO.setLastName(userFuture.result().getLastName());
                adminDTO.setEmail(userFuture.result().getEmail());
                adminDTO.addCourses(coursesFuture.result());
                adminDTO.addStudents(studentsFuture.result());
                adminDTO.addTeachers(teachersFuture.result());
                handler.handle((AsyncResult<T>) Future.succeededFuture(adminDTO));
              })
          .onFailure(failure -> handler.handle(Future.failedFuture(failure.getCause())));
    } else if (roleId.equals(EnumConstants.ROLES.TEACHER.getValue())) {
      TeacherDTO teacherDTO = new TeacherDTO();
      Future<List<UserDTO>> studentsFuture = userDAO.getUsersByTeacherId(userId);
      Future<List<CourseDTO>> coursesFuture = courseDao.getTeacherCourses(userId);

      CompositeFuture.all(userFuture, coursesFuture, studentsFuture)
          .onSuccess(
              success -> {
                teacherDTO.setFirstName(userFuture.result().getFirstName());
                teacherDTO.setLastName(userFuture.result().getLastName());
                teacherDTO.setEmail(userFuture.result().getEmail());
                teacherDTO.addCourses(coursesFuture.result());
                teacherDTO.addStudents(studentsFuture.result());

                handler.handle((AsyncResult<T>) Future.succeededFuture(teacherDTO));
              })
          .onFailure(failure -> handler.handle(Future.failedFuture(failure.getCause())));
    } else {
      StudentDTO studentDTO = new StudentDTO();
      Future<List<CourseDTO>> coursesFuture = courseDao.getAllCourses();
      Future<List<CourseDTO>> registeredCoursesFuture = courseDao.getStudentCourses(userId);

      CompositeFuture.all(userFuture, coursesFuture, registeredCoursesFuture)
          .onSuccess(
              success -> {
                studentDTO.setFirstName(userFuture.result().getFirstName());
                studentDTO.setLastName(userFuture.result().getLastName());
                studentDTO.setEmail(userFuture.result().getEmail());
                studentDTO.addCourses(coursesFuture.result());
                studentDTO.setRegisteredCourses(registeredCoursesFuture.result());

                handler.handle((AsyncResult<T>) Future.succeededFuture(studentDTO));
              })
          .onFailure(failure -> handler.handle(Future.failedFuture(failure.getCause())));
    }
  }
}

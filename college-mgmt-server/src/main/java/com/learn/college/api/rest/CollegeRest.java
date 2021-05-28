package com.learn.college.api.rest;

import com.learn.college.api.helper.UserServiceHelper;
import com.learn.college.api.service.CourseService;
import com.learn.college.api.service.UserService;
import com.learn.college.common.dto.CourseDTO;
import com.learn.college.common.dto.StudentCourseDTO;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.interfaces.BaseRest;
import com.learn.college.common.util.CommonUtils;
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

  private static final CourseService courseService = CourseService.getInstance();

  private static final UserService userService = UserService.getInstance();

  private static final String COURSE_ADD_SUCCESS = "Course added successfully";
  private static final String USER_ID_NOT_FOUND = "User ID is not found";
  private static final String EMAIL_OR_PASSWORD_NOT_VALID = "Email/Password is not valid";
  private static final String COURSE_REGISTRATION_SUCCESS = "Course registration is successful";
  private static final String USER_DETAILS_UPDATE_SUCCESS = "User details are updated successfully";

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
    routerBuilder.operation(ADD_USER).handler(RoutingContext::next);
    routerBuilder.operation(ADD_COURSE).handler(RoutingContext::next);
    routerBuilder.operation(UPDATE_USER).handler(RoutingContext::next);
    routerBuilder.operation(GET_INFO).handler(RoutingContext::next);
    routerBuilder.operation(REGISTER_COURSES).handler(RoutingContext::next);
  }

  @Override
  public Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.get(PATH_PING).handler(this::testRoute).failureHandler(RestUtils::handleError);

    router.get(PATH_GET).handler(this::getInfo).handler(RestUtils::handleError);

    router
        .post(PATH_ADD_USER)
        .consumes(APPLICATION_JSON)
        .handler(RestUtils.parseBody(UserDTO.class))
        .handler(this::addUser)
        .handler(RestUtils::handleError);

    router
        .post(PATH_UPDATE_USER)
        .consumes(APPLICATION_JSON)
        .handler(RestUtils.parseBody(UserDTO.class))
        .handler(this::updateUser)
        .handler(RestUtils::handleError);

    router
        .post(PATH_ADD_COURSE)
        .consumes(APPLICATION_JSON)
        .handler(RestUtils.parseBody(CourseDTO.class))
        .handler(this::addCourse)
        .handler(RestUtils::handleError);

    router
        .post(PATH_REGISTER_COURSES)
        .consumes(APPLICATION_JSON)
        .handler(RestUtils.parseBody(StudentCourseDTO.class))
        .handler(this::registerCourse)
        .handler(RestUtils::handleError);

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
      log.error(USER_ID_NOT_FOUND);
      RestUtils.handleBadRequest(context, new Throwable(USER_ID_NOT_FOUND));
    }
    JsonObject json = context.user().principal();
    RestUtils.handleSuccess(context, json);
  }

  /**
   * This method adds User with specific role
   *
   * @param context RoutingContext
   */
  private void addUser(RoutingContext context) {
    UserDTO userDTO = context.get(BODY);
    if (CommonUtils.isValidEmail(userDTO.getEmail())
        && CommonUtils.isValidPassword(userDTO.getPassword())) {
      UserServiceHelper.addUser(userDTO, context);
    } else {
      context.fail(new Throwable(EMAIL_OR_PASSWORD_NOT_VALID));
      context.next();
    }
  }

  /**
   * This method adds Course
   *
   * @param context RoutingContext
   */
  private void addCourse(RoutingContext context) {
    CourseDTO courseDTO = context.get(BODY);
    courseService.addCourse(courseDTO, RestUtils.voidHandler(context, COURSE_ADD_SUCCESS));
  }

  /**
   * This method updates user details
   *
   * @param context RoutingContext
   */
  private void updateUser(RoutingContext context) {
    if (context.user() == null
        || context.user().principal() == null
        || context.user().principal().getString(DB_USER_ID) == null) {
      log.error(USER_ID_NOT_FOUND);
      RestUtils.handleBadRequest(context, new Throwable(USER_ID_NOT_FOUND));
    }
    UserDTO userDTO = context.get(BODY);
    String userId = context.user().principal().getString(DB_USER_ID);
    userDTO.setUserId(userId);
    userService.updateUser(userDTO, RestUtils.voidHandler(context, USER_DETAILS_UPDATE_SUCCESS));
  }

  /**
   * This method allows users of role STUDENT to register for courses
   *
   * @param context RoutingContext
   */
  private void registerCourse(RoutingContext context) {
    if (context.user() == null
        || context.user().principal() == null
        || context.user().principal().getString(DB_USER_ID) == null) {
      log.error(USER_ID_NOT_FOUND);
      RestUtils.handleBadRequest(context, new Throwable(USER_ID_NOT_FOUND));
    }
    StudentCourseDTO studentCourseDTO = context.get(BODY);
    courseService.registerCourses(
        studentCourseDTO.getCourseIdList(),
        context.user().principal().getString(DB_USER_ID),
        RestUtils.voidHandler(context, COURSE_REGISTRATION_SUCCESS));
  }

  /**
   * This method gets all details based on logged in user
   *
   * @param context RoutingContext
   */
  private void getInfo(RoutingContext context) {
    if (context.user() == null
        || context.user().principal() == null
        || context.user().principal().getString(DB_USER_ID) == null) {
      log.error(USER_ID_NOT_FOUND);
      RestUtils.handleBadRequest(context, new Throwable(USER_ID_NOT_FOUND));
    }
    String userId = context.user().principal().getString(DB_USER_ID);
    Integer roleId = context.user().principal().getInteger(PERMISSION_ROLE_ID);
    userService.getInfo(userId, roleId, RestUtils.dataHandler(context));
  }
}

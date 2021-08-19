package com.learn.college.api.rest;

import com.learn.college.api.auth.AuthUtils;
import com.learn.college.api.dao.CourseDAO;
import com.learn.college.api.dao.UserDAO;
import com.learn.college.api.mapper.CourseDTOMapper;
import com.learn.college.api.mapper.UserDTOMapper;
import com.learn.college.api.verticles.UserVerticle;
import com.learn.college.common.config.ApplicationConfiguration;
import com.learn.college.common.config.ConfigUtil;
import com.learn.college.common.config.EnumConstants;
import com.learn.college.common.config.TestUtility;
import com.learn.college.common.dto.CourseDTO;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.util.DbUtils;
import com.learn.college.common.util.RestUtils;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import io.vertx.sqlclient.Tuple;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static com.learn.college.common.config.Constants.PATH_UPDATE_USER;
import static com.learn.college.common.config.TestConstants.*;
import static com.learn.college.common.config.TestConstants.BAD_REQUEST_STATUS;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*", "javax.crypto.*"})
@PrepareForTest({AsyncResult.class, DbUtils.class, RestUtils.class})
public class CollegeRestTest {
  private static Vertx vertx;

  private static WebClient client;

  @Mock private DbUtils dbUtils;
  @Mock private AsyncResult<Long> longAsyncResult;
  @Mock private AsyncResult<Void> voidAsyncResult;
  @Mock private AsyncResult<List<UserDTO>> userListAsyncResult;
  @Mock private AsyncResult<List<CourseDTO>> courseListAsyncResult;

  static {
    AuthUtils.setAuthValidationEnabled(EnumConstants.Environment.TEST.toString());
    ConfigUtil.loadSystemProperties();
  }

  @BeforeClass
  public static void setUp(TestContext context) {
    vertx = Vertx.vertx();

    ApplicationConfiguration.initialize(
        vertx,
        ar -> {
          if (ar.succeeded()) {
            DeploymentOptions options = ApplicationConfiguration.getDeploymentOptions();
            vertx.deployVerticle(
                UserVerticle.class.getName(), options, context.asyncAssertSuccess());
          }
          client = WebClient.create(vertx);
        });
  }

  @AfterClass
  public static void tearDown(TestContext testContext) {
    // closes the vertx instance. Un-deploys verticles
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    setInternalState(UserDAO.class, PI_DATA_RESOURCE, dbUtils);
    setInternalState(CourseDAO.class, PI_DATA_RESOURCE, dbUtils);

    PowerMockito.mockStatic(
        RestUtils.class,
        invocationOnMock -> {
          if (invocationOnMock.getMethod().getName().equals("getUserIdFromContext")) {
            return TEST_USER_ID;
          }
          return invocationOnMock.callRealMethod();
        });
  }

  /**
   * This method sets value to private instance variables in Class
   *
   * @param object Class to which variable to set
   * @param fieldName name of the variable
   * @param value instance to be set
   */
  private static void setInternalState(Object object, String fieldName, Object value) {
    Whitebox.setInternalState(object, fieldName, value);
  }

  /**
   * Test Case to add course for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testAddCourse_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Long>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Long>>) invocationOnMock.getArgument(2))
                      .handle(longAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execInsert(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));
    when(longAsyncResult.succeeded()).thenReturn(true);
    when(longAsyncResult.result()).thenReturn(TEST_ID);

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_ADD_COURSE)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            TestUtility.testCourseRequest(),
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(SUCCESS_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to add course for failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testAddCourse_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Long>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Long>>) invocationOnMock.getArgument(2))
                      .handle(longAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execInsert(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));
    when(longAsyncResult.succeeded()).thenReturn(false);

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_ADD_COURSE)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            TestUtility.testCourseRequest(),
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(BAD_REQUEST_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to add course for failure case
   *
   * @param context test context
   */
  @Test
  public void testAddCourseWithInvalidParams_N(TestContext context) {
    final Async async = context.async();

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_ADD_COURSE)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .send(
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(BAD_REQUEST_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }
  /**
   * Test Case to update user for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testUpdateUser_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Void>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Void>>) invocationOnMock.getArgument(2))
                      .handle(Future.succeededFuture());
                  return null;
                })
        .when(dbUtils)
        .execUpdate(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_UPDATE_USER)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            new JsonObject().put("first_name", "wilson").put("last_name", "fisk"),
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(SUCCESS_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to update user for failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testUpdateUser_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Void>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Void>>) invocationOnMock.getArgument(2))
                      .handle(Future.failedFuture("Failed to update"));
                  return null;
                })
        .when(dbUtils)
        .execUpdate(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_UPDATE_USER)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            new JsonObject().put("first_name", "wilson").put("last_name", "fisk"),
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(BAD_REQUEST_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to update user with invalid params for failure case
   *
   * @param context test context
   */
  @Test
  public void testUpdateUserWithInvalidParams_N(TestContext context) {
    final Async async = context.async();

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_UPDATE_USER)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .send(
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(BAD_REQUEST_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to register courses for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testRegisterCourse_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Void>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Void>>) invocationOnMock.getArgument(2))
                      .handle(Future.succeededFuture());
                  return null;
                })
        .when(dbUtils)
        .execBatch(Mockito.anyString(), Mockito.any(), Mockito.any(Handler.class));
    when(voidAsyncResult.succeeded()).thenReturn(true);

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_REGISTER_COURSES)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            TestUtility.testCourseList(),
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(SUCCESS_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to register course for failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testRegisterCourse_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Void>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Void>>) invocationOnMock.getArgument(2))
                      .handle(Future.failedFuture("Failed to register courses"));
                  return null;
                })
        .when(dbUtils)
        .execBatch(Mockito.anyString(), Mockito.any(), Mockito.any(Handler.class));

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_REGISTER_COURSES)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            TestUtility.testCourseList(),
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(BAD_REQUEST_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to register course with invalid params for failure case
   *
   * @param context test context
   */
  @Test
  public void testRegisterCourseWithInvalidParams_N(TestContext context) {
    final Async async = context.async();

    client = WebClient.create(vertx);
    client
        .postAbs(COLLEGE_BASE_URL + PATH_REGISTER_COURSES)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .send(
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(BAD_REQUEST_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to get admin user info for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetAdminInfo_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.mockStatic(
        RestUtils.class,
        invocationOnMock -> {
          if (invocationOnMock.getMethod().getName().equals("getUserIdFromContext")) {
            return TEST_USER_ID;
          } else if (invocationOnMock.getMethod().getName().equals("getPermissionIdFromContext")) {
            return 1;
          }
          return invocationOnMock.callRealMethod();
        });

    List<UserDTO> userDTOList = new ArrayList<>();
    userDTOList.add(TestUtility.testUser());

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<UserDTO>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<UserDTO>>>) invocationOnMock.getArgument(3))
                      .handle(userListAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execGet(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userListAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userListAsyncResult.result()).thenReturn(userDTOList);

    List<CourseDTO> courseDTOList = new ArrayList<>();
    courseDTOList.add(TestUtility.testCourseDTO());

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<CourseDTO>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<CourseDTO>>>) invocationOnMock.getArgument(3))
                      .handle(courseListAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execGet(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(CourseDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(courseListAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(courseListAsyncResult.result()).thenReturn(courseDTOList);

    client = WebClient.create(vertx);
    client
        .getAbs(COLLEGE_BASE_URL + PATH_GET)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .send(
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(SUCCESS_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to get teacher user info for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetTeacherInfo_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.mockStatic(
        RestUtils.class,
        invocationOnMock -> {
          if (invocationOnMock.getMethod().getName().equals("getUserIdFromContext")) {
            return TEST_USER_ID;
          } else if (invocationOnMock.getMethod().getName().equals("getPermissionIdFromContext")) {
            return 2;
          }
          return invocationOnMock.callRealMethod();
        });

    List<UserDTO> userDTOList = new ArrayList<>();
    userDTOList.add(TestUtility.testUser());

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<UserDTO>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<UserDTO>>>) invocationOnMock.getArgument(3))
                      .handle(userListAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execGet(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userListAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userListAsyncResult.result()).thenReturn(userDTOList);

    List<CourseDTO> courseDTOList = new ArrayList<>();
    courseDTOList.add(TestUtility.testCourseDTO());

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<CourseDTO>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<CourseDTO>>>) invocationOnMock.getArgument(3))
                      .handle(courseListAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execGet(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(CourseDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(courseListAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(courseListAsyncResult.result()).thenReturn(courseDTOList);

    client = WebClient.create(vertx);
    client
        .getAbs(COLLEGE_BASE_URL + PATH_GET)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .send(
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(SUCCESS_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }

  /**
   * Test Case to get student user info for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetStudentInfo_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.mockStatic(
        RestUtils.class,
        invocationOnMock -> {
          if (invocationOnMock.getMethod().getName().equals("getUserIdFromContext")) {
            return TEST_USER_ID;
          } else if (invocationOnMock.getMethod().getName().equals("getPermissionIdFromContext")) {
            return 3;
          }
          return invocationOnMock.callRealMethod();
        });

    List<UserDTO> userDTOList = new ArrayList<>();
    userDTOList.add(TestUtility.testUser());

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<UserDTO>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<UserDTO>>>) invocationOnMock.getArgument(3))
                      .handle(userListAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execGet(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userListAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userListAsyncResult.result()).thenReturn(userDTOList);

    List<CourseDTO> courseDTOList = new ArrayList<>();
    courseDTOList.add(TestUtility.testCourseDTO());

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<CourseDTO>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<CourseDTO>>>) invocationOnMock.getArgument(3))
                      .handle(courseListAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execGet(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(CourseDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(courseListAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(courseListAsyncResult.result()).thenReturn(courseDTOList);

    client = WebClient.create(vertx);
    client
        .getAbs(COLLEGE_BASE_URL + PATH_GET)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .send(
            response -> {
              async.complete();
              if (response.succeeded()) {
                context.assertTrue(SUCCESS_STATUS.equals(response.result().statusCode()));
              } else {
                context.fail(response.cause());
              }
            });
  }
}

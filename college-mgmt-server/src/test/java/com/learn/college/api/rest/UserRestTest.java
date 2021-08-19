package com.learn.college.api.rest;

import com.learn.college.api.auth.AuthUtils;
import com.learn.college.api.dao.UserDAO;
import com.learn.college.api.dao.UserRoleDAO;
import com.learn.college.api.mapper.UserDTOMapper;
import com.learn.college.api.verticles.UserVerticle;
import com.learn.college.common.config.*;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.util.DbUtils;
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

import static com.learn.college.common.config.TestConstants.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*", "javax.crypto.*"})
@PrepareForTest({AsyncResult.class, DbUtils.class})
public class UserRestTest {

  private static Vertx vertx;

  private static WebClient client;

  @Mock private DbUtils dbUtils;
  @Mock private AsyncResult<UserDTO> userDTOAsyncResult;
  @Mock private AsyncResult<Long> longAsyncResult;
  @Mock private AsyncResult<List<JsonObject>> dataAsyncResult;

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
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    setInternalState(UserDAO.class, PI_DATA_RESOURCE, dbUtils);
    setInternalState(UserRoleDAO.class, PI_DATA_RESOURCE, dbUtils);
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
   * Test Case to perform user sign up for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testSignUp_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(null);

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Long>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Long>>) invocationOnMock.getArgument(2))
                      .handle(longAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execInsert(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));
    PowerMockito.when(longAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(longAsyncResult.result()).thenReturn(TEST_ID);

    // client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_SIGN_UP)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .sendJsonObject(
            TestUtility.getTestAuthCredentials(true),
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
   * Test Case to perform user sign up for Email already exists failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testSignUp_EmailAlreadyExists_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(TestUtility.testUser());

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_SIGN_UP)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .sendJsonObject(
            TestUtility.getTestAuthCredentials(true),
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
   * Test Case to perform user sign up for failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testSignUp_FailedAddUser_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(null);

    PowerMockito.doAnswer(
            (Answer<AsyncResult<Long>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<Long>>) invocationOnMock.getArgument(2))
                      .handle(Future.failedFuture("Failed to insert user"));
                  return null;
                })
        .when(dbUtils)
        .execInsert(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_SIGN_UP)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .sendJsonObject(
            TestUtility.getTestAuthCredentials(true),
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
   * Test Case to perform user sign up for failure case with invalid request body
   *
   * @param context test context
   */
  @Test
  public void testSignUpWithInvalidParams_N(TestContext context) {
    final Async async = context.async();

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_SIGN_UP)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
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
   * Test Case to perform user sign in for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testSignIn_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(TestUtility.testUser());

    List<JsonObject> list = new ArrayList<>();
    list.add(new JsonObject().put(ROLE_ID, 1));

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<JsonObject>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<JsonObject>>>) invocationOnMock.getArgument(2))
                      .handle(dataAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execGet(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));
    PowerMockito.when(dataAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(dataAsyncResult.result()).thenReturn(list);

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_SIGN_IN)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .sendJsonObject(
            TestUtility.getTestAuthCredentials(true),
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
   * Test Case to perform user sign in with invalid password for failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testSignInWithInvalidPassword_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(TestUtility.testUser());

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_SIGN_IN)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .sendJsonObject(
            TestUtility.getTestAuthCredentials(false),
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
   * Test Case to perform user sign in for failure case with invalid request body
   *
   * @param context test context
   */
  @Test
  public void testSignInWithInvalidParams_N(TestContext context) {
    final Async async = context.async();

    // client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_SIGN_IN)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
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
   * Test Case to perform reset password for success case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testResetPassword_P(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(TestUtility.testUser());

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
        .postAbs(AUTH_BASE_URL + PATH_RESET_PASS)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            TestUtility.getTestResetPassCredentials(true),
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
   * Test Case to perform reset password with invalid email for failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testResetPasswordWithInvalidEmail_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(null);

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_RESET_PASS)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            TestUtility.getTestResetPassCredentials(true),
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
   * Test Case to perform reset password with invalid password for failure case
   *
   * @param context test context
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testResetPasswordWithInvalidPassword_N(TestContext context) {
    final Async async = context.async();

    PowerMockito.doAnswer(
            (Answer<AsyncResult<UserDTO>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<UserDTO>>) invocationOnMock.getArgument(3))
                      .handle(userDTOAsyncResult);
                  return null;
                })
        .when(dbUtils)
        .execRow(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    PowerMockito.when(userDTOAsyncResult.succeeded()).thenReturn(true);
    PowerMockito.when(userDTOAsyncResult.result()).thenReturn(TestUtility.testUser());

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_RESET_PASS)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .putHeader(AUTH_TOKEN, TEST_AUTH_TOKEN)
        .sendJsonObject(
            TestUtility.getTestResetPassCredentials(false),
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
   * Test Case to perform user sign in with invalid request body for failure case
   *
   * @param context test context
   */
  @Test
  public void testResetPasswordWithInvalidParams_N(TestContext context) {
    final Async async = context.async();

    client = WebClient.create(vertx);
    client
        .postAbs(AUTH_BASE_URL + PATH_RESET_PASS)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
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
}

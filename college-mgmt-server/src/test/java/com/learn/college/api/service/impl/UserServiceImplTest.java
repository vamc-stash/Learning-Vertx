package com.learn.college.api.service.impl;

import com.learn.college.api.auth.AuthService;
import com.learn.college.api.auth.impl.AuthServiceImpl;
import com.learn.college.api.dao.CourseDAO;
import com.learn.college.api.dao.UserDAO;
import com.learn.college.api.dao.UserRoleDAO;
import com.learn.college.common.config.TestUtility;
import com.learn.college.common.dto.UserDTO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.learn.college.common.config.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  UserDAO.class,
  UserRoleDAO.class,
  CourseDAO.class,
  AuthService.class,
  AuthServiceImpl.class
})
public class UserServiceImplTest {

  private static UserDAO userDAO;
  private static UserRoleDAO userRoleDAO;
  private static CourseDAO courseDAO;
  private static AuthServiceImpl authServiceImpl;
  private static AuthService authService;

  @BeforeClass
  public static void beforeClass() {
    // enable mocking for all static methods of class
    PowerMockito.mockStatic(UserDAO.class);
    userDAO = mock(UserDAO.class);
    when(UserDAO.getInstance()).thenReturn(userDAO);

    PowerMockito.mockStatic(UserRoleDAO.class);
    userRoleDAO = mock(UserRoleDAO.class);
    when(UserRoleDAO.getInstance()).thenReturn(userRoleDAO);

    PowerMockito.mockStatic(CourseDAO.class);
    courseDAO = mock(CourseDAO.class);
    when(CourseDAO.getInstance()).thenReturn(courseDAO);

    PowerMockito.mockStatic(AuthServiceImpl.class);
    PowerMockito.mockStatic(AuthService.class);
    authServiceImpl = mock(AuthServiceImpl.class);
    authService = mock(AuthService.class);
    when(AuthService.getInstance()).thenReturn(authServiceImpl);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindUserByEmail_P() {
    PowerMockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<UserDTO>> userHandler = invocationOnMock.getArgument(1);
              userHandler.handle(Future.succeededFuture(TestUtility.testUser()));
              return null;
            })
        .when(userDAO)
        .findUserByEmail(Mockito.anyString(), Mockito.any(Handler.class));

    Handler<AsyncResult<UserDTO>> handler =
        handle -> {
          UserDTO expectedResult = handle.result();
          assertEquals(expectedResult, TestUtility.testUser());
        };

    UserServiceImpl.getInstance().findUserByEmail(TEST_EMAIL, handler);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInsertUser_P() {
    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(TEST_EMAIL);
    userDTO.setPassword(TEST_PASS);

    when(authService.generatePasswordHash(Mockito.anyString())).thenReturn(TEST_PASS_HASH);

    PowerMockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<Long>> userHandler = invocationOnMock.getArgument(1);
              userHandler.handle(Future.succeededFuture(TEST_ID));
              return null;
            })
        .when(userDAO)
        .insertUser(Mockito.any(), Mockito.any(Handler.class));

    Handler<AsyncResult<Long>> handler =
        handle -> {
          Long expectedResult = handle.result();
          assertEquals(expectedResult, TEST_ID);
        };

    UserServiceImpl.getInstance().insertUser(userDTO, handler);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInsertUserRole_P() {
    PowerMockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<Long>> userHandler = invocationOnMock.getArgument(2);
              userHandler.handle(Future.succeededFuture(TEST_ID));
              return null;
            })
        .when(userDAO)
        .insertUserRole(Mockito.anyString(), Mockito.anyInt(), Mockito.any(Handler.class));

    Handler<AsyncResult<Long>> handler =
        handle -> {
          Long expectedResult = handle.result();
          assertEquals(expectedResult, TEST_ID);
        };

    UserServiceImpl.getInstance().insertUserRole(TEST_USER_ID, 1, handler);
  }
}

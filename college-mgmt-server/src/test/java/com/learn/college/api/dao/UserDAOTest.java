package com.learn.college.api.dao;

import com.learn.college.api.mapper.UserDTOMapper;
import com.learn.college.common.config.TestUtility;
import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.lib.UpdateQuery;
import com.learn.college.common.util.DbUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Tuple;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static com.learn.college.common.config.TestConstants.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*", "javax.crypto.*"})
@PrepareForTest({
  UserDAO.class,
  DbUtils.class,
  AsyncResult.class,
  UserDTOMapper.class,
  UpdateQuery.class
})
public class UserDAOTest {
  @Mock DbUtils primaryDataResource;
  @Mock UserDTOMapper userDTOMapper;

  @Mock Handler<AsyncResult<UserDTO>> asyncUserDTOHandler;
  @Mock Handler<AsyncResult<Long>> asyncLongHandler;
  @Mock AsyncResult<List<UserDTO>> asyncListUserDTOHandler;

  private static UserDTO userDTO;

  @BeforeClass
  public static void setUp() {
    userDTO = TestUtility.testUser();
  }

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    setInternalState(UserDAO.class, PI_DATA_RESOURCE, primaryDataResource);
    setInternalState(UserDAO.class, USER_DTO_MAPPER, userDTOMapper);
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

  @Test
  @SuppressWarnings("unchecked")
  public void testFindUserByEmail_P() {
    UserDAO.getInstance().findUserByEmail(TEST_EMAIL, asyncUserDTOHandler);

    String query = Whitebox.getInternalState(UserDAO.class, "USER_GET_BY_EMAIL");
    verify(primaryDataResource, times(1))
        .execRow(
            eq(query),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));
    verifyNoMoreInteractions(primaryDataResource);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInsertUser_P() {
    UserDAO.getInstance().insertUser(userDTO, asyncLongHandler);

    String query = Whitebox.getInternalState(UserDAO.class, "USER_INSERT");
    verify(primaryDataResource, times(1))
        .execInsert(eq(query), Mockito.any(Tuple.class), Mockito.any(Handler.class));
    verifyNoMoreInteractions(primaryDataResource);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInsertUserRole_P() {
    UserDAO.getInstance().insertUserRole(TEST_USER_ID, 1, asyncLongHandler);

    String query = Whitebox.getInternalState(UserDAO.class, "USER_ROLE_INSERT");
    verify(primaryDataResource, times(1))
        .execInsert(eq(query), Mockito.any(Tuple.class), Mockito.any(Handler.class));
    verifyNoMoreInteractions(primaryDataResource);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUpdateUserDetails_P() {
    PowerMockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<Void>> handler = invocationOnMock.getArgument(2);
              handler.handle(Future.succeededFuture());
              return null;
            })
        .when(primaryDataResource)
        .execUpdate(Mockito.anyString(), Mockito.any(Tuple.class), Mockito.any(Handler.class));

    Handler<AsyncResult<Void>> handler = handle -> assertTrue(handle.succeeded());
    UserDAO.getInstance().updateUserDetails(userDTO, handler);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUsersByRole_P() {
    List<UserDTO> list = new ArrayList<>();
    list.add(TestUtility.testUser());

    PowerMockito.doAnswer(
            (Answer<AsyncResult<List<UserDTO>>>)
                invocationOnMock -> {
                  ((Handler<AsyncResult<List<UserDTO>>>) invocationOnMock.getArgument(3))
                      .handle(asyncListUserDTOHandler);
                  return null;
                })
        .when(primaryDataResource)
        .execGet(
            Mockito.anyString(),
            Mockito.any(Tuple.class),
            Mockito.any(UserDTOMapper.class),
            Mockito.any(Handler.class));

    when(asyncListUserDTOHandler.succeeded()).thenReturn(true);
    when(asyncListUserDTOHandler.result()).thenReturn(list);

    UserDAO.getInstance().getUsersByRole(1);
  }
}

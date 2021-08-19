package com.learn.college.api.service.impl;

import com.learn.college.api.dao.CourseDAO;
import com.learn.college.common.config.TestUtility;
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

import java.util.ArrayList;

import static com.learn.college.common.config.TestConstants.TEST_USER_ID;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CourseDAO.class})
public class CourseServiceImplTest {

  private static CourseDAO courseDAO;

  @BeforeClass
  public static void beforeClass() {
    // enable mocking for all static methods of class
    PowerMockito.mockStatic(CourseDAO.class);
    courseDAO = mock(CourseDAO.class);
    when(CourseDAO.getInstance()).thenReturn(courseDAO);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testAddCourse_P() {

    PowerMockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<Void>> voidHandler = invocationOnMock.getArgument(1);
              voidHandler.handle(Future.succeededFuture());
              return null;
            })
        .when(courseDAO)
        .insertCourse(Mockito.any(), Mockito.any(Handler.class));

    Handler<AsyncResult<Void>> handler = handle -> assertTrue(handle.succeeded());

    new CourseServiceImpl().addCourse(TestUtility.testCourseDTO(), handler);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testRegisterCourses_P() {

    PowerMockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<Void>> voidHandler = invocationOnMock.getArgument(2);
              voidHandler.handle(Future.succeededFuture());
              return null;
            })
        .when(courseDAO)
        .addStudentCourseMapping(Mockito.any(), Mockito.anyString(), Mockito.any(Handler.class));

    Handler<AsyncResult<Void>> handler = handle -> assertTrue(handle.succeeded());

    new CourseServiceImpl().registerCourses(new ArrayList<>(), TEST_USER_ID, handler);
  }
}

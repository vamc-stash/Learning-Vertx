package com.learn.college.api.mapper;

import com.learn.college.common.dto.CourseDTO;
import io.vertx.sqlclient.Row;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.learn.college.common.config.Constants.*;
import static com.learn.college.common.config.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class CourseDTOMapperTest {

  @Mock private Row rs;

  @Before
  public void before() {
    when(rs.getString(DB_COURSE_ID)).thenReturn(TEST_COURSE_ID);
    when(rs.getString(DB_COURSE_TITLE)).thenReturn(TEST_COURSE_TITLE);
    when(rs.getString(DB_COURSE_TEACHER)).thenReturn(TEST_TEACHER_ID);
    when(rs.getInteger(DB_COURSE_CREDITS)).thenReturn(TEST_COURSE_CREDITS);
  }

  @Test
  public void testCourseMapper_P() {
    CourseDTO courseDTO = CourseDTOMapper.getInstance().mapRow(rs);

    assertEquals(TEST_COURSE_ID, courseDTO.getCourseId());
    assertEquals(TEST_COURSE_TITLE, courseDTO.getTitle());
    assertEquals(TEST_COURSE_CREDITS, courseDTO.getCredits());
    assertEquals(TEST_TEACHER_ID, courseDTO.getTeacherId());
  }
}

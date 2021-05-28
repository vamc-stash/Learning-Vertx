package com.learn.college.api.mapper;

import com.learn.college.common.dto.CourseDTO;
import com.learn.college.common.interfaces.BaseRowMapper;
import io.vertx.sqlclient.Row;

import static com.learn.college.common.config.Constants.*;

/** Class is responsible for converting database Row to Course POJO object */
public class CourseDTOMapper implements BaseRowMapper<CourseDTO> {

  // Singleton instance
  private static final CourseDTOMapper courseDTOMapper = new CourseDTOMapper();

  /**
   * Gets CourseDTOMapper instance
   *
   * @return CourseDTOMapper instance
   */
  public static CourseDTOMapper getInstance() {
    return courseDTOMapper;
  }

  @Override
  public CourseDTO mapRow(Row rs) {
    CourseDTO courseDTO = new CourseDTO();
    courseDTO.setCourseId(rs.getString(DB_COURSE_ID));
    courseDTO.setTitle(rs.getString(DB_COURSE_TITLE));
    courseDTO.setCredits(rs.getInteger(DB_COURSE_CREDITS));
    courseDTO.setTeacherId(rs.getString(DB_COURSE_TEACHER));
    return courseDTO;
  }
}

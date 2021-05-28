package com.learn.college.api.dao;

import com.learn.college.api.mapper.CourseDTOMapper;
import com.learn.college.common.dto.CourseDTO;
import com.learn.college.common.lib.DatabaseResource;
import com.learn.college.common.util.DbUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** The type CourseDAO */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CourseDAO {

  // Instance for fetching data from DB
  private static final DbUtils primaryDataResource = DatabaseResource.getPiDataResource();

  // Singleton instance of UserDTOMapper
  private static final CourseDTOMapper courseDTOMapper = CourseDTOMapper.getInstance();

  // Singleton instance
  private static final CourseDAO courseDAO = new CourseDAO();

  /**
   * Gets the Singleton instance of CourseDAO
   *
   * @return CourseDAO instance
   */
  public static CourseDAO getInstance() {
    return courseDAO;
  }

  /* Query to insert course */
  private static final String INSERT_COURSE =
      "INSERT INTO courses (course_id, title, credits, teacher_id) VALUES (?, ?, ?, ?)";

  /* Query to select courses */
  private static final String SELECT_ALL_COURSES = "SELECT * FROM courses";

  /* Query to insert student courses */
  private static final String INSERT_STUDENT_COURSES =
      "INSERT INTO student_courses (course_id, student_id) VALUES (?, ?)";

  /* Query to select courses by student id */
  private static final String SELECT_COURSES_BY_STUDENT_ID =
      "SELECT * FROM courses WHERE course_id IN (SELECT course_id FROM student_courses WHERE student_id = ?)";

  /* Query to select courses by teacher id */
  private static final String SELECT_COURSES_BY_TEACHER_ID =
      "SELECT * FROM courses WHERE teacher_id = ?";

  /**
   * Inserts course
   *
   * @param courseDTO CourseDTO
   * @param handler async handler
   */
  public void insertCourse(CourseDTO courseDTO, Handler<AsyncResult<Long>> handler) {
    Tuple tuple = Tuple.tuple();
    tuple.addString(courseDTO.getCourseId());
    tuple.addString(courseDTO.getTitle());
    tuple.addInteger(courseDTO.getCredits());
    tuple.addString(courseDTO.getTeacherId());
    primaryDataResource.execInsert(INSERT_COURSE, tuple, handler);
  }

  /**
   * Gets All Courses
   *
   * @return future
   */
  public Future<List<CourseDTO>> getAllCourses() {
    Promise<List<CourseDTO>> promise = Promise.promise();
    primaryDataResource.execGet(
        SELECT_ALL_COURSES,
        Tuple.tuple(),
        courseDTOMapper,
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
   * Adds StudentCourses Mapping
   *
   * @param courseIdList List of course Ids
   * @param studentId student id
   * @param handler async handler
   */
  public void addStudentCourseMapping(
      List<String> courseIdList, String studentId, Handler<AsyncResult<Void>> handler) {
    List<Tuple> tuples = new ArrayList<>();

    courseIdList.forEach(
        courseId -> {
          Tuple tuple = Tuple.tuple();
          tuple.addString(courseId);
          tuple.addString(studentId);
          tuples.add(tuple);
        });

    primaryDataResource.execBatch(INSERT_STUDENT_COURSES, tuples, handler);
  }

  /**
   * Gets All Courses registered by Student
   *
   * @param studentId student id
   * @return future
   */
  public Future<List<CourseDTO>> getStudentCourses(String studentId) {
    Promise<List<CourseDTO>> promise = Promise.promise();
    Tuple tuple = Tuple.tuple();
    tuple.addString(studentId);
    primaryDataResource.execGet(
        SELECT_COURSES_BY_STUDENT_ID,
        tuple,
        courseDTOMapper,
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
   * Gets All Courses under a teacher
   *
   * @param teacherId teacher id
   * @return future
   */
  public Future<List<CourseDTO>> getTeacherCourses(String teacherId) {
    Promise<List<CourseDTO>> promise = Promise.promise();
    Tuple tuple = Tuple.tuple();
    tuple.addString(teacherId);
    primaryDataResource.execGet(
        SELECT_COURSES_BY_TEACHER_ID,
        tuple,
        courseDTOMapper,
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

package com.learn.college.api.service;

import com.learn.college.api.service.impl.CourseServiceImpl;
import com.learn.college.common.dto.CourseDTO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface CourseService {

  /**
   * Method gets the instance of CourseServiceImpl
   *
   * @return {@link CourseServiceImpl}
   */
  static CourseServiceImpl getInstance() {
    return new CourseServiceImpl();
  }

  /**
   * Method adds Course
   *
   * @param courseDTO {@link CourseDTO}
   * @param handler the handler
   */
  void addCourse(CourseDTO courseDTO, Handler<AsyncResult<Void>> handler);

  /**
   * Method allows to register for courses
   *
   * @param courseIdList List of courses
   * @param userId user id
   * @param handler async handler
   */
  void registerCourses(
      List<String> courseIdList, String userId, Handler<AsyncResult<Void>> handler);
}

package com.learn.college.api.service.impl;

import com.learn.college.api.dao.CourseDAO;
import com.learn.college.api.service.CourseService;
import com.learn.college.common.dto.CourseDTO;
import com.learn.college.common.util.CommonUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;

public class CourseServiceImpl implements CourseService {

  private static final CourseDAO courseDao = CourseDAO.getInstance();

  /**
   * {@inheritDoc}
   *
   * @param courseDTO {@link CourseDTO}
   * @param handler async handler
   */
  @Override
  public void addCourse(CourseDTO courseDTO, Handler<AsyncResult<Void>> handler) {
    courseDTO.setCourseId(CommonUtils.generateUUID().toString());
    courseDao.insertCourse(
        courseDTO,
        ar -> {
          if (ar.succeeded()) {
            handler.handle(Future.succeededFuture());
          } else {
            handler.handle(Future.failedFuture(ar.cause()));
          }
        });
  }

  /**
   * {@inheritDoc}
   *
   * @param courseIdList List of courses
   * @param userId user id
   * @param handler async handler
   */
  @Override
  public void registerCourses(
      List<String> courseIdList, String userId, Handler<AsyncResult<Void>> handler) {
    courseDao.addStudentCourseMapping(courseIdList, userId, handler);
  }
}

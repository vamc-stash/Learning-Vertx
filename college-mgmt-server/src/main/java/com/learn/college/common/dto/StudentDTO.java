package com.learn.college.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentDTO extends UserDTO {

  List<CourseDTO> availableCourses;

  List<CourseDTO> registeredCourses;

  public void addCourses(List<CourseDTO> availableCourses) {
    this.availableCourses = new ArrayList<>(availableCourses);
  }

  public void setRegisteredCourses(List<CourseDTO> registeredCourses) {
    this.registeredCourses = new ArrayList<>(registeredCourses);
  }
}

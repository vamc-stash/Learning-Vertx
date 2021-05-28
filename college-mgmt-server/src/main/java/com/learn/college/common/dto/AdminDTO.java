package com.learn.college.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminDTO extends UserDTO {

  List<CourseDTO> courses;

  List<UserDTO> teachers;

  List<UserDTO> students;

  public void addCourses(List<CourseDTO> courses) {
    this.courses = new ArrayList<>(courses);
  }

  public void addTeachers(List<UserDTO> teachers) {
    this.teachers = new ArrayList<>(teachers);
  }

  public void addStudents(List<UserDTO> students) {
    this.students = new ArrayList<>(students);
  }
}

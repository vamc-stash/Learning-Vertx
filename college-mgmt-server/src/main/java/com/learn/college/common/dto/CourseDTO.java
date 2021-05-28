package com.learn.college.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.learn.college.common.config.Constants.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDTO {

  @JsonProperty(DB_COURSE_ID)
  String courseId;

  @JsonProperty(DB_COURSE_TITLE)
  String title;

  @JsonProperty(DB_COURSE_CREDITS)
  Integer credits;

  @JsonProperty(DB_COURSE_TEACHER)
  String teacherId;
}

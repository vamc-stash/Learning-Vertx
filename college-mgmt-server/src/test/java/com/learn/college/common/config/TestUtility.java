package com.learn.college.common.config;

import com.learn.college.common.dto.CourseDTO;
import com.learn.college.common.dto.UserDTO;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

import static com.learn.college.common.config.TestConstants.*;

public class TestUtility {

  public static JsonObject getTestAuthCredentials(Boolean isValid) {
    JsonObject authObject = new JsonObject();
    if (isValid) {
      authObject.put(EMAIL, TEST_EMAIL).put(PASSWORD, TEST_PASS);
    } else {
      authObject.put(EMAIL, TEST_EMAIL).put(PASSWORD, TEST_INVALID_PASS);
    }

    return authObject;
  }

  public static UserDTO testUser() {
    UserDTO user = new UserDTO();
    user.setUserId(TEST_USER_ID);
    user.setEmail(TEST_EMAIL);
    user.setFirstName(null);
    user.setLastName(null);
    user.setPassword(TEST_PASS_HASH);

    return user;
  }

  public static JsonObject getTestResetPassCredentials(Boolean isValid) {
    return getTestAuthCredentials(isValid).put(RESET_PASSWORD, TEST_PASS);
  }

  public static CourseDTO testCourseDTO() {
    CourseDTO courseDTO = new CourseDTO();
    courseDTO.setCourseId(TEST_COURSE_ID);
    courseDTO.setTeacherId(TEST_TEACHER_ID);
    courseDTO.setCredits(TEST_COURSE_CREDITS);
    courseDTO.setTitle(TEST_COURSE_TITLE);

    return courseDTO;
  }

  public static JsonObject testCourseRequest() {
    JsonObject course = new JsonObject();
    course
        .put(COURSE_TITLE, TEST_COURSE_TITLE)
        .put(COURSE_CREDITS, TEST_COURSE_CREDITS)
        .put(TEACHER_ID, TEST_TEACHER_ID);
    return course;
  }

  public static JsonObject testCourseList() {
    JsonObject courseList = new JsonObject();

    courseList.put(
        "courseIdList",
        new JsonArray()
            .add("edeac470-1571-4468-a5ab-9361d1804ecc")
            .add("1d99434a-f822-4dc3-a3dd-680aaac46681"));
    return courseList;
  }

  public static Map<String, String> getResourceMap() {
    Map<String, String> resourceMap = new HashMap<>();
    resourceMap.put("/api/v1/college/addUser@POST", "efce42a8-bf19-11eb-8a3d-c85b7653eeb9");
    resourceMap.put("/api/v1/auth/signin@POST", "a97150ac-bb0c-11eb-8008-c85b7653eeb9");
    return resourceMap;
  }

  public static Map<Integer, JsonArray> getPermissionMap() {
    Map<Integer, JsonArray> permissionMap = new HashMap<>();
    JsonArray jsonArray = new JsonArray();
    jsonArray
        .add("2e770778-ac15-11eb-8c16-c85b7653eeb9")
        .add("a97150ac-bb0c-11eb-8008-c85b7653eeb9");
    permissionMap.put(1, jsonArray);
    permissionMap.put(2, jsonArray);
    permissionMap.put(3, jsonArray);
    return permissionMap;
  }

  public static Map<String, Integer> getRolesMap() {
    Map<String, Integer> rolesMap = new HashMap<>();
    rolesMap.put("ADMIN", 1);
    rolesMap.put("STUDENT", 2);
    rolesMap.put("TEACHER", 3);
    return rolesMap;
  }
}

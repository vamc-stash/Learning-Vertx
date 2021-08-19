package com.learn.college.common.config;

public class TestConstants {

  public static final String AUTH_BASE_URL = "http://localhost:8888/api/v1/auth";
  public static final String COLLEGE_BASE_URL = "http://localhost:8888/api/v1/college";

  public static final Integer SUCCESS_STATUS = 200;
  public static final Integer REDIRECT_STATUS = 302;
  public static final Integer BAD_REQUEST_STATUS = 400;
  public static final Integer INTERNAL_SERVER_ERROR_STATUS = 500;

  public static final String CONTENT_TYPE = "content-type";
  public static final String APPLICATION_JSON ="application/json";
  public static final String AUTH_TOKEN = "AUTH_TOKEN";

  public static final String PATH_SIGN_UP = "/signup";
  public static final String PATH_SIGN_IN = "/signin";
  public static final String PATH_RESET_PASS = "/resetPassword";
  public static final String PATH_ADD_USER = "/addUser";
  public static final String PATH_ADD_COURSE = "/addCourse";
  public static final String PATH_UPDATE_USER = "/updateUser";
  public static final String PATH_REGISTER_COURSES = "/registerCourses";
  public static final String PATH_GET = "/get";

  public static final String EMAIL = "email";
  public static final String PASSWORD = "password";
  public static final String PI_DATA_RESOURCE = "primaryDataResource";
  public static final String ROLE_ID = "role_id";
  public static final String RESET_PASSWORD = "resetPassword";
  public static final String USER_DTO_MAPPER = "userDTOMapper";
  public static final String COURSE_TITLE = "title";
  public static final String COURSE_CREDITS = "credits";
  public static final String TEACHER_ID = "teacherId";

  public static final String TEST_EMAIL = "test@mail.com";
  public static final String TEST_PASS = "password";
  public static final String TEST_INVALID_PASS = "passwrod";
  public static final Long TEST_ID = Long.getLong("1");
  public static final String TEST_USER_ID = "b18effdb-c37f-4f79-919e-1d25fa0f989a";
  public static final String TEST_PASS_HASH = "cGFzc3dvcmRlQnFDVXROcU91N2hUZFlveU1WaWRJM2I=";
  public static final String TEST_AUTH_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNjYwMTA3MWYtZGYwZi00NzdkLWEwNzMtMzQ2NzE2NjJiOWJmIiwicm9sZV9pZCI6MiwiaWF0IjoxNjIyMTc0NDA2LCJleHAiOjE2MjIxOTI0MDYsImlzcyI6IkNvbGxlZ2UtU2VydmVyLVByb3ZpZGVyIiwic3ViIjoiYXV0aCJ9.pDl50ii4ZCibFsw0z-UfRtQkI1HtgM1-R7wfK0J7ayc";
  public static final String TEST_COURSE_ID = "b18effdb-c37f-4f79-919e-1d25fa0f989b";
  public static final String TEST_COURSE_TITLE = "title";
  public static final Integer TEST_COURSE_CREDITS = 3;
  public static final String TEST_TEACHER_ID = "b18effdb-c37f-4f79-919e-1d25fa0f989c";
}

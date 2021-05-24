package com.learn.college.common.config;

/** Class contains constants used across the application */
public class Constants {

  /** To prevent object creation of this class */
  private Constants() {}

  /** Swagger File Location */
  public static final String SWAGGER =
      System.getProperty("swagger.location").replace("\\", "/"); // for windows OS

  /** Config Constants */
  public static final String CONFIG_STORE_OPTIONS_DIR_PATH = System.getProperty("config.path");

  public static final String CONFIG_STORE_OPTIONS_FILE = "file";
  public static final String CONFIG_STORE_OPTIONS_PATH = "path";
  public static final String CONFIG_STORE_OPTIONS_FORMAT_JSON = "json";
  public static final String CONFIG_FILE_PATH = "application-config.json";

  /** Common Terms */
  public static final String ACCEPT = "accept";

  public static final String ORIGIN = "origin";
  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String X_REQUESTED_WITH = "x-requested-with";
  public static final String CONTENT_TYPE_CORS_HEADER = "Content-Type";
  public static final String CONTENT_TYPE = "content-type";
  public static final String APPLICATION_JSON = "application/json";
  public static final String BODY = "body";
  public static final String PARAMS = "params";
  public static final String AUTH_TOKEN = "auth-token";
  public static final String AUTH_TOKEN_CAPS = "AUTH_TOKEN";
  public static final String JWT = "jwt";

  public static final Integer SUCCESS_STATUS = 200;
  public static final Integer REDIRECT_STATUS = 302;
  public static final Integer BAD_REQUEST_STATUS = 400;
  public static final Integer INTERNAL_SERVER_ERROR_STATUS = 500;

  public static final String ERROR_INVALID_AUTH_PARAMS = "Invalid Auth params";
  public static final String ERROR_INVALID_PASSWORD = "Invalid password";
  public static final String VALIDATION_SUCCESS = "Validation is success";

  public static final String ADMIN_USER_SIGN_UP_SUCCESS = "Admin User SignUp Success";
  public static final String ADMIN_USER_SIGN_UP_FAILURE = "Admin User SignUp Failure";

  /** API paths */
  public static final String PATH_COLLEGE_AUTH_BASE = "/api/v1/auth/";

  public static final String PATH_COLLEGE_BASE = "/api/v1/college/*";
  public static final String PATH_COLLEGE_MOUNT_POINT = "/api/v1/college/";
  public static final String PATH_PING = "/ping";
  public static final String PATH_SIGN_UP = "/signup";
  public static final String PATH_SIGN_IN = "/signin";

  /** Swagger Operation Ids */
  public static final String USER_SIGN_UP = "sign-up";

  public static final String USER_SIGN_IN = "sign-in";
  public static final String USER_SIGN_IN_PING = "sign-in-ping";

  /** Constants related to dbConfig */
  public static final String RESOURCES = "resources";

  public static final String PI_DATA = "pi_data";

  /** Constants related to Resource Table */
  public static final String RESOURCE_TABLE = "resource";

  public static final String RESOURCE_KEY = "resource_key";
  public static final String RESOURCE_DESCRIPTION = "description";
  public static final String RESOURCE_TYPE = "type";
  public static final String RESOURCE_METHOD = "method";
  public static final String RESOURCE_STATUS = "status";

  /** Constants related to RolePermission Table */
  public static final String PERMISSION_TABLE = "role_permissions";

  public static final String PERMISSION_ROLE_ID = "role_id";
  public static final String PERMISSION_RESOURCES = "resources";

  /** Constants related to Roles Table */
  public static final String ROLES_TABLE = "roles";

  public static final String DB_ROLE_ID = "id";
  public static final String DB_ROLE = "role";
  public static final String DB_ROLE_DESCRIPTION = "description";

  /** Constants related to Users Table */
  public static final String USERS_TABLE = "users";

  public static final String DB_USER_ID = "user_id";
  public static final String DB_FIRST_NAME = "first_name";
  public static final String DB_LAST_NAME = "last_name";
  public static final String DB_EMAIL = "email";
  public static final String DB_PASSWORD = "password";

  /** Delimiter used to prepare combination of URL and method */
  public static final String DELIMITER = "@";
}

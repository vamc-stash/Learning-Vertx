package com.learn.college.common.error;

/** This class enumerates all the error codes that may occur in server */
public enum ErrorCode {
  SUCCESS(0, "Success"),

  BAD_REQUEST_ERROR(400000, "Bad Request"),
  BAD_REQUEST_VALIDATION_ERROR(400001, "Bad Request, Validation Error"),
  INCORRECT_JSON_RECEIVED(400002, "Incorrect JSON received"),
  INVALID_QUERY_PARAM(400003, "Invalid query param"),
  MISSING_QUERY_PARAM(400004, "Missing query param"),
  NO_QUERY_PARAMS(400005, "Query params are not provided"),
  INVALID_RESOURCE_ID(400006, "Resource Id is null or empty"),

  INVALID_AUTH_TOKEN(401001, "Authentication failed, retry login"),
  AUTHENTICATION_VERIFICATION_ERROR(401002, "Authentication failed, retry login"),
  AUTHORISATION_VERIFICATION_ERROR(401003, "Unauthorised"),

  CODE_EXPIRED_ERROR(414000, "Code is expired"),

  INTERNAL_PROCESSING_ERROR(500000, "Internal Processing Error"),
  INTERNAL_SERVER_ERROR(500001, "Failed to start server"),
  INTERNAL_SWAGGER_ERROR(500002, "Failed to load swagger"),
  INTERNAL_CONFIGURATION_ERROR(500003, "Failed to load configuration");

  /** This represents the Error Code */
  private final Integer value;

  /** This represents the Error Message associated with code */
  private final String message;

  private static final Integer OFFSET = 1000;

  /**
   * Constructor to set values for error code and message
   *
   * @param value Integer value of the error code
   * @param message Error message
   */
  ErrorCode(final int value, final String message) {
    this.value = value;
    this.message = message;
  }

  /**
   * This method returns the Integer value of the error code
   *
   * @return value Error Code
   */
  public Integer getValue() {
    return value;
  }

  /**
   * This method returns the error message associated with code.
   *
   * @return message Error Message
   */
  public String getMessage() {
    return message;
  }

  public ErrorResponse getErrorResponse() {
    return new ErrorResponse(value, message);
  }

  public Integer getHttpErrorCode() {
    return value / OFFSET;
  }

  public Integer getHttpErrorCode(Integer val) {
    return val / OFFSET;
  }
}

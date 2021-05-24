package com.learn.college.common.error;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLException;

import javax.xml.bind.ValidationException;

/**
 * This custom unchecked exception class represents the error response sent to the client on failure
 */
public class ErrorResponse extends RuntimeException {

  /** UID for Serialization and Deserialization */
  private static final Long serialVersionUID = 4L;

  private Integer errorCode;

  private String errorMessage;

  private static final Integer OFFSET = 1000;

  private static final String ERROR_CODE = "code";

  private static final String ERROR_MESSAGE = "message";

  public ErrorResponse() {
    this.errorCode = 10000;
    this.errorMessage = "Unknown Error";
  }

  /**
   * Initiates a new ErrorResponse
   *
   * @param errorCode Error Code
   * @param errorMessage Error Message
   */
  public ErrorResponse(Integer errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public final Integer getErrorCode() {
    return errorCode;
  }

  @Override
  public final String getMessage() {
    return errorMessage;
  }

  public final ErrorResponse setErrorCode(final Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  public final ErrorResponse setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public final Integer getHttpErrorCode() {
    return errorCode / OFFSET;
  }

  @Override
  public final String toString() {
    if (getHttpErrorCode() == HttpResponseStatus.NO_CONTENT.code()) {
      return "";
    }
    return new JsonObject()
        .put(ERROR_CODE, errorCode)
        .put(ERROR_MESSAGE, errorMessage)
        .encodePrettily();
  }

  public static ErrorResponse getError(Throwable failure) {
    if (failure instanceof ErrorResponse) {
      return (ErrorResponse) failure;
    }
    if (failure instanceof MySQLException || failure instanceof ConnectTimeoutException) {
      return ErrorCode.INTERNAL_PROCESSING_ERROR.getErrorResponse();
    }

    /* There is an issue with vertx open api3 validation library for the validation exception,
     * so handle it separately.
     * */
    if (failure instanceof ValidationException) {
      String errorMessage = failure.getMessage();
      if (errorMessage.contains("ValidationException")) {
        errorMessage = "Error during validation of the request.";
      }
      return new ErrorResponse(ErrorCode.BAD_REQUEST_VALIDATION_ERROR.getValue(), errorMessage);
    }
    return new ErrorResponse(ErrorCode.BAD_REQUEST_ERROR.getValue(), failure.getMessage());
  }
}

package com.learn.college.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learn.college.common.config.EnumConstants;
import com.learn.college.common.error.ErrorResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.learn.college.common.config.Constants.*;
import static com.learn.college.common.error.ErrorCode.*;

/** Utility Class for rest api operations */
@UtilityClass
@Slf4j
public class RestUtils {

  private static final String ERROR_MSG_BODY_PARAM = "Error while parsing body param: {}";
  private static final String BODY_PARAMS = "Body Params: {} \n {}";

  /**
   * Enable CORS support
   *
   * @param router Router
   */
  public static void enableCorsSupport(Router router) {
    Set<String> allowedHeaders = new HashSet<>();
    Set<HttpMethod> allowedMethods = new HashSet<>();

    allowedHeaders.add(X_REQUESTED_WITH);
    allowedHeaders.add(ACCESS_CONTROL_ALLOW_ORIGIN);
    allowedHeaders.add(ORIGIN);
    allowedHeaders.add(CONTENT_TYPE_CORS_HEADER);
    allowedHeaders.add(ACCEPT);

    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.PUT);
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.OPTIONS);

    router
        .route()
        .handler(
            CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));
  }

  /**
   * Bad Request
   *
   * @param context RoutingContext
   * @param exception Throwable exception
   */
  public static void handleBadRequest(RoutingContext context, Throwable exception) {
    context.fail(BAD_REQUEST_ERROR.getHttpErrorCode(), ErrorResponse.getError(exception));
  }

  /**
   * Internal Server Error
   *
   * @param context RoutingContext
   * @param exception Throwable exception
   */
  public static void handleInternalError(RoutingContext context, Throwable exception) {
    context.fail(INTERNAL_PROCESSING_ERROR.getHttpErrorCode(), ErrorResponse.getError(exception));
  }

  /**
   * Handle error
   *
   * @param failureContext the failure context
   */
  public static void handleError(RoutingContext failureContext) {
    log.info(
        "Fail: {} {} {}",
        failureContext.request().path(),
        failureContext.statusCode(),
        failureContext.failure().getMessage());
    final ErrorResponse error = ErrorResponse.getError(failureContext.failure());
    failureContext
        .response()
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .setStatusCode(error.getHttpErrorCode())
        .end(error.toString());
  }

  /**
   * Success
   *
   * @param <T> the type parameter
   * @param context RoutingContext
   * @param result the result
   */
  public static <T> void handleSuccess(RoutingContext context, T result) {
    String path = context.request().path();
    log.info("Success {} {}", path, SUCCESS_STATUS);

    context
        .response()
        .setStatusCode(SUCCESS_STATUS)
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .end(Json.encodePrettily(result));
  }

  /**
   * Handle Redirection
   *
   * @param context Routing Context
   * @param path url path
   */
  public static void handleRedirection(RoutingContext context, String path) {
    context.response().setStatusCode(REDIRECT_STATUS).putHeader(HttpHeaders.LOCATION, path).end();
  }

  /**
   * DataHandler
   *
   * @param <T> the type parameter
   * @param context RoutingContex
   * @return handler
   */
  public static <T> Handler<AsyncResult<T>> dataHandler(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        handleSuccess(context, ar.result());
      } else {
        handleBadRequest(context, ar.cause());
      }
    };
  }

  /**
   * Void Handler
   *
   * @param context RoutingContext
   * @param message message
   * @return handler
   */
  public static Handler<AsyncResult<Void>> voidHandler(RoutingContext context, String message) {
    return ar -> {
      if (ar.succeeded()) {
        handleSuccess(context, message);
      } else {
        handleBadRequest(context, ar.cause());
      }
    };
  }

  /**
   * Void Handler
   *
   * @param context RoutingContext
   * @return handler
   */
  public static Handler<AsyncResult<Void>> voidHandler(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        handleSuccess(context, ar.result());
      } else {
        handleBadRequest(context, ar.cause());
      }
    };
  }

  /**
   * Gets the path from request
   *
   * @param context RoutingContext
   * @return api path
   */
  public static String getPath(RoutingContext context) {
    return context.request().path();
  }

  /**
   * Params body params into DTO
   *
   * @param <T> generic type
   * @param type DTO Class
   * @return Handler
   */
  public static <T> Handler<RoutingContext> parseBody(Class<T> type) {
    return context -> {
      log.debug(BODY_PARAMS, getPath(context), context.getBodyAsString());

      try {
        T value = DatabindCodec.mapper().readValue(context.getBodyAsString(), type);
        context.put(BODY, value);
        context.next();
      } catch (JsonProcessingException e) {
        log.error(ERROR_MSG_BODY_PARAM, e.getMessage(), e);
        handleBadRequest(context, INCORRECT_JSON_RECEIVED.getErrorResponse());
      }
    };
  }

  /**
   * Parse query params
   *
   * @param params query params
   * @return Handler
   */
  public static Handler<RoutingContext> parseParams(List<String> params) {
    return context -> {
      log.debug("Params: {} \n {}", getPath(context), context.queryParams());
      JsonObject queryParams = new JsonObject();
      MultiMap multiMap = context.queryParams();
      params.forEach(param -> queryParams.put(param, multiMap.get(param)));
      if (queryParams.isEmpty()) {
        handleBadRequest(context, MISSING_QUERY_PARAM.getErrorResponse());
      } else {
        context.put(PARAMS, queryParams);
        context.next();
      }
    };
  }

  /**
   * Gets body
   *
   * @param <T> the type parameter
   * @param context RoutingContext
   * @param type the type
   * @return the body
   */
  public static <T> T getBody(RoutingContext context, Class<T> type) {
    log.debug(BODY_PARAMS, context.request().path(), context.getBodyAsString());
    T value = null;
    try {
      value = DatabindCodec.mapper().readValue(context.getBodyAsString(), type);
      log.info("Parsed body: {}", Json.encode(value));
    } catch (Exception e) {
      log.error(ERROR_MSG_BODY_PARAM, e.getMessage(), e);
      handleBadRequest(context, INCORRECT_JSON_RECEIVED.getErrorResponse());
    }
    return value;
  }

  private static void prepareBadRequest(boolean anyOf, RoutingContext context, String param) {
    if (anyOf) {
      log.error("Empty String Param: {}", param);

      handleBadRequest(
          context,
          new ErrorResponse(
              INVALID_QUERY_PARAM.getValue(), INVALID_QUERY_PARAM.getMessage() + param));
    }
  }

  public static String getUserIdFromContext(RoutingContext context, String env) {
    if(!EnumConstants.Environment.TEST.toString().equals(env)) {
      if (context.user() == null
              || context.user().principal() == null
              || context.user().principal().getString(DB_USER_ID) == null) {
        return null;
      }
      return context.user().principal().getString(DB_USER_ID);
    }
    return null;
  }

  public static Integer getPermissionIdFromContext(RoutingContext context, String env) {
    if(!EnumConstants.Environment.TEST.toString().equals(env)) {
      if (context.user() == null
              || context.user().principal() == null
              || context.user().principal().getInteger(PERMISSION_ROLE_ID) == null) {
        return null;
      }
      return context.user().principal().getInteger(PERMISSION_ROLE_ID);
    }
    return null;
  }
}

package com.learn.college.api.auth.impl;

import com.learn.college.api.auth.AuthenticateHandler;
import com.learn.college.common.error.ErrorCode;
import io.vertx.core.Vertx;
import io.vertx.core.http.Cookie;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.RoutingContext;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import static com.learn.college.common.config.Constants.*;

public class AuthenticateHandlerImpl implements AuthenticateHandler {
  private JWTAuth authProvider;

  @Setter private static boolean isValidationEnabled = Boolean.TRUE;

  /**
   * Instantiates a new AuthenticateHandlerImpl
   *
   * @param vertx Vertx instance
   * @param jwtAuthOptions Auth Options
   */
  public AuthenticateHandlerImpl(Vertx vertx, JWTAuthOptions jwtAuthOptions) {
    if (isValidationEnabled) {
      this.authProvider = JWTAuth.create(vertx, jwtAuthOptions);
    }
  }

  @Override
  public void handle(RoutingContext context) {
    if (!isValidationEnabled) {
      context.next();
      return;
    }
    String authToken = getAuthToken(context);
    if (StringUtils.isBlank(authToken)) {
      context.fail(ErrorCode.INVALID_AUTH_TOKEN.getErrorResponse());
    } else {
      JsonObject payload = new JsonObject().put(JWT, authToken);
      authProvider.authenticate(
          payload,
          ar -> {
            if (ar.succeeded()) {
              context.setUser(ar.result());
              context.next();
            } else {
              context.fail(ErrorCode.AUTHENTICATION_VERIFICATION_ERROR.getErrorResponse());
            }
          });
    }
  }

  /**
   * Method fetches AuthToken from Cookie or Header
   *
   * @param context {@link RoutingContext}
   * @return auth token
   */
  private String getAuthToken(RoutingContext context) {
    if (context.cookieMap() != null) {
      Cookie authTokenCookie = context.cookieMap().get(AUTH_TOKEN_CAPS);
      if (authTokenCookie != null && !StringUtils.isNotBlank(authTokenCookie.getValue())) {
        return authTokenCookie.getValue();
      }
    }
    return context.request().getHeader(AUTH_TOKEN_CAPS);
  }
}

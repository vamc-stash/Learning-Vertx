package com.learn.college.api.auth;

import com.learn.college.api.auth.impl.AuthenticateHandlerImpl;
import com.learn.college.common.config.ApplicationConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public interface AuthenticateHandler extends Handler<RoutingContext> {

  /**
   * Create an AuthenticateHandler
   *
   * @return the authenticate handler
   */
  static AuthenticateHandler create(Vertx vertx) {
    return new AuthenticateHandlerImpl(vertx, ApplicationConfiguration.getJwtAuthOptions());
  }

  /**
   * Method sets Authentication validation flag
   *
   * @param isEnabled true if validation is enabled
   */
  static void setAuthValidationEnabled(boolean isEnabled) {
    AuthenticateHandlerImpl.setValidationEnabled(isEnabled);
  }
}

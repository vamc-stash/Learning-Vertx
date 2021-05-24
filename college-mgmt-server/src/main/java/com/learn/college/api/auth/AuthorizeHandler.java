package com.learn.college.api.auth;

import com.learn.college.api.auth.impl.AuthorizeHandlerImpl;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/** The interface for AuthorizationHandler */
public interface AuthorizeHandler extends Handler<RoutingContext> {

  /**
   * Create an AuthorizeHandler
   *
   * @return the authorize handler
   */
  static AuthorizeHandler create() {
    return new AuthorizeHandlerImpl();
  }

  /**
   * Method sets Authorization validation flag
   *
   * @param isEnabled true if validation is enabled
   */
  static void setAuthValidationEnabled(boolean isEnabled) {
    AuthorizeHandlerImpl.setValidationEnabled(isEnabled);
  }
}

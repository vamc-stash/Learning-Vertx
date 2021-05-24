package com.learn.college.api.auth;

import com.learn.college.common.config.EnumConstants;
import lombok.experimental.UtilityClass;

/** Utility Class for Authentication */
@UtilityClass
public class AuthUtils {

  /**
   * Method sets Auth validation flag for given environment
   *
   * @param environment environment
   */
  public static void setAuthValidationEnabled(String environment) {
    boolean flag = isAuthValidationRequired(environment);
    AuthorizeHandler.setAuthValidationEnabled(flag);
    AuthenticateHandler.setAuthValidationEnabled(flag);
  }

  private boolean isAuthValidationRequired(String environment) {
    return EnumConstants.Environment.LOCAL.toString().equals(environment);
  }
}

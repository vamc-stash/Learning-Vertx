package com.learn.college.common.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Utility class for common functions */
@UtilityClass
public class CommonUtils {

  private static final String EMAIL_REGEX =
      "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

  private static final Pattern emailValidator = Pattern.compile(EMAIL_REGEX);

  private static final int PASSWORD_MIN_CHARS = 6;

  /**
   * Validates email
   *
   * @param email email
   * @return true if email is valid else false
   */
  public static boolean isValidEmail(String email) {
    Matcher matcher = emailValidator.matcher(email);
    return matcher.find();
  }

  /**
   * Validates password
   *
   * @param password password
   * @return true if success else false
   */
  public static boolean isValidPassword(String password) {
    return (password.length() >= PASSWORD_MIN_CHARS);
  }

  /**
   * The method generated UUID
   *
   * @return UUID
   */
  public static UUID generateUUID() {
    return UUID.randomUUID();
  }
}

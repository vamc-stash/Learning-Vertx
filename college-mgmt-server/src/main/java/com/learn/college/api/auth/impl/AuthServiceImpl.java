package com.learn.college.api.auth.impl;

import com.learn.college.api.auth.AuthService;
import com.learn.college.common.config.ApplicationConfiguration;
import com.learn.college.common.dto.UserDTO;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.VertxContextPRNG;
import io.vertx.ext.auth.jwt.JWTAuth;

import java.util.Base64;
import java.util.Objects;

import static com.learn.college.common.config.Constants.*;

public class AuthServiceImpl implements AuthService {

  private static final Integer RANDOM_PASSWORD_SALT_LENGTH = 24;

  private static final String RANDOM_STRING =
      "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";

  private final JWTAuth jwtAuth;

  public AuthServiceImpl() {
    jwtAuth = ApplicationConfiguration.getJwtAuth();
  }

  @Override
  public String generateAuthToken(UserDTO userDTO) {
    JsonObject payload =
        new JsonObject()
            .put(DB_USER_ID, userDTO.getUserId())
            .put(PERMISSION_ROLE_ID, userDTO.getRoleId());
    JWTOptions jwtOptions = new JWTOptions(ApplicationConfiguration.getDefaultJwtToken());
    return jwtAuth.generateToken(payload, jwtOptions);
  }

  @Override
  public String generatePasswordHash(String password) {
    String randomPassword = password + generateRandomString(RANDOM_PASSWORD_SALT_LENGTH);
    return Base64.getEncoder().encodeToString(randomPassword.getBytes());
  }

  @Override
  public String generateRandomString(int count) {
    StringBuilder randomStringBuilder = new StringBuilder();
    while (count-- != 0) {
      randomStringBuilder.append(
          RANDOM_STRING.charAt(VertxContextPRNG.current().nextInt(RANDOM_STRING.length())));
    }
    return randomStringBuilder.toString();
  }

  @Override
  public String decryptPassword(String password) {
    String decodePassword = new String(Base64.getDecoder().decode(password));
    return decodePassword.substring(0, decodePassword.length() - RANDOM_PASSWORD_SALT_LENGTH);
  }

  @Override
  public String validatePassword(UserDTO user, String password) {
    if (Objects.nonNull(user.getPassword())) {
      String decryptedPassword = decryptPassword(user.getPassword());
      if (decryptedPassword.equals(password)) {
        return VALIDATION_SUCCESS;
      } else {
        return ERROR_INVALID_PASSWORD;
      }
    } else {
      return ERROR_INVALID_AUTH_PARAMS;
    }
  }
}

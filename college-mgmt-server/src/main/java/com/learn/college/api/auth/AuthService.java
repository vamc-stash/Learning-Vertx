package com.learn.college.api.auth;

import com.learn.college.api.auth.impl.AuthServiceImpl;
import com.learn.college.common.dto.UserDTO;

/** The interface AuthService */
public interface AuthService {

  /**
   * Method gets the instance of AuthServiceImpl
   *
   * @return {@link AuthServiceImpl}
   */
  static AuthServiceImpl getInstance() {
    return new AuthServiceImpl();
  }

  /**
   * Generate Auth Token
   *
   * @param userDTO UserDTO
   */
  String generateAuthToken(UserDTO userDTO);

  /**
   * Generates Password Hash
   *
   * @param password password
   */
  String generatePasswordHash(String password);

  /**
   * Generates Random String
   *
   * @param count string length
   */
  String generateRandomString(int count);

  /**
   * Decrypt Password
   *
   * @param password password
   */
  String decryptPassword(String password);

  /**
   * Validate the password
   *
   * @param password password
   */
  String validatePassword(UserDTO user, String password);
}

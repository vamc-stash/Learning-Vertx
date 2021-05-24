package com.learn.college.api.mapper;

import com.learn.college.common.dto.UserDTO;
import com.learn.college.common.interfaces.BaseRowMapper;
import io.vertx.sqlclient.Row;

import static com.learn.college.common.config.Constants.*;

/** Class is responsible for converting database Row to POJO object */
public class UserDTOMapper implements BaseRowMapper<UserDTO> {

  // Singleton instance
  private static final UserDTOMapper userDTOMapper = new UserDTOMapper();

  /**
   * Gets UserDTOMapper instance
   *
   * @return UserDTOMapper instance
   */
  public static UserDTOMapper getInstance() {
    return userDTOMapper;
  }

  @Override
  public UserDTO mapRow(Row rs) {
    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(rs.getString(DB_USER_ID));
    userDTO.setFirstName(rs.getString(DB_FIRST_NAME));
    userDTO.setLastName(rs.getString(DB_LAST_NAME));
    userDTO.setEmail(rs.getString(DB_EMAIL));
    userDTO.setPassword(rs.getString(DB_PASSWORD));
    return userDTO;
  }
}

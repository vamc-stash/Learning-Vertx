package com.learn.college.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.learn.college.common.config.Constants.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDTO {

  @JsonProperty(DB_USER_ID)
  private String userId;

  @JsonProperty(DB_FIRST_NAME)
  private String firstName;

  @JsonProperty(DB_LAST_NAME)
  private String lastName;

  @JsonProperty(DB_EMAIL)
  private String email;

  @JsonProperty(DB_PASSWORD)
  private String password;

  private Integer roleId;

  @JsonProperty(AUTH_TOKEN)
  private String authToken;

  public UserDTO() {}

  public UserDTO(String email, String password) {
    this.email = email;
    this.password = password;
  }
}

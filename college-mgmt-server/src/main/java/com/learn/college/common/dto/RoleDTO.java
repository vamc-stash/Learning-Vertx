package com.learn.college.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.learn.college.common.config.Constants.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDTO {

  Integer id;

  String role;

  @JsonProperty(DB_ROLE_DESCRIPTION)
  String roleDisplay;
}

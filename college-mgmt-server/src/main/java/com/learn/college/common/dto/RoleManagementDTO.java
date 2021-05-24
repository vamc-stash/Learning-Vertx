package com.learn.college.common.dto;

import io.vertx.core.json.JsonArray;
import lombok.Data;

import java.util.Map;

@Data
public class RoleManagementDTO {

  /** Resources Map. key: combination of description and method, value: resource_key */
  private Map<String, String> resourceMap;

  /** Permissions Map. key: role_id, value: list of resource_ids */
  private Map<Integer, JsonArray> permissionMap;

  /** Roles Map key: role, value: role_id */
  private Map<String, Integer> rolesMap;
}

package com.learn.college.api.helper;

import com.learn.college.api.service.RoleManagementService;
import com.learn.college.common.dto.RoleManagementDTO;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

import static com.learn.college.common.config.Constants.DELIMITER;

/** Class responsible for loading role based permissions data */
@UtilityClass
@Slf4j
public class RoleManagementHelper {
  private static final String ROLE_PERMISSION_LOAD_ERROR = "Failed to load role permission data";

  private static final RoleManagementService roleManagementService =
      RoleManagementService.getInstance();

  private Map<String, String> resourceMap;
  private Map<Integer, JsonArray> permissionMap;
  private Map<String, Integer> rolesMap;

  /**
   * Method prepares KEY from requested url path and HTTP method. This key is used to fetch mapped
   * resource
   *
   * @param path url
   * @param method HTTP method
   * @return key
   */
  public static String prepareResourceMapKey(String path, String method) {
    return (path + DELIMITER + method);
  }

  /** Method load role based permission data from db */
  public static Future<Void> loadRolePermissionData() {
    Promise<Void> promise = Promise.promise();
    roleManagementService.loadRoleManagementData(
        handler -> {
          if (handler.succeeded()) {
            RoleManagementDTO data = handler.result();
            resourceMap = data.getResourceMap();
            permissionMap = data.getPermissionMap();
            rolesMap = data.getRolesMap();
            promise.complete();
          } else {
            log.error("Load role permission data failed, {}", handler.cause().getMessage());
            promise.fail(ROLE_PERMISSION_LOAD_ERROR);
          }
        });
    return promise.future();
  }

  /**
   * Method fetches ResourceId
   *
   * @param path url path
   * @param method http method
   * @return resource id
   */
  public static String getResource(String path, String method) {
    if (Objects.isNull(resourceMap) || resourceMap.isEmpty()) return null;
    return resourceMap.get(prepareResourceMapKey(path, method));
  }

  /**
   * Method fetches role id
   *
   * @param role role
   * @return roleId
   */
  public static Integer getRoleId(String role) {
    if (Objects.isNull(role)) {
      return null;
    }
    return rolesMap.get(role);
  }

  /**
   * Method checks permission of roleId to resourceId
   *
   * @param roleId role id
   * @param resourceId resource id
   * @return true if permission exists else false
   */
  public static Boolean hasPermission(Integer roleId, String resourceId) {
    if (Objects.isNull(permissionMap)
        || Objects.isNull(roleId)
        || StringUtils.isBlank(resourceId)) {
      log.error(" has Permission {} \n {} \n {}", permissionMap, roleId, resourceId);
      return false;
    }
    JsonArray permissions = permissionMap.get(roleId);
    if (permissions == null) return false;
    return permissions.contains(resourceId);
  }
}

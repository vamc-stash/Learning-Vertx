package com.learn.college.api.service.impl;

import com.learn.college.api.dao.RoleManagementDAO;
import com.learn.college.api.service.RoleManagementService;
import com.learn.college.common.config.EnumConstants;
import com.learn.college.common.dto.RoleManagementDTO;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

import java.util.Map;

public class RoleManagementServiceImpl implements RoleManagementService {

  private static final RoleManagementDAO roleManagementDAO = RoleManagementDAO.getInstance();

  @Override
  public void loadRoleManagementData(Handler<AsyncResult<RoleManagementDTO>> roleAccessHandler) {
    Future<Map<String, String>> resourceFuture =
        roleManagementDAO.getResourceMap(EnumConstants.RESOURCE_TYPE.API.getValue());
    Future<Map<Integer, JsonArray>> permissionFuture =
        roleManagementDAO.getPermissionMap(EnumConstants.RESOURCE_TYPE.API.getValue());
    Future<Map<String, Integer>> rolesFuture = roleManagementDAO.getRolesMap();

    CompositeFuture.join(resourceFuture, permissionFuture, rolesFuture)
        .onComplete(
            handler -> {
              if (handler.succeeded()) {
                RoleManagementDTO result = new RoleManagementDTO();
                result.setResourceMap(resourceFuture.result());
                result.setPermissionMap(permissionFuture.result());
                result.setRolesMap(rolesFuture.result());
                roleAccessHandler.handle(Future.succeededFuture(result));
              } else {
                roleAccessHandler.handle(Future.failedFuture(handler.cause()));
              }
            });
  }
}

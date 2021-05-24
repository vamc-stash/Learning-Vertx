package com.learn.college.api.service;

import com.learn.college.api.service.impl.RoleManagementServiceImpl;
import com.learn.college.common.dto.RoleManagementDTO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/** The RoleManagement Service Interface */
public interface RoleManagementService {

  /**
   * Method gets the instance of RoleManagementServiceImpl
   *
   * @return {@link RoleManagementService}
   */
  static RoleManagementService getInstance() {
    return new RoleManagementServiceImpl();
  }

  /**
   * Method loads role based permissions data from database
   *
   * @param handler async handler
   */
  void loadRoleManagementData(Handler<AsyncResult<RoleManagementDTO>> handler);
}

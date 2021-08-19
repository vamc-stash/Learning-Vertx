package com.learn.college.api.service.impl;

import com.learn.college.api.dao.RoleManagementDAO;
import com.learn.college.common.config.TestUtility;
import com.learn.college.common.dto.RoleManagementDTO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RoleManagementDAO.class})
public class RoleManagementServiceImplTest {

  private static RoleManagementDAO roleManagementDAO;

  @BeforeClass
  public static void beforeClass() {
    // enable mocking for all static methods of class
    PowerMockito.mockStatic(RoleManagementDAO.class);
    roleManagementDAO = mock(RoleManagementDAO.class);
    when(RoleManagementDAO.getInstance()).thenReturn(roleManagementDAO);
  }

  @Test
  public void testLoadRoleManagementData_P() {

    Promise<Map<String, String>> resourcePromise = Promise.promise();
    resourcePromise.complete(TestUtility.getResourceMap());
    PowerMockito.when(roleManagementDAO.getResourceMap(Mockito.anyString()))
        .thenReturn(resourcePromise.future());

    Promise<Map<Integer, JsonArray>> permissionPromise = Promise.promise();
    permissionPromise.complete(TestUtility.getPermissionMap());
    PowerMockito.when(roleManagementDAO.getPermissionMap(Mockito.anyString()))
        .thenReturn(permissionPromise.future());

    Promise<Map<String, Integer>> rolesPromise = Promise.promise();
    rolesPromise.complete(TestUtility.getRolesMap());
    PowerMockito.when(roleManagementDAO.getRolesMap()).thenReturn(rolesPromise.future());

    Handler<AsyncResult<RoleManagementDTO>> roleAccessHandler =
        handle -> {
          assertTrue(handle.succeeded());
          assertEquals(
              TestUtility.getResourceMap().size(), handle.result().getResourceMap().size());
          assertEquals(
              TestUtility.getPermissionMap().size(), handle.result().getPermissionMap().size());
          assertEquals(TestUtility.getRolesMap().size(), handle.result().getRolesMap().size());
        };

    new RoleManagementServiceImpl().loadRoleManagementData(roleAccessHandler);
  }
}

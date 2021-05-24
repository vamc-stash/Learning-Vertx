package com.learn.college.api.auth.impl;

import com.learn.college.api.auth.AuthorizeHandler;
import com.learn.college.api.helper.RoleManagementHelper;
import com.learn.college.common.error.ErrorCode;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import static com.learn.college.common.config.Constants.PERMISSION_ROLE_ID;
import static com.learn.college.common.error.ErrorCode.INVALID_RESOURCE_ID;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AuthorizeHandlerImpl implements AuthorizeHandler {

  @Setter private static boolean isValidationEnabled = Boolean.FALSE;

  @Override
  public void handle(RoutingContext context) {
    if (!isValidationEnabled) {
      context.next();
      return;
    }

    String resourceId =
        RoleManagementHelper.getResource(
            context.request().path(), context.request().method().name());

    if (StringUtils.isBlank(resourceId)) {
      context.fail(INVALID_RESOURCE_ID.getErrorResponse());
      return;
    }

    Integer roleId = context.user().principal().getInteger(PERMISSION_ROLE_ID, Integer.MAX_VALUE);
    if (RoleManagementHelper.hasPermission(roleId, resourceId)) {
      context.next();
    } else {
      context.fail(ErrorCode.AUTHORISATION_VERIFICATION_ERROR.getErrorResponse());
    }
  }
}

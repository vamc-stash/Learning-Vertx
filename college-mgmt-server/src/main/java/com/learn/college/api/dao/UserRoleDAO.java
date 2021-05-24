package com.learn.college.api.dao;

import com.learn.college.common.lib.DatabaseResource;
import com.learn.college.common.util.DbUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/** The type UserRoleDAO */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRoleDAO {

  // instance for fetching data from DB
  private static final DbUtils primaryDataResource = DatabaseResource.getPiDataResource();

  // Singleton instance
  private static final UserRoleDAO userRoleDAO = new UserRoleDAO();

  /**
   * Gets the Singleton instance of UserRoleDAO
   *
   * @return UserRoleDAO instance
   */
  public static UserRoleDAO getInstance() {
    return userRoleDAO;
  }

  private static final String SELECT_ROLE_ID_BY_USER_ID =
      "SELECT role_id FROM user_roles WHERE user_id = ?";

  /**
   * Method gets roleId by UserId
   *
   * @param userId user id
   * @param handler the handler
   */
  public void getRoleIdByUserId(String userId, Handler<AsyncResult<List<JsonObject>>> handler) {
    Tuple tuple = Tuple.tuple();
    tuple.addString(userId);
    primaryDataResource.execGet(SELECT_ROLE_ID_BY_USER_ID, tuple, handler);
  }
}

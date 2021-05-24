package com.learn.college.api.dao;

import com.learn.college.api.helper.RoleManagementHelper;
import com.learn.college.common.lib.DatabaseResource;
import com.learn.college.common.util.DbUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.HashMap;
import java.util.Map;

import static com.learn.college.common.config.Constants.*;

/** The type RoleManagement dao */
public class RoleManagementDAO {

  private static final DbUtils piDataResource = DatabaseResource.getPiDataResource();

  private static final RoleManagementDAO dao = new RoleManagementDAO();

  public static RoleManagementDAO getInstance() {
    return dao;
  }

  private static final String SELECT_RESOURCE_QUERY = "SELECT * FROM resource WHERE type = ?;";

  private static final String SELECT_PERMISSION_QUERY =
      "SELECT * FROM role_permissions WHERE type = ?;";

  private static final String SELECT_ROLES_QUERY = "SELECT * FROM roles;";

  /**
   * Method generates resources map. key: combination of description and method, value: resource_key
   *
   * @param type resource type
   * @return future
   */
  public Future<Map<String, String>> getResourceMap(String type) {
    Promise<Map<String, String>> promise = Promise.promise();
    Tuple tuple = Tuple.tuple();
    tuple.addString(type);
    piDataResource.getRowSet(
        SELECT_RESOURCE_QUERY,
        tuple,
        ar -> {
          if (ar.succeeded()) {
            Map<String, String> resourceMap = new HashMap<>();
            RowSet<Row> rows = ar.result();
            rows.forEach(
                row -> {
                  String key =
                      RoleManagementHelper.prepareResourceMapKey(
                          row.getString(RESOURCE_DESCRIPTION), row.getString(RESOURCE_METHOD));
                  resourceMap.put(key, row.getString(RESOURCE_KEY));
                });
            promise.complete(resourceMap);
          } else {
            promise.fail(ar.cause());
          }
        });
    return promise.future();
  }

  /**
   * Method generates permissions map. key: role_id, value: list of resource ids
   *
   * @param type resource type
   * @return future
   */
  public Future<Map<Integer, JsonArray>> getPermissionMap(String type) {
    Promise<Map<Integer, JsonArray>> promise = Promise.promise();
    Tuple tuple = Tuple.tuple();
    tuple.addString(type);
    piDataResource.getRowSet(
        SELECT_PERMISSION_QUERY,
        tuple,
        ar -> {
          if (ar.succeeded()) {
            Map<Integer, JsonArray> permissionMap = new HashMap<>();
            RowSet<Row> rows = ar.result();
            rows.forEach(
                row -> {
                  JsonArray resources = row.getJsonArray(PERMISSION_RESOURCES);
                  permissionMap.put(row.getInteger(PERMISSION_ROLE_ID), resources);
                });
            promise.complete(permissionMap);
          } else {
            promise.fail(ar.cause());
          }
        });
    return promise.future();
  }

  /**
   * Method generates roles map. key: role, value: role_id
   *
   * @return future
   */
  public Future<Map<String, Integer>> getRolesMap() {
    Promise<Map<String, Integer>> promise = Promise.promise();
    piDataResource.getRowSet(
        SELECT_ROLES_QUERY,
        Tuple.tuple(),
        ar -> {
          if (ar.succeeded()) {
            Map<String, Integer> rolesMap = new HashMap<>();
            RowSet<Row> rows = ar.result();
            rows.forEach(row -> rolesMap.put(row.getString(DB_ROLE), row.getInteger(DB_ROLE_ID)));
            promise.complete(rolesMap);
          } else {
            promise.fail(ar.cause());
          }
        });
    return promise.future();
  }
}

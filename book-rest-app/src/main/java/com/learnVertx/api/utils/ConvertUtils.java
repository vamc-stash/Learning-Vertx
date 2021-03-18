package com.learnVertx.api.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class ConvertUtils {

    public static JsonArray sqlRowsToJsonArray(RowSet<Row> rows) {
        JsonArray result = new JsonArray();
        rows.forEach(row -> {
            JsonObject obj = new JsonObject();
            rows.columnsNames().forEach(col -> {
                obj.put(col, row.getValue(col));
            });
            result.add(obj);
        });
        return result;
    }
}

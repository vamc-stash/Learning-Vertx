package com.learnVertx.api.utils;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ConfigUtils {
    public static ConfigRetriever configRetriever(Vertx vertx) {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);
        return ConfigRetriever.create(vertx, options);
    }
}

package api.service;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ConfigService {
    public static ConfigRetriever getConfigRetriever(Vertx vertx) {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);
        return ConfigRetriever.create(vertx, options);
    }
}

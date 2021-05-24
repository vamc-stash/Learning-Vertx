package com.learn.college.common.lib;

import com.learn.college.common.error.ErrorCode;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static com.learn.college.common.config.Constants.*;

/** ConfigRetriever Class the retrieve the application config */
@Slf4j
@UtilityClass
public class ConfigurationRetriever {

  /** Application Config File Location */
  public static final String APPLICATION_CONFIG_FILE =
      CONFIG_STORE_OPTIONS_DIR_PATH + CONFIG_FILE_PATH;

  /**
   * Config Retriever
   *
   * @param vertx Vertx instance
   * @param handler ConfigRetriever handler
   */
  public static void getConfig(Vertx vertx, Handler<Future<JsonObject>> handler) {
    ConfigStoreOptions store =
        new ConfigStoreOptions()
            .setType(CONFIG_STORE_OPTIONS_FILE)
            .setFormat(CONFIG_STORE_OPTIONS_FORMAT_JSON)
            .setConfig(new JsonObject().put(CONFIG_STORE_OPTIONS_PATH, APPLICATION_CONFIG_FILE));
    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(store);
    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig(
        ar -> {
          if (ar.succeeded()) {
            JsonObject config = ar.result();
            handler.handle(Future.succeededFuture(config));
          } else {
            log.error("Error: Failed to load configuration, {}", ar.cause().getMessage());
            handler.handle(
                Future.failedFuture(ErrorCode.INTERNAL_PROCESSING_ERROR.getErrorResponse()));
          }
        });
  }
}

package com.learn.college.common.config;

import com.learn.college.api.auth.AuthUtils;
import com.learn.college.common.lib.ConfigurationRetriever;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import lombok.Getter;

import static com.learn.college.common.config.Constants.PI_DATA;
import static com.learn.college.common.config.Constants.RESOURCES;

public class ApplicationConfiguration {

  /** The Constant APPLICATION_NAME */
  @Getter private static String applicationName;
  /** The Constant APPLICATION_VERSION */
  @Getter private static String applicationVersion;
  /** The Constant DEPLOYMENT_OPTIONS */
  @Getter private static DeploymentOptions deploymentOptions;
  /** The Constant HTTP_SERVER_OPTIONS */
  @Getter private static HttpServerOptions httpServerOptions;
  /** The Constant JWT_AUTH_OPTIONS */
  @Getter private static JWTAuthOptions jwtAuthOptions;
  /** The Constant Default JWT */
  @Getter private static JsonObject defaultJwtToken;
  /** The Constant DEPLOYMENT_OPTIONS */
  @Getter private static String environment;
  /** The Constant Database Config Object */
  @Getter private static JsonObject primaryResourceConfig;
  /** The JWTAuth */
  @Getter private static JWTAuth jwtAuth;

  private static final String HTTP = "http";
  private static final String DB = "db";
  private static final String NAME = "name";
  private static final String VERSION = "version";
  private static final String DEPLOY = "deploy";
  private static final String ENV = "env";
  private static final String AUTH = "auth";
  private static final String JWT_AUTH_OPTIONS = "jwtAuthOptions";
  private static final String DEFAULT_JWT_OPTIONS = "defaultJwtToken";

  private ApplicationConfiguration() {}

  /**
   * Initialize
   *
   * @param vertx Vertx instance
   * @param handler the handler
   */
  public static void initialize(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    ConfigurationRetriever.getConfig(
        vertx,
        configHandler -> {
          if (configHandler.succeeded()) {
            JsonObject config = configHandler.result();
            applicationName = config.getString(NAME);
            applicationVersion = config.getString(VERSION);
            environment = config.getString(ENV);
            httpServerOptions = new HttpServerOptions(config.getJsonObject(HTTP));
            deploymentOptions = new DeploymentOptions(config.getJsonObject(DEPLOY));
            AuthUtils.setAuthValidationEnabled(environment);
            JsonObject dbConfig = config.getJsonObject(DB);
            JsonObject authConfig = config.getJsonObject(AUTH);
            defaultJwtToken = authConfig.getJsonObject(DEFAULT_JWT_OPTIONS);
            CompositeFuture.join(
                    setAuthOptions(vertx, authConfig, environment), setDbOptions(dbConfig, environment))
                .onSuccess(success -> handler.handle(Future.succeededFuture()))
                .onFailure(failure -> handler.handle(Future.failedFuture(failure)));
          } else {
            handler.handle(
                Future.failedFuture(
                    new Throwable("Config retriever failed", configHandler.cause())));
          }
        });
  }

  /**
   * Method prepares authentication options
   *
   * @param authConfig auth config json
   * @param env environment
   * @return future CompletionFuture
   */
  public static Future<Void> setAuthOptions(Vertx vertx, JsonObject authConfig, String env) {
    Promise<Void> promise = Promise.promise();
    JsonObject authOptions = authConfig.getJsonObject(JWT_AUTH_OPTIONS);
    if (EnumConstants.Environment.LOCAL.toString().equals(env) || EnumConstants.Environment.TEST.toString().equals(env)) {
      jwtAuthOptions = new JWTAuthOptions(authOptions);
      jwtAuth = JWTAuth.create(vertx, jwtAuthOptions);
      /*PubSecKeyOptions options = new PubSecKeyOptions().setAlgorithm("HS256").setBuffer("College");
      jwtAuthOptions = new JWTAuthOptions().addPubSecKey(options);*/
      promise.complete();
    } else {
      // dev, prod purpose
    }
    return promise.future();
  }

  /**
   * Method prepares db options
   *
   * @param dbConfig auth config json
   * @param env environment
   * @return future CompletionFuture
   */
  public static Future<Void> setDbOptions(JsonObject dbConfig, String env) {
    Promise<Void> promise = Promise.promise();
    JsonObject resources = dbConfig.getJsonObject(RESOURCES);
    if (EnumConstants.Environment.LOCAL.toString().equals(env) || EnumConstants.Environment.TEST.toString().equals(env)) {
      primaryResourceConfig = resources.getJsonObject(PI_DATA);
      promise.complete();
    } else {
      // dev, prod purpose

    }
    return promise.future();
  }
}

package com.learn.college.common.config;

import com.learn.college.api.auth.AuthUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*", "javax.crypto.*"})
public class ApplicationConfigurationTest {
  @Mock private Handler<AsyncResult<Void>> voidAsyncHandler;

  @ClassRule public static RunTestOnContext rule = new RunTestOnContext();

  static {
    AuthUtils.setAuthValidationEnabled(EnumConstants.Environment.TEST.toString());
    ConfigUtil.loadSystemProperties();
  }

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void init() {
    Vertx vertx = Vertx.vertx();
    String verticleName = "testVerticle";
    ApplicationConfiguration.initialize(vertx, handler -> {});
    ApplicationStartup.start(vertx, verticleName, voidAsyncHandler);
  }

  @Test
  public void testSetAuthOptions_P() {
    JsonObject authConfig = new JsonObject();
    List<JsonObject> jwtAuthOptions = new ArrayList<>();
    jwtAuthOptions.add(
        new JsonObject()
            .put("buffer", "college-mgmt-server-key")
            .put("algorithm", "HS256")
            .put("symmetric", true));
    authConfig.put("jwtAuthOptions", jwtAuthOptions.get(0));
    String env = "env";
    ApplicationConfiguration.setAuthOptions(Vertx.currentContext().owner(), authConfig, env);
  }

  @Test
  public void testSetDbOptions_P() {
    JsonObject dbConfig = new JsonObject();
    JsonObject resources = new JsonObject();
    resources.put(
        "pi_data",
        new JsonObject()
            .put(
                "connection",
                new JsonObject()
                    .put("port", 3306)
                    .put("database", "testDb")
                    .put("host", "localhost")));
    dbConfig.put("resources", resources);
    String env = "env";
    ApplicationConfiguration.setAuthOptions(Vertx.currentContext().owner(), dbConfig, env);
  }
}

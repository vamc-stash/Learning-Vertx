package com.learn.college.api.health.procedures;

import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;

import java.io.File;

import static com.learn.college.common.config.Constants.*;

public class DiskSpaceAvailHandler implements Handler<Promise<Status>> {

  private final Long minSpaceRequired;

  public DiskSpaceAvailHandler(JsonObject options) {
    super();

    this.minSpaceRequired = options.getLong(MIN_SPACE_REQUIRED);
  }

  @Override
  public void handle(Promise<Status> event) {
    File appDirPath = new File(APP_DIR_PATH);
    Long availableSpace = appDirPath.getUsableSpace();

    JsonObject obj = new JsonObject();
    obj.put(MIN_SPACE_REQUIRED, minSpaceRequired);
    obj.put(AVAILABLE_SPACE, availableSpace);

    if (availableSpace > minSpaceRequired) {
      event.complete(Status.OK(obj));
    } else {
      event.complete(Status.KO(obj));
    }
  }
}

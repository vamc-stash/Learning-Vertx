package com.learn.college.common.interfaces;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;

public interface BaseRest {

  void mountRouter(Vertx vertx, Router router);

  void addHandlerByOperationId(RouterBuilder routerBuilder);

  Router getRouter(Vertx vertx);
}

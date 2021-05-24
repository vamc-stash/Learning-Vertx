package api.handler;

import api.service.HttpResponseService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class InitHandler {
    public void home(RoutingContext ctx) {
        HttpResponseService.sendOkResponse(ctx, new JsonObject().put("msg", "welcome home!"));
    }
}

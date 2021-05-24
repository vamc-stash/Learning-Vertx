package api.handler;

import api.model.Role;
import api.model.User;
import api.service.AuthService;
import api.service.HttpResponseService;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import utils.Constants;

import java.util.UUID;

public class AuthHandler {
    private final AuthService authService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthHandler.class);

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    public void signup(RoutingContext ctx) {
        JsonObject reqBody = ctx.getBodyAsJson();
        String username = reqBody.getString("username");
        String password = reqBody.getString("password");
        authService.signup(username, password)
                .onSuccess(user -> {
                    HttpResponseService.sendCreatedResponse(ctx, new JsonObject().put("msg", "sign-up is success"));
                })
                .onFailure(handler -> {
                    HttpResponseService.sendErrorResponse(ctx, handler.getCause());
                });
    }

    public void login(RoutingContext ctx) {
        JsonObject reqBody = ctx.getBodyAsJson();
        String username = reqBody.getString("username");
        String password = reqBody.getString("password");
        authService.login(username, password)
                .onSuccess(user -> {
                    if(user == null) {
                        HttpResponseService.sendErrorResponse(ctx, "wrong credentials");
                    } else {
                        Session session = ctx.session();
                        session.put("session-id", UUID.randomUUID().toString());
                        HttpResponseService.sendCreatedResponse(ctx, new JsonObject().put("msg", user.getUsername() + " logged in successfully"));
                    }
                })
                .onFailure(handler -> {
                    HttpResponseService.sendErrorResponse(ctx, handler.getCause());
                });
    }

    public void logout(RoutingContext ctx) {
        Session session = ctx.session();
        String sessionId = session.get("session-id");
        LOGGER.info("Session ID: " + sessionId );
        session.destroy();
        ctx.redirect("/")
                .onSuccess(handler -> {
                    LOGGER.info("logged out successfully");
                })
                .onFailure(handler -> {
                    LOGGER.info("Failure in logging out");
                });
    }
}

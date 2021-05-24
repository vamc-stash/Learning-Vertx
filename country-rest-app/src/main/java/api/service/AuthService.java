package api.service;

import api.model.Role;
import api.model.User;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.impl.SqlTemplate;
import utils.Constants;
import utils.QueryUtils;
import verticle.MainVerticle;

public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final MySQLPool client;

    public AuthService(MySQLPool client) {
        this.client = client;
    }

    public Future<User> signup(String username, String password) {
        Promise<User> promise = Promise.promise();
        client.preparedQuery(QueryUtils.ADD_USER).execute(Tuple.of(username, password), ar -> {
            if(ar.succeeded()) {
                CompositeFuture.all(
                        DbService.execPreparedQuery(client, QueryUtils.GET_USER, Tuple.of(username)),
                        DbService.execPreparedQuery(client, QueryUtils.GET_USER_ROLE, Tuple.of(Constants.ROLE_USER))
                ).compose(handler -> {
                    RowSet<Row> usersRows =  handler.resultAt(0);
                    String uName = usersRows.iterator().next().getString("username");
                    RowSet<Row> rolesRows =  handler.resultAt(1);
                    String uRole = rolesRows.iterator().next().getString("role");
                    return DbService.execPreparedQuery(client, QueryUtils.ADD_USER_ROLE, Tuple.of(uName, uRole));
                }).onSuccess(handler -> {
                    promise.complete(new User(username, password));
                }).onFailure(handler -> {
                    LOGGER.error(ar.cause());
                    promise.fail(handler.getCause());
                });
            } else {
                LOGGER.error(ar.cause());
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    public Future<User> login(String username, String password) {
        Promise<User> promise = Promise.promise();
        client.preparedQuery(QueryUtils.SELECT_ONE_USER).execute(Tuple.of(username, password), ar -> {
            if(ar.succeeded()) {
                if(ar.result().size() <= 0) {
                    promise.complete(null);
                } else {
                    promise.complete(new User(username, password));
                }
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }
}

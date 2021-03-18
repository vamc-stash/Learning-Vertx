package example03;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
    private final int httpPort = 8080;

    @Override
    public void start(Promise<Void> promise) {
        /*
        * Execute only one of the below methods and comment the rest for the demo
        * */
        //FileUtils.callbackHell(vertx);
        futureMethod1(promise);
        //futureMethod2(promise);

        /*
        * CompositeFuture
        * "all" returns a future that succeeds if all the futures passed as parameters succeed, and fails if at least one fails.
        * "any" returns a future that succeeds if any one of the futures passed as parameters succeed, and fails if all fail.
        * */
        CompositeFuture.any(
                RouterUtils.setUpRouter1(vertx),
                RouterUtils.setUpRouter2(vertx)
        ).onSuccess(handler -> {
            Router router = Router.router(vertx);
            router.mountSubRouter("/api/v1", handler.resultAt(0));
            router.mountSubRouter("/api/v1", handler.resultAt(1));
           vertx.createHttpServer().requestHandler(router).listen(8080, res -> {
               if (res.succeeded()) {
                   System.out.println("Server is listening at port: " + httpPort );
                   promise.complete();
               } else {
                   System.out.println("Server is failed to start at port: " + httpPort );
                   promise.fail(res.cause());
               }
           });
        }).onFailure(handler -> {
            promise.fail(handler.getCause().getMessage());
        });
    }

    private void futureMethod1(Promise<Void> promise) {
        Future<Buffer> readFuture = FileUtils.readFile(vertx);
        readFuture.onSuccess(asyncResult1 -> {
           Future<String> writeFuture = FileUtils.writeFile(vertx, asyncResult1);
           writeFuture.onSuccess(asyncResult2 -> {
               Future<String> copyFuture = FileUtils.copyFile(vertx, asyncResult2);
               copyFuture.onSuccess(System.out::println);

               copyFuture.onFailure(asyncResult3 -> {
                   promise.fail(asyncResult3.getCause().getMessage());
               });
           });
           writeFuture.onFailure(asyncResult2 -> {
               promise.fail(asyncResult2.getCause().getMessage());
           });
        });
        readFuture.onFailure(asyncResult1 -> {
            promise.fail(asyncResult1.getCause().getMessage());
        });
    }

    private void futureMethod2(Promise<Void> promise) {
        Future<Buffer> readFuture = FileUtils.readFile(vertx);
        readFuture.compose(asyncResult1 -> {
            return FileUtils.writeFile(vertx, asyncResult1);
        }).compose(asyncResult2 -> {
            return FileUtils.copyFile(vertx, asyncResult2);
        }).onSuccess(handler -> {
            System.out.println(handler);
            promise.complete();
        }).onFailure(handler -> {
            promise.fail(handler.getCause().getMessage());
        });
    }

    @Override
    public void stop() {
        System.out.println("Shutting down application");
    }
}

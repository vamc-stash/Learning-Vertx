package example03;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

public class FileUtils {
    private static final String filePath = "src/main/java/example03/sample.txt";
    private static final String filePathTo = "src/main/java/example03/sample-copy.txt";

    public static void callbackHell(Vertx vertx) {

        vertx.fileSystem().readFile(filePath, handler -> {
            if(handler.succeeded()) {
                vertx.fileSystem().writeFile(filePath, handler.result(), handler2 -> {
                    if(handler2.succeeded()) {
                        vertx.fileSystem().copy(filePath, filePathTo, handler3 -> {
                            if(handler3.succeeded()) {
                                System.out.println("Successfully copied contents from: " + handler3.result() + " to: " + filePathTo);
                            } else {
                                System.err.println("Error while copying from a file: " + handler3.cause().getMessage());
                            }
                        });
                    } else {
                        System.err.println("Error while writing to a file: " + handler2.cause().getMessage());
                    }
                });
            } else {
                System.err.println("Error while reading from a file: " + handler.cause().getMessage());
            }
        });
    }

    public static Future<Buffer> readFile(Vertx vertx) {
        Promise<Buffer> promise = Promise.promise();

        vertx.fileSystem().readFile(filePath, handler -> {
            if(handler.succeeded()) {
                promise.complete(handler.result());
            } else {
                System.err.println("Error while reading from a file: " + handler.cause().getMessage());
                promise.fail(handler.cause());
            }
        });

        return promise.future();
    }



    public static Future<String> writeFile(Vertx vertx, Buffer data) {
        Promise<String> promise = Promise.promise();

        vertx.fileSystem().writeFile(filePath, data, handler -> {
            if(handler.succeeded()) {
                promise.complete(filePath);
            } else {
                System.err.println("Error while writing to a file: " + handler.cause().getMessage());
                promise.fail(handler.cause());
            }
        });

        return promise.future();
    }

    public static Future<String> copyFile(Vertx vertx, String filePath) {
        Promise<String> promise = Promise.promise();

        vertx.fileSystem().copy(filePath, filePathTo, handler -> {
            if(handler.succeeded()) {
                promise.complete("Successfully copied contents from: " + filePath + " to: " + filePathTo);
            } else {
                System.err.println("Error while copying from a file: " + handler.cause().getMessage());
                promise.fail(handler.cause());
            }
        });

        return promise.future();
    }
}

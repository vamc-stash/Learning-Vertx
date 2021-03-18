import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import java.util.concurrent.CountDownLatch;

public class App {
    public static void main(String[] args) throws InterruptedException {
        final Logger LOGGER =  LoggerFactory.getLogger(App.class);

        /*
        * Scenario 1:- 2 threads are logged in the console. Ideally it should be one thread, but one (or more) JVM maintenance
        * thread may present.
        * */
        LOGGER.info(Thread.activeCount()); //1 or 2


        /*
        * Scenario 2:- It yields 4 Vert.x (2 additional threads). One is running the application, and another is called
        * vertx-blocked-thread-checker(debugging purpose, it is useful to know if a handler is blocking longer than we want).
        * */
        Vertx vertx = Vertx.vertx();
        LOGGER.info(Thread.activeCount()); //4 (2 + 2)


        /*
        * Scenario 3:-
        * Lets deploy 5 verticles - 10 threads (4 + 6)
        * 8 verticles - 12 threads (4 + 8)
        * 10 verticles - 12 threads
        * 100 verticles - 12 threads .... This value doesn't change
        * There 4 logical processors(2 cores) i.e., 4 CPU core threads in my machine. So, Vert.x assigns at max. 2 event loop
        * threads per CPU core thread, so a total of 8 additional threads.
        * */
        int verticleInstances = 5;
        final CountDownLatch latch = new CountDownLatch(verticleInstances);
        for(int i=0; i<verticleInstances; i++) {
            vertx.deployVerticle(new SampleVerticle(i), cd -> latch.countDown());
            LOGGER.info(Thread.activeCount());
        }
        latch.await();
        LOGGER.info(Thread.activeCount());


        /*
        * Scenario 4:- Deploy worker verticles. Worker verticles are used to execute long running or blocking tasks.
        * worker verticles use a separate thread pool, which is of size 20 by default. You can control the size of this pool by
        * calling setWorkerPoolSize() on VertxOptions, then passing them upon Vert.x initialization.
        * Unlike regular verticles, worker verticles aren't distributed evenly across threads, since they serve a different
        * purpose.
        * */
        //Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(10));
        /*DeploymentOptions workerOpts = new DeploymentOptions().setWorker(true);
        for (int i = 0; i < verticleInstances; i++) {
            vertx.deployVerticle(new SampleVerticle(i), workerOpts, cd -> latch.countDown());
        }
        latch.await();
        LOGGER.info(Thread.activeCount());*/
    }
}


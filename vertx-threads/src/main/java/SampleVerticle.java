import io.vertx.core.AbstractVerticle;

public class SampleVerticle extends AbstractVerticle {
    int i;

    SampleVerticle(int i) {
        this.i = i;
    }

    @Override
    public void start() {
        System.out.println(Thread.currentThread().getName() + ": " + i);
    }
}

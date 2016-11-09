package pl.ciruk.security.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPools {
    ThreadPoolExecutor boundedPool(int size) {
        return new ThreadPoolExecutor(
                size,
                size,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100 * size)) {
            @Override
            protected void afterExecute(Runnable runnable, Throwable throwable) {
                super.afterExecute(runnable, throwable);
                if (throwable == null && runnable instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) runnable;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (CancellationException ce) {
                        throwable = ce;
                    } catch (ExecutionException ee) {
                        throwable = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // ignore/reset
                    }
                }
                if (throwable != null) {
                    // Handle error
                    throwable.printStackTrace();
                }
            }
        };
    }
}

package xyz.refinedev.phoenix.handler.database.local;

import xyz.refinedev.lib.io.lettuce.core.RedisFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class LocalRedisFuture<T> extends CompletableFuture<T> implements RedisFuture<T> {

    LocalRedisFuture() {}

    static <T> LocalRedisFuture<T> of(T t) {
        LocalRedisFuture<T> localRedisFuture;
        (localRedisFuture = new LocalRedisFuture()).complete(t);
        return localRedisFuture;
    }

    static <T> LocalRedisFuture<T> of(Throwable throwable) {
        LocalRedisFuture<T> localRedisFuture;
        (localRedisFuture = new LocalRedisFuture()).completeExceptionally(throwable);
        return localRedisFuture;
    }

    public String getError() {
        if (!this.isCompletedExceptionally()) {
            return null;
        }
        try {
            this.get();
            return null;
        } catch (ExecutionException executionException) {
            return String.valueOf(executionException.getCause().getMessage());
        } catch (Exception exception) {
            return String.valueOf(exception.getMessage());
        }
    }

    public boolean await(long value, TimeUnit timeUnit) throws InterruptedException {
        try {
            this.get(value, timeUnit);
            return true;
        } catch (ExecutionException executionException) {
            return true;
        } catch (TimeoutException timeoutException) {
            return false;
        }
    }
}

package xyz.refinedev.phoenix.utils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreadFactoryBuilder {

    private String text = null;

    private Boolean enabled = null;

    private Integer integer = null;

    private UncaughtExceptionHandler uncaughtExceptionHandler = null;

    private ThreadFactory threadFactory = null;

    public ThreadFactoryBuilder setNameFormat(String text) {
        this.text = text;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ThreadFactoryBuilder setPriority(int integer) {
        Preconditions.checkArgument(integer >= 1, "Thread priority (%s) must be >= %s", integer, 1);
        Preconditions.checkArgument(
                integer <= 10, "Thread priority (%s) must be <= %s", integer, 10);
        this.integer = integer;
        return this;
    }

    public ThreadFactoryBuilder setUncaughtExceptionHandler(
            UncaughtExceptionHandler uncaughtExceptionHandler2) {
        this.uncaughtExceptionHandler = Preconditions.checkNotNull(uncaughtExceptionHandler2);
        return this;
    }

    public ThreadFactoryBuilder setThreadFactory(ThreadFactory threadFactory2) {
        this.threadFactory = Preconditions.checkNotNull(threadFactory2);
        return this;
    }

    public ThreadFactory build() {
        return build(this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder threadFactoryBuilder) {
        String text2 = threadFactoryBuilder.text;
        Boolean matches = threadFactoryBuilder.enabled;
        Integer value = threadFactoryBuilder.integer;
        UncaughtExceptionHandler uncaughtExceptionHandler2 =
                threadFactoryBuilder.uncaughtExceptionHandler;
        ThreadFactory threadFactory2 =
                threadFactoryBuilder.threadFactory != null
                        ? threadFactoryBuilder.threadFactory
                        : Executors.defaultThreadFactory();
        AtomicLong atomicLong = text2 != null ? new AtomicLong(0L) : null;
        return result -> {
            Thread thread = threadFactory2.newThread(result);
            if (text2 != null) {
                thread.setName(String.format(text2, atomicLong.getAndIncrement()));
            }
            if (matches != null) {
                thread.setDaemon(matches);
            }
            if (value != null) {
                thread.setPriority(value);
            }
            if (uncaughtExceptionHandler2 != null) {
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler2);
            }
            return thread;
        };
    }
}

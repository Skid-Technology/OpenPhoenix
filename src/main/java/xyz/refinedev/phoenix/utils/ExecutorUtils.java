package xyz.refinedev.phoenix.utils;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PhoenixPlatform;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExecutorUtils {

    private static final String text = "This is a utility class and cannot be instantiated";

    private static final long time = 120L;

    public static void applyExecutorService(ExecutorService executorService) {
        matchesEnabled(executorService, TimeUnit.SECONDS.toMillis(time));
    }

    public static boolean matchesEnabled(ExecutorService executorService, long value) {
        try {
            executorService.shutdown();
            if (executorService.awaitTermination(value, TimeUnit.MILLISECONDS)) {
                return true;
            }
            executorService.shutdownNow();
        } catch (InterruptedException interruptedException) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return false;
    }

    public static ThreadFactoryBuilder create(
            String text, int value, boolean matches, Logger logger) {
        return new ThreadFactoryBuilder()
                .setNameFormat(text)
                .setPriority(value)
                .setUncaughtExceptionHandler(new ExecutorUtils.LoggingExceptionHandler(logger))
                .setDaemon(matches);
    }

    private ExecutorUtils() {
        throw new UnsupportedOperationException(text);
    }

    public static class LoggingExceptionHandler implements UncaughtExceptionHandler {

        private final Logger logger;

        private static final String text = " threw an uncaught exception:";

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            this.logger.log(Level.SEVERE, thread.getName() + text, throwable);
        }

        public LoggingExceptionHandler(Logger logger2) {
            this.logger = logger2;
        }
    }

    public static class MainThreadExecutor implements Executor {

        private final PhoenixPlatform phoenixPlatform;

        @Override
        public void execute(@NotNull Runnable runnable) {
            ExecuteOnMainThread executeOnMainThread = this.phoenixPlatform.getExecuteOnMainThread();
            executeOnMainThread.run(runnable::run);
        }

        public MainThreadExecutor(PhoenixPlatform phoenixPlatform2) {
            this.phoenixPlatform = phoenixPlatform2;
        }
    }
}

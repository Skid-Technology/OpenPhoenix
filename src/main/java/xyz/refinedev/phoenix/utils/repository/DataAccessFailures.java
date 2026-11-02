package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.phoenix.PlatformGetter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public final class DataAccessFailures {

    public static final int DEGRADED_THRESHOLD = 5;

    private static final Map<String, AtomicInteger> CONSECUTIVE = new ConcurrentHashMap<>();

    private DataAccessFailures() {}

    public static void record(
            String text, String text2, String text3, String text4, Throwable throwable) {
        boolean matches = countFailure(text);
        PlatformGetter.getInstance()
                .getLogger()
                .log(
                        Level.SEVERE,
                        "Data access failure ["
                                + text
                                + "/"
                                + text2
                                + "] on "
                                + text3
                                + (text4 == null ? "" : " (" + text4 + ")"),
                        throwable);
        try {
            PlatformGetter.getInstance()
                    .getMetricsHandler()
                    .metric("phoenix_data_access_failure")
                    .findMetric("backend", text)
                    .findMetric("operation", text2)
                    .findMetric("entity", text3)
                    .initialize();
        } catch (Throwable throwable2) {
        }
        if (matches) {
            PlatformGetter.getInstance()
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Data access to "
                                    + text
                                    + " is DEGRADED: "
                                    + 5
                                    + " consecutive failures. Reads are returning empty results, which callers cannot distinguish from missing data.");
        }
    }

    static boolean countFailure(String text) {
        return counter(text).incrementAndGet() == 5;
    }

    public static void recordSuccess(String text) {
        counter(text).set(0);
    }

    static int consecutiveFailures(String text) {
        return counter(text).get();
    }

    private static AtomicInteger counter(String text) {
        return CONSECUTIVE.computeIfAbsent(text, result -> new AtomicInteger());
    }

    static void resetForTesting() {
        CONSECUTIVE.clear();
    }
}

package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.handler.metric.Metric;

public interface IMetricsHandler {

    void start();

    void stop();

    boolean isEnabled();

    @NotNull
    default Metric metric(@NotNull String text) {
        return new Metric(this, text);
    }

    void write(@NotNull Metric metric);
}

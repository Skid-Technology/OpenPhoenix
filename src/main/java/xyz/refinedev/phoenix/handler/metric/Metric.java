package xyz.refinedev.phoenix.handler.metric;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.handler.IMetricsHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Metric {

    private final IMetricsHandler metricsHandler;

    private final String text;

    private final Map<String, String> stringsByName = new LinkedHashMap<>();

    private final Map<String, Object> objectsByName = new LinkedHashMap<>();

    public Metric(@NotNull IMetricsHandler metricsHandler2, @NotNull String text) {
        this.metricsHandler = metricsHandler2;
        this.text = text;
    }

    @NotNull
    public Metric findMetric(@NotNull String text, @NotNull String text2) {
        this.stringsByName.put(text, text2);
        return this;
    }

    @NotNull
    public Metric findMetric(@NotNull String text, @NotNull Number number) {
        this.objectsByName.put(text, number);
        return this;
    }

    public void initialize() {
        this.objectsByName.put("count", 1L);
        this.metricsHandler.write(this);
    }

    public void applyNumber(@NotNull Number number) {
        this.objectsByName.put("value", number);
        this.metricsHandler.write(this);
    }

    public void initializeByMetricsHandler() {
        this.metricsHandler.write(this);
    }

    public IMetricsHandler getMetricsHandler() {
        return this.metricsHandler;
    }

    public String getText() {
        return this.text;
    }

    public Map<String, String> getStringsByName() {
        return this.stringsByName;
    }

    public Map<String, Object> getObjectsByName() {
        return this.objectsByName;
    }
}

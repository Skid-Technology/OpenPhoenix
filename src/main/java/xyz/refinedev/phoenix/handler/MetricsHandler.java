package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.influxdb.client.InfluxDBClient;
import xyz.refinedev.lib.influxdb.client.InfluxDBClientFactory;
import xyz.refinedev.lib.influxdb.client.WriteApi;
import xyz.refinedev.lib.influxdb.client.domain.WritePrecision;
import xyz.refinedev.lib.influxdb.client.write.Point;
import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.metric.Metric;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.logging.Level;

public class MetricsHandler implements IMetricsHandler {

    private final CommonPlatform platform;

    private InfluxDBClient influxDBClient;

    private WriteApi writeApi;

    private boolean enabled;

    public MetricsHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    @Override
    public void start() {
        if (GlobalConfig.METRICS.ENABLED) {
            try {
                this.influxDBClient =
                        InfluxDBClientFactory.create(
                                GlobalConfig.METRICS.URL,
                                GlobalConfig.METRICS.TOKEN.toCharArray(),
                                GlobalConfig.METRICS.ORG,
                                GlobalConfig.METRICS.BUCKET);
                this.writeApi = this.influxDBClient.makeWriteApi();
                this.enabled = true;
                this.platform
                        .getLogger()
                        .info("Metrics exporter started, writing to " + GlobalConfig.METRICS.URL);
            } catch (Exception exception) {
                this.enabled = false;
                this.platform
                        .getLogger()
                        .log(
                                Level.WARNING,
                                "Failed to start metrics exporter: " + exception.getMessage(),
                                exception);
            }
        }
    }

    @Override
    public void stop() {
        if (this.enabled) {
            this.enabled = false;
            try {
                if (this.writeApi != null) {
                    this.writeApi.close();
                }
                if (this.influxDBClient != null) {
                    this.influxDBClient.close();
                }
                this.platform.getLogger().info("Metrics exporter stopped.");
            } catch (Exception exception) {
                this.platform
                        .getLogger()
                        .log(
                                Level.WARNING,
                                "Failed to cleanly stop metrics exporter: "
                                        + exception.getMessage(),
                                exception);
            }
        }
    }

    @Override
    public void write(@NotNull Metric metric) {
        if (this.enabled && this.writeApi != null) {
            try {
                Point point =
                        Point.measurement(metric.getText())
                                .addTag("server", GlobalConfig.SERVER.NAME)
                                .time(Instant.now(), WritePrecision.MS);
                for (Entry<String, String> entry : metric.getStringsByName().entrySet()) {
                    point.addTag((String) entry.getKey(), (String) entry.getValue());
                }
                for (Entry<String, Object> entry2 : metric.getObjectsByName().entrySet()) {
                    this.applyPoint(point, (String) entry2.getKey(), entry2.getValue());
                }
                this.writeApi.writePoint(point);
            } catch (Exception exception) {
                if (this.platform.isDebugMode()) {
                    this.platform
                            .getLogger()
                            .log(
                                    Level.WARNING,
                                    "Metrics write failed: " + exception.getMessage(),
                                    exception);
                }
            }
        }
    }

    private void applyPoint(Point point, String text, Object result) {
        if (result instanceof Long
                || result instanceof Integer
                || result instanceof Short
                || result instanceof Byte) {
            point.addField(text, ((Number) result).longValue());
        } else if (result instanceof Number) {
            point.addField(text, ((Number) result).doubleValue());
        } else if (result instanceof Boolean) {
            point.addField(text, (Boolean) result);
        } else if (result != null) {
            point.addField(text, result.toString());
        }
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    public InfluxDBClient getInfluxDBClient() {
        return this.influxDBClient;
    }

    public WriteApi getWriteApi() {
        return this.writeApi;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}

package xyz.refinedev.phoenix.metrics;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.lang.reflect.Method;
import java.util.Map.Entry;

public class BukkitMetrics {

    private final Phoenix plugin;

    private Method method;

    private Method methodMethod;

    private boolean enabled;

    public void initialize() {
        BukkitMetrics bukkitMetrics = this;
        Tasks.runAsyncTimer(this.plugin, bukkitMetrics::initializeByPlugin, 600L, 600L);
    }

    private void initializeByPlugin() {
        if (this.plugin.getMetricsHandler().isEnabled()) {
            this.plugin
                    .getMetricsHandler()
                    .metric("phoenix_bukkit_players_online")
                    .applyNumber(Bukkit.getOnlinePlayers().size());
            int value = 0;
            for (World world : Bukkit.getWorlds()) {
                value += world.getLoadedChunks().length;
            }
            this.plugin
                    .getMetricsHandler()
                    .metric("phoenix_bukkit_loaded_chunks")
                    .applyNumber(value);
            Double value2;
            if ((value2 = this.getDouble()) != null) {
                this.plugin
                        .getMetricsHandler()
                        .metric("phoenix_bukkit_tps")
                        .findMetric("window", "1m")
                        .applyNumber(value2);
            }
            Double value3;
            if ((value3 = this.getDoubleByMethodMethod()) != null) {
                this.plugin.getMetricsHandler().metric("phoenix_bukkit_mspt").applyNumber(value3);
            }
            Runtime runtime;
            long value4 = (runtime = Runtime.getRuntime()).totalMemory();
            long value5 = runtime.freeMemory();
            long value6 = runtime.maxMemory();
            this.plugin
                    .getMetricsHandler()
                    .metric("phoenix_bukkit_heap")
                    .findMetric("used", value4 - value5)
                    .findMetric("free", value5)
                    .findMetric("max", value6)
                    .initializeByMetricsHandler();
            long value7;
            if ((value7 = this.plugin.getDatabaseHandler().pingRedisMillis()) >= 0L) {
                this.plugin.getMetricsHandler().metric("phoenix_redis_ping_ms").applyNumber(value7);
            }
            for (Entry<String, Long> entry :
                    this.plugin.getDatabaseHandler().getPoolStats().entrySet()) {
                this.plugin
                        .getMetricsHandler()
                        .metric("phoenix_db_pool")
                        .findMetric("stat", (String) entry.getKey())
                        .applyNumber((Number) entry.getValue());
            }
        }
    }

    private void initializeByEnabled() {
        if (!this.enabled) {
            this.enabled = true;
            try {
                Class[] classs = new Class[0];
                this.method = Server.class.getMethod("getTPS", classs);
            } catch (NoSuchMethodException noSuchMethodException) {
                this.method = null;
            }
            try {
                Class[] classs2 = new Class[0];
                this.methodMethod = Server.class.getMethod("getAverageTickTime", classs2);
            } catch (NoSuchMethodException noSuchMethodException2) {
                this.methodMethod = null;
            }
        }
    }

    private Double getDouble() {
        this.initializeByEnabled();
        if (this.method == null) {
            return null;
        }
        try {
            double[] values;
            return (values = (double[]) this.method.invoke(Bukkit.getServer())) != null
                            && values.length > 0
                    ? values[0]
                    : null;
        } catch (Exception exception) {
            return null;
        }
    }

    private Double getDoubleByMethodMethod() {
        this.initializeByEnabled();
        if (this.methodMethod == null) {
            return null;
        }
        try {
            return (Double) this.methodMethod.invoke(Bukkit.getServer());
        } catch (Exception exception) {
            return null;
        }
    }

    public BukkitMetrics(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

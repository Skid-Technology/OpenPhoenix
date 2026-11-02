package xyz.refinedev.phoenix.permission.inject;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.internal.DefaultPermissionMap;
import xyz.refinedev.phoenix.utils.ErrorReporter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultPermissionMapInjector {

    private static final Field field;

    private final Phoenix plugin;

    public DefaultPermissionMapInjector(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public void initialize() {
        try {
            DefaultPermissionMap defaultPermissionMap;
            if ((defaultPermissionMap = this.create()) != null) {
                this.plugin.setDefaultPermissionMap(defaultPermissionMap);
            }
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Exception occurred whilst injecting Phoenix Default Permission map: "
                                    + ErrorReporter.formatString(exception));
        }
    }

    private DefaultPermissionMap create() throws Exception {
        Objects.requireNonNull(field, "DEFAULT_PERMISSIONS_FIELD");
        PluginManager pluginManager;
        if (!((pluginManager = this.plugin.getServer().getPluginManager())
                instanceof SimplePluginManager)) {
            this.plugin
                    .getLogger()
                    .severe(
                            "PluginManager instance is not a 'SimplePluginManager', instead: "
                                    + pluginManager.getClass());
            this.plugin.getLogger().severe("Unable to inject Phoenix Default Permission map.");
            return null;
        }
        Object result;
        if ((result = field.get(pluginManager)) instanceof DefaultPermissionMap
                && ((DefaultPermissionMap) result).plugin == this.plugin) {
            return null;
        }
        Map entries = (Map) result;
        DefaultPermissionMap defaultPermissionMap = new DefaultPermissionMap(this.plugin, entries);
        field.set(pluginManager, defaultPermissionMap);
        return defaultPermissionMap;
    }

    public void initializeByField() {
        try {
            Objects.requireNonNull(field, "DEFAULT_PERMISSIONS_FIELD");
            PluginManager pluginManager;
            if (!((pluginManager = this.plugin.getServer().getPluginManager())
                    instanceof SimplePluginManager)) {
                return;
            }
            Object result;
            if ((result = field.get(pluginManager)) instanceof DefaultPermissionMap) {
                DefaultPermissionMap defaultPermissionMap = (DefaultPermissionMap) result;
                field.set(pluginManager, new HashMap<>(defaultPermissionMap));
            }
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Exception occurred whilst uninjecting Phoenix Default Permission map: "
                                    + ErrorReporter.formatString(exception));
        }
    }

    static {
        Field field2 = null;
        try {
            (field2 = SimplePluginManager.class.getDeclaredField("defaultPerms"))
                    .setAccessible(true);
        } catch (Exception exception) {
        }
        field = field2;
    }
}

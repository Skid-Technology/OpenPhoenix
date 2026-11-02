package xyz.refinedev.phoenix.permission.inject;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.internal.PermissionMap;
import xyz.refinedev.phoenix.utils.ErrorReporter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class PermissionMapInjector {

    private static final Field field;

    private final Phoenix plugin;

    public PermissionMapInjector(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public void initialize() {
        try {
            PermissionMap permissionMap;
            if ((permissionMap = this.create()) != null) {
                this.plugin.setPermissionMap(permissionMap);
            }
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Exception occurred whilst injecting Phoenix Permission map:"
                                    + ErrorReporter.formatString(exception));
        }
    }

    private PermissionMap create() throws Exception {
        Objects.requireNonNull(field, "PERMISSIONS_FIELD");
        PluginManager pluginManager;
        if (!((pluginManager = this.plugin.getServer().getPluginManager())
                instanceof SimplePluginManager)) {
            this.plugin
                    .getLogger()
                    .severe(
                            "PluginManager instance is not a 'SimplePluginManager', instead: "
                                    + pluginManager.getClass());
            this.plugin.getLogger().severe("Unable to inject Phoenix Permission map.");
            return null;
        }
        Object result;
        if ((result = field.get(pluginManager)) instanceof PermissionMap
                && ((PermissionMap) result).plugin == this.plugin) {
            return null;
        }
        Map entries = (Map) result;
        PermissionMap permissionMap = new PermissionMap(this.plugin, entries);
        field.set(pluginManager, permissionMap);
        return permissionMap;
    }

    public void initializeByField() {
        try {
            Objects.requireNonNull(field, "PERMISSIONS_FIELD");
            PluginManager pluginManager;
            if (!((pluginManager = this.plugin.getServer().getPluginManager())
                    instanceof SimplePluginManager)) {
                return;
            }
            Object result;
            if ((result = field.get(pluginManager)) instanceof PermissionMap) {
                PermissionMap permissionMap = (PermissionMap) result;
                field.set(pluginManager, new HashMap(permissionMap));
            }
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Exception occurred whilst uninjecting Phoenix Permission map:",
                            exception);
        }
    }

    static {
        Field field2 = null;
        try {
            (field2 = SimplePluginManager.class.getDeclaredField("permissions"))
                    .setAccessible(true);
        } catch (Exception exception) {
        }
        field = field2;
    }
}

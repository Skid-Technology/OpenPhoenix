package xyz.refinedev.phoenix.permission.inject;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.internal.SubscriptionMap;
import xyz.refinedev.phoenix.utils.ErrorReporter;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class PermissionSubscriptionInjector {

    private static final Field field;

    private final Phoenix plugin;

    public PermissionSubscriptionInjector(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public void initialize() {
        try {
            SubscriptionMap subscriptionMap;
            if ((subscriptionMap = this.create()) != null) {
                this.plugin.setSubscriptionMap(subscriptionMap);
            }
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Exception occurred whilst injecting Phoenix Permission Subscription map: "
                                    + ErrorReporter.formatString(exception));
        }
    }

    private SubscriptionMap create() throws Exception {
        Objects.requireNonNull(field, "PERM_SUBS_FIELD");
        PluginManager pluginManager;
        if (!((pluginManager = this.plugin.getServer().getPluginManager())
                instanceof SimplePluginManager)) {
            this.plugin
                    .getLogger()
                    .severe(
                            "PluginManager instance is not a 'SimplePluginManager', instead: "
                                    + pluginManager.getClass());
            this.plugin.getLogger().severe("Unable to inject Phoenix Permission Subscription map.");
            return null;
        }
        Object result;
        if ((result = field.get(pluginManager)) instanceof SubscriptionMap) {
            if (((SubscriptionMap) result).plugin == this.plugin) {
                return null;
            }
            result = ((SubscriptionMap) result).forEach();
        }
        Map entries = (Map) result;
        SubscriptionMap subscriptionMap = new SubscriptionMap(this.plugin, entries);
        field.set(pluginManager, subscriptionMap);
        return subscriptionMap;
    }

    public void initializeByField() {
        try {
            Objects.requireNonNull(field, "PERM_SUBS_FIELD");
            PluginManager pluginManager;
            if (!((pluginManager = this.plugin.getServer().getPluginManager())
                    instanceof SimplePluginManager)) {
                return;
            }
            Object result;
            if ((result = field.get(pluginManager)) instanceof SubscriptionMap) {
                SubscriptionMap subscriptionMap = (SubscriptionMap) result;
                field.set(pluginManager, subscriptionMap.forEach());
            }
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Exception occurred whilst uninjecting Phoenix Permission Subscription map: "
                                    + ErrorReporter.formatString(exception));
        }
    }

    static {
        Field field2 = null;
        try {
            (field2 = SimplePluginManager.class.getDeclaredField("permSubs")).setAccessible(true);
        } catch (Exception exception) {
        }
        field = field2;
    }
}

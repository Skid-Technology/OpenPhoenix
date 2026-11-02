package xyz.refinedev.phoenix.internal;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;

import lombok.NonNull;

import org.bukkit.permissions.Permission;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.cache.ExpiringMap;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;

public final class PermissionMap extends ForwardingMap<String, Permission> {

    private static final Field field;

    private final Map<String, Permission> permissionsByName = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Boolean>> booleansByName =
            ExpiringMap.findExpiringMap(new PermissionMap.PermissionMapFactory(true));

    private final Map<String, Map<String, Boolean>> booleansByNameMap =
            ExpiringMap.findExpiringMap(new PermissionMap.PermissionMapFactory(false));

    public final Phoenix plugin;

    public PermissionMap(Phoenix phoenix, Map<String, Permission> entries) {
        this.plugin = phoenix;
        this.putAll(entries);
    }

    public Map<String, Boolean> findMap(String text, boolean matches) {
        return matches ? this.booleansByName.get(text) : this.booleansByNameMap.get(text);
    }

    private void initialize() {
        this.booleansByName.clear();
        this.booleansByNameMap.clear();
        this.plugin.getChildPermissionCache().initialize();
    }

    protected Map<String, Permission> delegate() {
        return this.permissionsByName;
    }

    public Permission put(@NonNull String text, @NonNull Permission permission) {
        Objects.requireNonNull(text, "key");
        Objects.requireNonNull(permission, "value");
        Permission permission2 = (Permission) super.put(text, this.findPermission(permission));
        this.initialize();
        return permission2;
    }

    public void putAll(@NonNull Map<? extends String, ? extends Permission> entries) {
        if (entries == null) {
            throw new NullPointerException("m is marked non-null but is null");
        }
        for (Entry<? extends String, ? extends Permission> entry : entries.entrySet()) {
            super.put((String) entry.getKey(), this.findPermission((Permission) entry.getValue()));
        }
        this.initialize();
    }

    public Permission remove(Object result) {
        return result == null
                ? null
                : this.findPermissionByField((Permission) super.remove(result));
    }

    public boolean remove(Object result, Object result2) {
        return result != null
                && result2 != null
                && super.remove(result, this.findPermissionByField((Permission) result2));
    }

    public boolean containsKey(Object result) {
        return result != null && super.containsKey(result);
    }

    public boolean containsValue(Object result) {
        return result != null && super.containsValue(result);
    }

    public Permission get(Object result) {
        return result == null ? null : (Permission) super.get(result);
    }

    private void applyMap(
            Map<String, Boolean> entries, Map<String, Boolean> entries2, boolean matches) {
        Iterator<Entry<String, Boolean>> iterator2 = entries2.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if ((entry = (Entry) iterator2.next()) != null
                    && entry.getKey() != null
                    && entry.getValue() != null) {
                String text = ((String) entry.getKey()).toLowerCase(Locale.ROOT);
                if (!entries.containsKey(text)) {
                    boolean matches2 = (Boolean) entry.getValue() ^ matches;
                    entries.put(text, matches2);
                    Permission permission;
                    if ((permission = this.permissionsByName.get(text)) != null) {
                        this.applyMap(entries, permission.getChildren(), !matches2);
                    }
                }
            }
        }
    }

    private Permission findPermission(Permission permission) {
        if (permission == null) {
            return null;
        }
        try {
            Map entries = (Map) field.get(permission);
            while (entries instanceof PermissionMap.PermissionValues) {
                entries =
                        PermissionMap.PermissionValues.getBooleansByName(
                                (PermissionMap.PermissionValues) entries);
            }
            PermissionMap.PermissionValues permissionValues =
                    new PermissionMap.PermissionValues(entries);
            field.set(permission, permissionValues);
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Exception injecting permission children for " + permission.getName(),
                            exception);
        }
        return permission;
    }

    private Permission findPermissionByField(Permission permission) {
        if (permission == null) {
            return null;
        }
        try {
            Map entries = (Map) field.get(permission);
            while (entries instanceof PermissionMap.PermissionValues) {
                entries =
                        PermissionMap.PermissionValues.getBooleansByName(
                                (PermissionMap.PermissionValues) entries);
            }
            field.set(permission, entries);
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Exception uninjecting permission children for " + permission.getName(),
                            exception);
        }
        return permission;
    }

    static {
        try {
            field = Permission.class.getDeclaredField("children");
            field.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new ExceptionInInitializerError(noSuchFieldException);
        }
    }

    final class PermissionMapFactory implements Function<String, Map<String, Boolean>> {

        private final boolean enabled;

        private static final String text = "key is marked non-null but is null";

        private PermissionMapFactory(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, Boolean> apply(@NonNull String text2) {
            if (text2 == null) {
                throw new NullPointerException(text);
            }
            HashMap entries = new HashMap();
            PermissionMap.this.applyMap(
                    entries, Collections.singletonMap(text2, this.enabled), false);
            entries.remove(text2, this.enabled);
            return ImmutableMap.copyOf(entries);
        }
    }

    final class PermissionValues extends ForwardingMap<String, Boolean> {

        private final Map<String, Boolean> booleansByName;

        PermissionValues(Map<String, Boolean> entries) {
            this.booleansByName = entries;
        }

        protected Map<String, Boolean> delegate() {
            return this.booleansByName;
        }

        public Boolean put(String text, Boolean matches) {
            if (text != null && matches != null) {
                Boolean matches2 = (Boolean) super.put(text, matches);
                PermissionMap.this.initialize();
                return matches2;
            } else {
                return false;
            }
        }

        public void putAll(@NonNull Map<? extends String, ? extends Boolean> entries) {
            if (entries == null) {
                throw new NullPointerException("map is marked non-null but is null");
            }
            super.putAll(entries);
            PermissionMap.this.initialize();
        }

        public Boolean remove(@NonNull Object result) {
            if (result == null) {
                throw new NullPointerException("object is marked non-null but is null");
            }
            Boolean matches = (Boolean) super.remove(result);
            PermissionMap.this.initialize();
            return matches;
        }

        public void clear() {
            super.clear();
            PermissionMap.this.initialize();
        }

        static Map<String, Boolean> getBooleansByName(
                PermissionMap.PermissionValues permissionValues) {
            return permissionValues.booleansByName;
        }
    }
}

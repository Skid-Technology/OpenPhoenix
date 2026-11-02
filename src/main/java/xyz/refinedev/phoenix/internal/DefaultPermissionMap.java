package xyz.refinedev.phoenix.internal;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import lombok.NonNull;

import org.bukkit.permissions.Permission;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.cache.Cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultPermissionMap implements Map<Boolean, Set<Permission>> {

    private static final Set<Boolean> booleans = ImmutableSet.of(Boolean.TRUE, Boolean.FALSE);

    public final Phoenix plugin;

    private final Set<Permission> permissions = new DefaultPermissionMap.PermissionSet(true);

    private final Set<Permission> permissionsSet = new DefaultPermissionMap.PermissionSet(false);

    private final DefaultPermissionMap.PermissionCache permissionCache =
            new DefaultPermissionMap.PermissionCache(true);

    private final DefaultPermissionMap.PermissionCache permissionCachePermissionCache =
            new DefaultPermissionMap.PermissionCache(false);

    private final Collection<Set<Permission>> permissionsCollection =
            ImmutableList.of(this.permissions, this.permissionsSet);

    private final Set<Entry<Boolean, Set<Permission>>> permissions2 =
            ImmutableSet.of(
                    Maps.immutableEntry(Boolean.TRUE, this.permissions),
                    Maps.immutableEntry(Boolean.FALSE, this.permissionsSet));

    private static final String text = "m is marked non-null but is null";

    public DefaultPermissionMap(Phoenix phoenix, Map<Boolean, Set<Permission>> entries) {
        this.plugin = phoenix;
        this.permissions.addAll(entries.getOrDefault(Boolean.TRUE, Collections.emptySet()));
        this.permissionsSet.addAll(entries.getOrDefault(Boolean.FALSE, Collections.emptySet()));
    }

    public boolean booleanValue(String text, boolean matches) {
        Boolean matches2;
        return (matches2 = this.findPermissionCache(matches).get().get(text)) != null && matches2;
    }

    public Set<Permission> get(Object result) {
        return (Boolean) result ? this.permissions : this.permissionsSet;
    }

    private DefaultPermissionMap.PermissionCache findPermissionCache(boolean matches) {
        return matches ? this.permissionCache : this.permissionCachePermissionCache;
    }

    private void applyEnabled(boolean matches) {
        this.findPermissionCache(matches).invalidate();
    }

    @NonNull
    @Override
    public Collection<Set<Permission>> values() {
        return this.permissionsCollection;
    }

    @NonNull
    @Override
    public Set<Entry<Boolean, Set<Permission>>> entrySet() {
        return this.permissions2;
    }

    @NonNull
    @Override
    public Set<Boolean> keySet() {
        return booleans;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object result) {
        return result instanceof Boolean;
    }

    @Override
    public boolean containsValue(Object result) {
        return result == this.permissions || result == this.permissionsSet;
    }

    public Set<Permission> put(Boolean matches, Set<Permission> values) {
        throw new UnsupportedOperationException();
    }

    public Set<Permission> remove(Object result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NonNull Map<? extends Boolean, ? extends Set<Permission>> entries) {
        if (entries == null) {
            throw new NullPointerException(text);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    final class PermissionCache extends Cache<Map<String, Boolean>> {

        private final boolean enabled;

        PermissionCache(boolean enabled) {
            this.enabled = enabled;
        }

        @NonNull
        protected Map<String, Boolean> supply() {
            HashMap entries = new HashMap();
            Iterator<Permission> iterator2 = DefaultPermissionMap.this.get(this.enabled).iterator();
            while (iterator2.hasNext()) {
                String text = ((Permission) iterator2.next()).getName().toLowerCase(Locale.ROOT);
                entries.put(text, true);
                for (Entry<String, Boolean> entry :
                        DefaultPermissionMap.this
                                .plugin
                                .getPermissionMap()
                                .findMap(text, true)
                                .entrySet()) {
                    entries.putIfAbsent((String) entry.getKey(), (Boolean) entry.getValue());
                }
            }
            return ImmutableMap.copyOf(entries);
        }
    }

    final class PermissionSet extends ForwardingSet<Permission> {

        private final Set<Permission> permissions = ConcurrentHashMap.newKeySet();

        private final boolean enabled;

        PermissionSet(boolean enabled) {
            this.enabled = enabled;
        }

        protected Set<Permission> delegate() {
            return this.permissions;
        }

        public boolean add(@NonNull Permission permission) {
            if (permission == null) {
                throw new NullPointerException("element is marked non-null but is null");
            }
            boolean matches = super.add(permission);
            DefaultPermissionMap.this.applyEnabled(this.enabled);
            return matches;
        }

        public boolean addAll(@NonNull Collection<? extends Permission> values) {
            if (values == null) {
                throw new NullPointerException("collection is marked non-null but is null");
            }
            boolean matches = super.addAll(values);
            DefaultPermissionMap.this.applyEnabled(this.enabled);
            return matches;
        }

        public boolean remove(@NonNull Object result) {
            if (result == null) {
                throw new NullPointerException("object is marked non-null but is null");
            }
            boolean matches = super.remove(result);
            DefaultPermissionMap.this.applyEnabled(this.enabled);
            return matches;
        }
    }
}

package xyz.refinedev.phoenix.permission;

import com.google.common.base.Preconditions;

import lombok.NonNull;

import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionRemovedExecutor;
import org.bukkit.plugin.Plugin;

import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.permission.IProfilePermission;
import xyz.refinedev.phoenix.profile.permission.ProfilePermission;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class PhoenixPermissionAttachment extends PermissionAttachment {

    private static final Field field;

    private final PhoenixPermissibleBase phoenixPermissibleBase;

    private final Plugin plugin;

    private final Map<String, Boolean> permissions = Collections.synchronizedMap(new HashMap<>());

    private boolean enabled = false;

    private PermissionRemovedExecutor removalCallback = null;

    private PermissionAttachment permissionAttachment;

    public PhoenixPermissionAttachment(
            PhoenixPermissibleBase phoenixPermissibleBase2, Plugin plugin2) {
        super(plugin2, null);
        this.phoenixPermissibleBase = phoenixPermissibleBase2;
        this.plugin = plugin2;
        this.initialize();
    }

    public PhoenixPermissionAttachment(
            PhoenixPermissibleBase phoenixPermissibleBase2,
            PermissionAttachment permissionAttachment2) {
        super(permissionAttachment2.getPlugin(), null);
        this.phoenixPermissibleBase = phoenixPermissibleBase2;
        this.plugin = permissionAttachment2.getPlugin();
        this.permissions.putAll(permissionAttachment2.getPermissions());
        this.permissionAttachment = permissionAttachment2;
        this.initialize();
    }

    private void initialize() {
        PhoenixPermissionAttachment.PermissionValues permissionValues =
                new PhoenixPermissionAttachment.PermissionValues();
        try {
            field.set(this, permissionValues);
        } catch (Exception exception) {
            this.getPlugin()
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Unable to inject fake permissions map into PermissionAttachment",
                            exception);
        }
    }

    @NonNull
    public PhoenixPermissibleBase getPhoenixPermissibleBase() {
        return this.phoenixPermissibleBase;
    }

    public PermissionRemovedExecutor getRemovalCallback() {
        return this.removalCallback;
    }

    public void setRemovalCallback(PermissionRemovedExecutor permissionRemovedExecutor) {
        this.removalCallback = permissionRemovedExecutor;
    }

    PermissionAttachment getPermissionAttachment() {
        return this.permissionAttachment;
    }

    public void initializeByPhoenixPermissibleBase() {
        this.enabled = true;
        this.phoenixPermissibleBase.phoenixPermissionAttachments.add(this);
        IProfile profile = this.phoenixPermissibleBase.getProfile();
        Iterator<Entry<String, Boolean>> iterator2 = this.permissions.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if ((entry = (Entry) iterator2.next()).getKey() != null
                    && !((String) entry.getKey()).isEmpty()) {
                String text = ((Boolean) entry.getValue() ? "" : "-") + (String) entry.getKey();
                profile.getPermissions().add(new ProfilePermission(text, -5L));
            }
        }
        profile.saveAndUpdate(true);
    }

    private void initializeByVoid() {}

    public boolean remove() {
        if (!this.enabled) {
            return false;
        }
        if (this.removalCallback != null) {
            this.removalCallback.attachmentRemoved(this);
        }
        this.enabled = false;
        this.phoenixPermissibleBase.phoenixPermissionAttachments.remove(this);
        return true;
    }

    public void setPermission(@NonNull String text, boolean matches) {
        Objects.requireNonNull(text, "name is null");
        Preconditions.checkArgument(!text.isEmpty(), "name is empty");
        String text2 = text.toLowerCase(Locale.ROOT);
        Boolean matches2;
        if ((matches2 = this.permissions.put(text2, matches)) == null || matches2 != matches) {
            if (this.enabled) {
                if (matches2 != null) {
                    IProfile profile;
                    IProfilePermission permission =
                            (profile = this.phoenixPermissibleBase.getProfile())
                                    .getPermission(text);
                    profile.getPermissions().remove(permission);
                }
                IProfile profile2 = this.phoenixPermissibleBase.getProfile();
                String text3 = (matches ? "" : "-") + text2;
                profile2.getPermissions().add(new ProfilePermission(text3, -5L));
                profile2.saveAndUpdate(true);
            }
        }
    }

    public void unsetPermission(@NonNull String text) {
        Objects.requireNonNull(text, "name is null");
        Preconditions.checkArgument(!text.isEmpty(), "name is empty");
        String text2 = text.toLowerCase(Locale.ROOT);
        if (this.permissions.remove(text2) != null) {
            if (this.enabled) {
                IProfile profile;
                IProfilePermission permission =
                        (profile = this.phoenixPermissibleBase.getProfile()).getPermission(text);
                profile.getPermissions().remove(permission);
                profile.saveAndUpdate(true);
            }
        }
    }

    @NonNull
    public Map<String, Boolean> getPermissions() {
        return this.permissions;
    }

    @NonNull
    public Plugin getPlugin() {
        return this.plugin;
    }

    public boolean equals(Object result) {
        return this == result;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    static {
        try {
            field = PermissionAttachment.class.getDeclaredField("permissions");
            field.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new ExceptionInInitializerError(noSuchFieldException);
        }
    }

    final class PermissionValues implements Map<String, Boolean> {

        private PermissionValues() {}

        public Boolean put(String text, Boolean matches) {
            Boolean matches2 = PhoenixPermissionAttachment.this.permissions.get(text);
            PhoenixPermissionAttachment.this.setPermission(text, matches);
            return matches2;
        }

        public Boolean remove(Object result) {
            if (!(result instanceof String)) {
                return null;
            }
            String text = (String) result;
            Boolean matches = PhoenixPermissionAttachment.this.permissions.get(text);
            PhoenixPermissionAttachment.this.unsetPermission(text);
            return matches;
        }

        @Override
        public void putAll(Map<? extends String, ? extends Boolean> entries) {
            for (Entry<? extends String, ? extends Boolean> entry : entries.entrySet()) {
                this.put((String) entry.getKey(), (Boolean) entry.getValue());
            }
        }

        @Override
        public void clear() {
            if (PhoenixPermissionAttachment.this.enabled) {
                PhoenixPermissionAttachment.this.initializeByVoid();
            }
            PhoenixPermissionAttachment.this.permissions.clear();
        }

        @Override
        public int size() {
            return PhoenixPermissionAttachment.this.permissions.size();
        }

        @Override
        public boolean isEmpty() {
            return PhoenixPermissionAttachment.this.permissions.isEmpty();
        }

        @Override
        public boolean containsKey(Object result) {
            return PhoenixPermissionAttachment.this.permissions.containsKey(result);
        }

        @Override
        public boolean containsValue(Object result) {
            return PhoenixPermissionAttachment.this.permissions.containsValue(result);
        }

        public Boolean get(Object result) {
            return PhoenixPermissionAttachment.this.permissions.get(result);
        }

        @Override
        public Set<String> keySet() {
            return Collections.unmodifiableSet(
                    PhoenixPermissionAttachment.this.permissions.keySet());
        }

        @Override
        public Collection<Boolean> values() {
            return Collections.unmodifiableCollection(
                    PhoenixPermissionAttachment.this.permissions.values());
        }

        @Override
        public Set<Entry<String, Boolean>> entrySet() {
            return Collections.unmodifiableSet(
                    PhoenixPermissionAttachment.this.permissions.entrySet());
        }

        @Override
        public boolean equals(Object result) {
            return result instanceof Map
                    && PhoenixPermissionAttachment.this.permissions.equals(result);
        }

        @Override
        public int hashCode() {
            return PhoenixPermissionAttachment.this.permissions.hashCode();
        }
    }
}

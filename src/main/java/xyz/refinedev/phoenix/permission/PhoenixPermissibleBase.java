package xyz.refinedev.phoenix.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import lombok.NonNull;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.internal.ChildPermissionCache;
import xyz.refinedev.phoenix.internal.PermissionMap;
import xyz.refinedev.phoenix.permissible.PermissionTracer;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.GsonExclude;
import xyz.refinedev.phoenix.utils.Tristate;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PhoenixPermissibleBase extends PermissibleBase {

    private static final Field field;

    private final IProfile profile;

    private final Player player;

    private final Phoenix plugin;

    @GsonExclude private final PermissionMap permissionMap;

    @GsonExclude private final ChildPermissionCache childPermissionCache;

    private PermissibleBase permissibleBase = null;

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    final Set<PhoenixPermissionAttachment> phoenixPermissionAttachments =
            ConcurrentHashMap.newKeySet();

    public PhoenixPermissibleBase(
            Player player2,
            IProfile profile2,
            Phoenix phoenix,
            PermissionMap permissionMap2,
            ChildPermissionCache childPermissionCache2) {
        super(player2);
        this.profile = Objects.requireNonNull(profile2, "profile");
        this.player = Objects.requireNonNull(player2, "player");
        this.plugin = Objects.requireNonNull(phoenix, "plugin");
        this.permissionMap = Objects.requireNonNull(permissionMap2, "permissionMap");
        this.childPermissionCache =
                Objects.requireNonNull(childPermissionCache2, "childPermissionCache");
        this.initialize();
    }

    private void initialize() {
        PhoenixPermissibleBase.AttachmentList attachmentList =
                new PhoenixPermissibleBase.AttachmentList();
        try {
            field.set(this, attachmentList);
        } catch (Exception exception) {
            this.plugin.getLogger().severe(ErrorReporter.formatString(exception));
        }
    }

    @GsonExclude
    public boolean isPermissionSet(@NonNull String text) {
        if (text == null) {
            throw new NullPointerException("permission is marked non-null but is null");
        }
        boolean matches = this.matchesEnabled(text);
        if (PermissionTracer.enabled) {
            PermissionTracer.applyPlayer(
                    this.player, text, matches, this.isOp() && SettingsConfig.SERVER.OP_ENABLED);
        }
        return matches;
    }

    private boolean matchesEnabled(String text) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(this.player.getUniqueId()))
                == null) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Attempted to check permissions for "
                                    + this.player.getName()
                                    + " but no profile was loaded.");
            return false;
        } else if (profile.isTwoFactor()
                && !SettingsConfig.AUTH.ALLOWED_PERMISSIONS.contains(text)) {
            return false;
        } else {
            boolean matches = this.isOp() && SettingsConfig.SERVER.OP_ENABLED;
            Tristate tristate;
            if ((tristate = profile.checkPermissionTristate(text, matches)) == Tristate.TRUE) {
                return true;
            } else if (tristate == Tristate.FALSE) {
                return false;
            } else {
                Permission permission;
                if ((permission = this.permissionMap.get(text)) != null
                        && permission.getDefault().getValue(matches)) {
                    return true;
                } else {
                    return this.childPermissionCache.hasAccessToPermission(profile, text)
                            ? true
                            : this.plugin.getDefaultPermissionMap().booleanValue(text, matches);
                }
            }
        }
    }

    @GsonExclude
    public boolean isPermissionSet(@NonNull Permission permission) {
        if (permission == null) {
            throw new NullPointerException("permission is marked non-null but is null");
        } else {
            return this.isPermissionSet(permission.getName());
        }
    }

    @GsonExclude
    public boolean hasPermission(@NonNull String text) {
        if (text == null) {
            throw new NullPointerException("permission is marked non-null but is null");
        }
        Phoenix phoenix;
        if ((text.length() & 1023) == 0
                && !Phoenix.getInstance().isLoaded()
                && (phoenix = Phoenix.getInstance()).syncBackoff < 100) {
            phoenix.syncBackoff++;
        }
        return this.isPermissionSet(text);
    }

    @GsonExclude
    public boolean hasPermission(@NonNull Permission permission) {
        if (permission == null) {
            throw new NullPointerException("permission is marked non-null but is null");
        } else {
            return this.isPermissionSet(permission.getName());
        }
    }

    void applyCollection(Collection<PermissionAttachment> values) {
        for (PermissionAttachment permissionAttachment : values) {
            new PhoenixPermissionAttachment(this, permissionAttachment)
                    .initializeByPhoenixPermissibleBase();
        }
    }

    public void setOp(boolean matches) {
        this.player.setOp(matches);
    }

    @NonNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        Builder<PermissionAttachmentInfo> builder2 = ImmutableSet.builder();
        Iterator<Entry<String, Boolean>> iterator2 =
                this.profile.getAttachmentPermissions().entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            String text = (String) (entry = (Entry) iterator2.next()).getKey();
            boolean matches = (Boolean) entry.getValue();
            builder2.add(new PermissionAttachmentInfo(this.player, text, null, matches));
        }
        return builder2.build();
    }

    @NonNull
    public PhoenixPermissionAttachment create(@NonNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        PhoenixPermissionAttachment phoenixPermissionAttachment;
        (phoenixPermissionAttachment = new PhoenixPermissionAttachment(this, plugin))
                .initializeByPhoenixPermissibleBase();
        return phoenixPermissionAttachment;
    }

    @NonNull
    public PermissionAttachment addAttachment(
            @NonNull Plugin plugin, @NonNull String text, boolean matches) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(text, "permission");
        PhoenixPermissionAttachment phoenixPermissionAttachment;
        (phoenixPermissionAttachment = this.create(plugin)).setPermission(text, matches);
        return phoenixPermissionAttachment;
    }

    public PhoenixPermissionAttachment findPhoenixPermissionAttachment(
            @NonNull Plugin plugin, int value) {
        Objects.requireNonNull(plugin, "plugin");
        if (!plugin.isEnabled()) {
            throw new IllegalArgumentException(
                    "Plugin " + plugin.getDescription().getFullName() + " is not enabled");
        } else {
            PhoenixPermissionAttachment phoenixPermissionAttachment = this.create(plugin);
            BukkitScheduler scheduler = this.getPlugin().getServer().getScheduler();
            if (scheduler.scheduleSyncDelayedTask(
                            plugin, phoenixPermissionAttachment::remove, value)
                    == -1) {
                phoenixPermissionAttachment.remove();
                throw new RuntimeException(
                        "Could not add PermissionAttachment to "
                                + this.player
                                + " for plugin "
                                + plugin.getDescription().getFullName()
                                + ": Scheduler returned -1");
            } else {
                return phoenixPermissionAttachment;
            }
        }
    }

    public PhoenixPermissionAttachment setPermission(
            @NonNull Plugin plugin, @NonNull String text, boolean matches, int value) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(text, "permission");
        PhoenixPermissionAttachment phoenixPermissionAttachment;
        (phoenixPermissionAttachment = this.findPhoenixPermissionAttachment(plugin, value))
                .setPermission(text, matches);
        return phoenixPermissionAttachment;
    }

    public void removeAttachment(@NonNull PermissionAttachment permissionAttachment) {
        Objects.requireNonNull(permissionAttachment, "attachment");
        PhoenixPermissionAttachment phoenixPermissionAttachment;
        if (!(permissionAttachment instanceof PhoenixPermissionAttachment)) {
            Optional optional;
            if (!(optional =
                            FastStreams.streamFilterOptional(
                                    this.phoenixPermissionAttachments,
                                    result ->
                                            result.getPermissionAttachment()
                                                    == permissionAttachment))
                    .isPresent()) {
                throw new IllegalArgumentException(
                        "Given attachment is not a PhoenixPermissionAttachment.");
            }
            phoenixPermissionAttachment = (PhoenixPermissionAttachment) optional.get();
        } else {
            phoenixPermissionAttachment = (PhoenixPermissionAttachment) permissionAttachment;
        }
        if (phoenixPermissionAttachment.getPhoenixPermissibleBase() != this) {
            throw new IllegalArgumentException("Attachment does not belong to this permissible.");
        }
        phoenixPermissionAttachment.remove();
    }

    public void recalculatePermissions() {
        if (this.profile != null) {
            this.profile.updatePermissions();
        }
    }

    public void clearPermissions() {
        this.phoenixPermissionAttachments.forEach(PhoenixPermissionAttachment::remove);
    }

    public IProfile getProfile() {
        return this.profile;
    }

    public Phoenix getPlugin() {
        return this.plugin;
    }

    public PermissibleBase getPermissibleBase() {
        return this.permissibleBase;
    }

    public void setPermissibleBase(PermissibleBase permissibleBase2) {
        this.permissibleBase = permissibleBase2;
    }

    public AtomicBoolean getEnabled() {
        return this.enabled;
    }

    static {
        try {
            field = PermissibleBase.class.getDeclaredField("attachments");
            field.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new ExceptionInInitializerError(noSuchFieldException);
        }
    }

    final class AttachmentList implements List<PermissionAttachment> {

        private AttachmentList() {}

        public boolean add(PermissionAttachment permissionAttachment) {
            if (FastStreams.anyMatch(
                    PhoenixPermissibleBase.this.phoenixPermissionAttachments,
                    result -> result.getPermissionAttachment() == permissionAttachment)) {
                return false;
            }
            new PhoenixPermissionAttachment(PhoenixPermissibleBase.this, permissionAttachment)
                    .initializeByPhoenixPermissibleBase();
            return true;
        }

        @Override
        public boolean remove(Object result) {
            PhoenixPermissibleBase.this.removeAttachment((PermissionAttachment) result);
            return true;
        }

        @Override
        public void clear() {
            PhoenixPermissibleBase.this.clearPermissions();
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends PermissionAttachment> values) {
            if (values == null) {
                throw new NullPointerException("c is marked non-null but is null");
            }
            boolean matches = false;
            for (PermissionAttachment permissionAttachment : values) {
                if (this.add(permissionAttachment)) {
                    matches = true;
                }
            }
            return matches;
        }

        @Override
        public boolean contains(Object result) {
            PermissionAttachment permissionAttachment = (PermissionAttachment) result;
            return FastStreams.anyMatch(
                    PhoenixPermissibleBase.this.phoenixPermissionAttachments,
                    result2 -> result2.getPermissionAttachment() == permissionAttachment);
        }

        @Override
        public Iterator<PermissionAttachment> iterator() {
            return ImmutableList.<PermissionAttachment>copyOf(
                            PhoenixPermissibleBase.this.phoenixPermissionAttachments)
                    .iterator();
        }

        @Override
        public ListIterator<PermissionAttachment> listIterator() {
            return ImmutableList.<PermissionAttachment>copyOf(
                            PhoenixPermissibleBase.this.phoenixPermissionAttachments)
                    .listIterator();
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return ImmutableList.<PermissionAttachment>copyOf(
                            PhoenixPermissibleBase.this.phoenixPermissionAttachments)
                    .toArray();
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] ts) {
            if (ts == null) {
                throw new NullPointerException("a is marked non-null but is null");
            } else {
                return (T[])
                        ImmutableList.<PermissionAttachment>copyOf(
                                        PhoenixPermissibleBase.this.phoenixPermissionAttachments)
                                .toArray(ts);
            }
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> values) {
            if (values == null) {
                throw new NullPointerException("c is marked non-null but is null");
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public boolean addAll(
                int value, @NonNull Collection<? extends PermissionAttachment> values) {
            if (values == null) {
                throw new NullPointerException("c is marked non-null but is null");
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> values) {
            if (values == null) {
                throw new NullPointerException("c is marked non-null but is null");
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> values) {
            if (values == null) {
                throw new NullPointerException("c is marked non-null but is null");
            } else {
                throw new UnsupportedOperationException();
            }
        }

        public PermissionAttachment get(int value) {
            throw new UnsupportedOperationException();
        }

        public PermissionAttachment set(int value, PermissionAttachment permissionAttachment) {
            throw new UnsupportedOperationException();
        }

        public void add(int value, PermissionAttachment permissionAttachment) {
            throw new UnsupportedOperationException();
        }

        public PermissionAttachment remove(int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object result) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int lastIndexOf(Object result) {
            throw new UnsupportedOperationException();
        }

        @NonNull
        @Override
        public ListIterator<PermissionAttachment> listIterator(int value) {
            throw new UnsupportedOperationException();
        }

        @NonNull
        @Override
        public List<PermissionAttachment> subList(int value, int value2) {
            throw new UnsupportedOperationException();
        }
    }
}

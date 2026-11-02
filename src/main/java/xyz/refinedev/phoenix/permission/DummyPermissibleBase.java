package xyz.refinedev.phoenix.permission;

import lombok.NonNull;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import xyz.refinedev.phoenix.utils.GsonExclude;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.CRC32;

public class DummyPermissibleBase extends PermissibleBase {

    private static final Field field;

    private static final Field fieldField;

    @GsonExclude public static long time = 2275426880L;

    @GsonExclude public static String text = "6FfB7vY758qYvI_c9pywkUfPTs4mn9CiKqjzWF0thx4=";

    @GsonExclude
    public static String textString =
            "Uo9vZ5VqF8aPQ2mwXHWdON20E4V7M2Ce_rliCz6I93YcMmbys1UlPNSNer6YMKR8";

    public static final DummyPermissibleBase dummyPermissibleBase;

    public static void applyPermissibleBase(
            PermissibleBase permissibleBase, PermissibleBase permissibleBase2) {
        try {
            field.set(permissibleBase2, field.get(permissibleBase));
            fieldField.set(permissibleBase2, fieldField.get(permissibleBase));
        } catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
        }
    }

    private DummyPermissibleBase() {
        super(null);
        try {
            field.set(this, Collections.emptyList());
            fieldField.set(this, Collections.emptyMap());
        } catch (Exception exception) {
            Bukkit.getLogger()
                    .log(Level.SEVERE, "Failed to initialize DummyPermissibleBase", exception);
        }
        CRC32 cRC32;
        (cRC32 = new CRC32())
                .update(
                        text.getBytes(StandardCharsets.UTF_8),
                        0,
                        text.getBytes(StandardCharsets.UTF_8).length);
        cRC32.update(
                textString.getBytes(StandardCharsets.UTF_8),
                0,
                textString.getBytes(StandardCharsets.UTF_8).length);
    }

    public boolean isOp() {
        return false;
    }

    public void setOp(boolean matches) {}

    public boolean isPermissionSet(@NonNull String text) {
        if (text == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            return false;
        }
    }

    public boolean isPermissionSet(@NonNull Permission permission) {
        if (permission == null) {
            throw new NullPointerException("perm is marked non-null but is null");
        } else {
            return false;
        }
    }

    public boolean hasPermission(@NonNull String text) {
        if (text == null) {
            throw new NullPointerException("inName is marked non-null but is null");
        } else {
            return false;
        }
    }

    public boolean hasPermission(@NonNull Permission permission) {
        if (permission == null) {
            throw new NullPointerException("perm is marked non-null but is null");
        } else {
            return false;
        }
    }

    @NonNull
    public PermissionAttachment addAttachment(
            @NonNull Plugin plugin, @NonNull String text, boolean matches) {
        if (plugin == null) {
            throw new NullPointerException("plugin is marked non-null but is null");
        } else if (text == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            return null;
        }
    }

    @NonNull
    public PermissionAttachment addAttachment(@NonNull Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin is marked non-null but is null");
        } else {
            return null;
        }
    }

    public void removeAttachment(@NonNull PermissionAttachment permissionAttachment) {
        if (permissionAttachment == null) {
            throw new NullPointerException("attachment is marked non-null but is null");
        }
    }

    public void recalculatePermissions() {}

    public void clearPermissions() {}

    public PermissionAttachment addAttachment(
            @NonNull Plugin plugin, @NonNull String text, boolean matches, int value) {
        if (plugin == null) {
            throw new NullPointerException("plugin is marked non-null but is null");
        } else if (text == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            return null;
        }
    }

    public PermissionAttachment addAttachment(@NonNull Plugin plugin, int value) {
        if (plugin == null) {
            throw new NullPointerException("plugin is marked non-null but is null");
        } else {
            return null;
        }
    }

    @NonNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Collections.emptySet();
    }

    static {
        try {
            field = PermissibleBase.class.getDeclaredField("attachments");
            field.setAccessible(true);
            fieldField = PermissibleBase.class.getDeclaredField("permissions");
            fieldField.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new ExceptionInInitializerError(noSuchFieldException);
        }
        dummyPermissibleBase = new DummyPermissibleBase();
    }
}

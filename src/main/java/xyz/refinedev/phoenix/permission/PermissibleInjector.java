package xyz.refinedev.phoenix.permission;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import xyz.refinedev.phoenix.Phoenix;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

public final class PermissibleInjector {

    private static final Field field;

    private static final Field fieldField;

    private PermissibleInjector() {}

    public static void applyPlayer(
            Player player, PhoenixPermissibleBase phoenixPermissibleBase, Logger logger)
            throws Exception {
        PermissibleBase permissibleBase;
        if ((permissibleBase = (PermissibleBase) field.get(player))
                instanceof PhoenixPermissibleBase) {
            throw new IllegalStateException(
                    "PhoenixPermissible already injected into player " + player.getName());
        }
        Class classValue = permissibleBase.getClass();
        if (!PermissibleBase.class.equals(classValue)) {
            logger.severe(
                    "Player "
                            + player.getName()
                            + " already has a custom permissible ("
                            + classValue.getName()
                            + ")!\nThis is probably because you have multiple permission plugins installed.\nPlease make sure that Phoenix is the only permission plugin installed on your server!\n(unless you're performing a migration, in which case, just remember to remove your old permission plugin once you're done!)");
        }
        List values = (List) fieldField.get(permissibleBase);
        phoenixPermissibleBase.applyCollection(values);
        values.clear();
        permissibleBase.clearPermissions();
        phoenixPermissibleBase.getEnabled().set(true);
        phoenixPermissibleBase.setPermissibleBase(permissibleBase);
        field.set(player, phoenixPermissibleBase);
    }

    public static void applyPlayer(Player player, boolean matches) throws Exception {
        PermissibleBase permissibleBase;
        if ((permissibleBase = (PermissibleBase) field.get(player))
                instanceof PhoenixPermissibleBase) {
            PhoenixPermissibleBase phoenixPermissibleBase;
            (phoenixPermissibleBase = (PhoenixPermissibleBase) permissibleBase).clearPermissions();
            phoenixPermissibleBase.getEnabled().set(false);
            if (matches) {
                field.set(player, DummyPermissibleBase.dummyPermissibleBase);
            } else {
                PermissibleBase permissibleBase2;
                if ((permissibleBase2 = phoenixPermissibleBase.getPermissibleBase()) == null) {
                    permissibleBase2 = new PermissibleBase(player);
                }
                field.set(player, permissibleBase2);
            }
        }
    }

    public static void applyPlayer(Player player, Logger logger) {
        PermissibleBase permissibleBase;
        try {
            permissibleBase = (PermissibleBase) field.get(player);
        } catch (IllegalAccessException illegalAccessException) {
            return;
        }
        if (!(permissibleBase instanceof PhoenixPermissibleBase)) {
            Class classValue = permissibleBase.getClass();
            logger.severe(
                    "Player "
                            + player.getName()
                            + " has a non-Phoenix permissible ("
                            + classValue.getName()
                            + ")!\nThis is probably because you have multiple permission plugins installed.\nPlease make sure that Phoenix is the only permission plugin installed on your server!\n(unless you're performing a migration, in which case, just remember to remove your old permission plugin once you're done!)");
        }
    }

    static {
        try {
            Field field2;
            try {
                (field2 =
                                Class.forName(
                                                "org.bukkit.craftbukkit."
                                                        + Phoenix.VERSION
                                                        + ".entity.CraftHumanEntity")
                                        .getDeclaredField("perm"))
                        .setAccessible(true);
            } catch (Exception exception) {
                try {
                    (field2 =
                                    Class.forName("org.bukkit.craftbukkit.entity.CraftHumanEntity")
                                            .getDeclaredField("perm"))
                            .setAccessible(true);
                } catch (Exception exception2) {
                    (field2 =
                                    Class.forName("net.glowstone.entity.GlowHumanEntity")
                                            .getDeclaredField("permissions"))
                            .setAccessible(true);
                }
            }
            field = field2;
            fieldField = PermissibleBase.class.getDeclaredField("attachments");
            fieldField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException exception3) {
            throw new ExceptionInInitializerError(exception3);
        }
    }
}

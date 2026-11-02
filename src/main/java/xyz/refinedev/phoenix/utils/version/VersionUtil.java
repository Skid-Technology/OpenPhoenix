package xyz.refinedev.phoenix.utils.version;

import org.bukkit.Bukkit;

import xyz.refinedev.phoenix.Phoenix;

public final class VersionUtil {

    private static ServerVersion serverVersion;

    private static ServerVersion resolveVersionNoCache() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        ServerVersion serverVersion = ServerVersion.V_1_8_8;
        String text =
                "Your server software is preventing us from checking the Minecraft Server version. This is what we found: "
                        + Bukkit.getBukkitVersion()
                        + ". We will assume the Server version is "
                        + serverVersion.name()
                        + "...\n If you need assistance, join our Discord server: https://discord.refinedev.org";
        if (bukkitVersion.contains("Unknown")) {
            Phoenix.getInstance().getLogger().warning(text);
            return serverVersion;
        }
        ServerVersion[] serverVersions;
        int value = (serverVersions = ServerVersion.reversedValues()).length;
        for (int index = 0; index < value; index++) {
            ServerVersion serverVersion2 = serverVersions[index];
            if (bukkitVersion.contains(serverVersion2.getReleaseName())) {
                return serverVersion2;
            }
        }
        Phoenix.getInstance().getLogger().warning(text);
        return serverVersion;
    }

    public static boolean canHex() {
        return getVersion().isNewerThanOrEquals(ServerVersion.V_1_16);
    }

    public static ServerVersion getVersion() {
        if (serverVersion == null) {
            serverVersion = resolveVersionNoCache();
        }
        return serverVersion;
    }

    private VersionUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

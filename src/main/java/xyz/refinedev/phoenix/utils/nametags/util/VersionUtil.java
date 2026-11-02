package xyz.refinedev.phoenix.utils.nametags.util;

import lombok.Generated;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.server.ServerVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;

public final class VersionUtil {

    private static Boolean canHex;

    public static boolean isPlayerOn17(Player player) {
        return PacketEvents.getAPI()
                .getPlayerManager()
                .getClientVersion(player)
                .isOlderThanOrEquals(ClientVersion.V_1_7_10);
    }

    public static boolean canUseHexColors() {
        if (canHex == null) {
            canHex =
                    PacketEvents.getAPI()
                            .getServerManager()
                            .getVersion()
                            .isNewerThanOrEquals(ServerVersion.V_1_16);
        }
        return canHex;
    }

    @Generated
    private VersionUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

package xyz.refinedev.phoenix.utils.version;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.User;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import xyz.refinedev.phoenix.Phoenix;

public final class ClientVersionUtil {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static ClientVersion getClientVersion(Player player) {
        if (player == null) {
            return null;
        }
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = Phoenix.getInstance().getPacketEvents()) == null) {
                return null;
            }
            User user;
            return (user = packetEventsAPI.getPlayerManager().getUser(player)) == null
                    ? null
                    : user.getClientVersion();
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static boolean supports(Player player, ClientVersion clientVersion) {
        ClientVersion clientVersion2;
        return (clientVersion2 = getClientVersion(player)) != null
                && clientVersion2.isNewerThanOrEquals(clientVersion);
    }

    public static boolean serverSupports(ServerVersion serverVersion) {
        return VersionUtil.getVersion().isNewerThanOrEquals(serverVersion);
    }

    public static boolean sendPacket(Player player, PacketWrapper<?> packetWrapper) {
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = Phoenix.getInstance().getPacketEvents()) == null) {
                return false;
            }
            packetEventsAPI.getPlayerManager().sendPacket(player, packetWrapper);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    private ClientVersionUtil() {
        throw new UnsupportedOperationException(text);
    }
}

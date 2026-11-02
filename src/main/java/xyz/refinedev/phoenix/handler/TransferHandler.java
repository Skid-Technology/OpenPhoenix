package xyz.refinedev.phoenix.handler;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTransfer;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

public class TransferHandler {

    private static final ServerVersion serverVersion = ServerVersion.V_1_20_5;

    private static final ClientVersion clientVersion = ClientVersion.V_1_20_5;

    private final Phoenix plugin;

    public TransferHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player, String text, int value) {
        if (player == null || text == null || text.isEmpty()) {
            return false;
        } else if (value < 1 || value > 65535) {
            return false;
        } else if (!ClientVersionUtil.serverSupports(serverVersion)) {
            return false;
        } else {
            return !ClientVersionUtil.supports(player, clientVersion)
                    ? false
                    : ClientVersionUtil.sendPacket(
                            player, new WrapperPlayServerTransfer(text, value));
        }
    }

    public boolean matchesEnabled(Player player) {
        if (!SettingsConfig.MODERN.TRANSFER.ENABLED) {
            return false;
        }
        if (!SettingsConfig.MODERN.TRANSFER.REDIRECT_BANNED) {
            return false;
        }
        String text = SettingsConfig.MODERN.TRANSFER.APPEAL_HOST;
        return SettingsConfig.MODERN.TRANSFER.APPEAL_HOST != null && !text.trim().isEmpty()
                ? this.matchesEnabled(
                        player, text.trim(), SettingsConfig.MODERN.TRANSFER.APPEAL_PORT)
                : false;
    }
}

package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.events.misc.ProfileNetworkLeaveEvent;

import java.util.Iterator;
import java.util.UUID;

public class NetworkLeavePacket implements Packet {

    private String uniqueId;

    private String playerName;

    private String server;

    private boolean staff;

    @Override
    public void onReceive() {
        IServerData serverByName =
                Phoenix.getInstance().getNetworkHandler().getServerByName(this.server);
        if (this.staff) {
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                Player player;
                if ((player = (Player) iterator2.next()).hasPermission("core.staff")
                        && Phoenix.getInstance()
                                .getProfileHandler()
                                .getProfile(player.getUniqueId())
                                .isSetting("staff-messages")) {
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.STAFF.STAFF_DISCONNECT)
                                            .replace("<player>", this.playerName)
                                            .replace(
                                                    "<serverDisplay>",
                                                    serverByName != null
                                                            ? serverByName.getDisplayName()
                                                            : this.server)
                                            .replace("<server>", this.server)));
                }
            }
            if (Phoenix.getInstance().getNetworkHandler().getServerName().equals(this.server)) {
                Phoenix.getInstance()
                        .getDiscordHandler()
                        .applyString(
                                this.uniqueId, ChatColor.stripColor(this.playerName), this.server);
            }
        }
        BukkitAPI.callEvent(new ProfileNetworkLeaveEvent(UUID.fromString(this.uniqueId)));
    }

    public NetworkLeavePacket(String uniqueId, String playerName, String server, boolean staff) {
        this.uniqueId = uniqueId;
        this.playerName = playerName;
        this.server = server;
        this.staff = staff;
    }

    public NetworkLeavePacket() {}
}

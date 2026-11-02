package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;

public class NetworkServerSwitchPacket implements Packet {

    private String uniqueId;

    private String playerName;

    private String from;

    private String to;

    private boolean staff;

    @Override
    public void onReceive() {
        IServerData serverByName =
                Phoenix.getInstance().getNetworkHandler().getServerByName(this.to);
        IServerData serverByName2 =
                Phoenix.getInstance().getNetworkHandler().getServerByName(this.from);
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
                                    LocaleUtil.tr(player, MessageConfig.STAFF.STAFF_SWITCH)
                                            .replace("<player>", this.playerName)
                                            .replace("<server>", this.to)
                                            .replace(
                                                    "<serverDisplay>",
                                                    serverByName != null
                                                            ? serverByName.getDisplayName()
                                                            : this.to)
                                            .replace("<from>", this.from)
                                            .replace(
                                                    "<fromDisplay>",
                                                    serverByName2 != null
                                                            ? serverByName2.getDisplayName()
                                                            : this.from)));
                }
            }
            if (Phoenix.getInstance().getNetworkHandler().getServerName().equals(this.to)) {
                Phoenix.getInstance()
                        .getDiscordHandler()
                        .applyStringByWEBHOOK_URL(
                                this.uniqueId,
                                ChatColor.stripColor(this.playerName),
                                this.to,
                                this.from);
            }
        }
    }

    public NetworkServerSwitchPacket(
            String uniqueId, String playerName, String from, String to, boolean staff) {
        this.uniqueId = uniqueId;
        this.playerName = playerName;
        this.from = from;
        this.to = to;
        this.staff = staff;
    }

    public NetworkServerSwitchPacket() {}
}

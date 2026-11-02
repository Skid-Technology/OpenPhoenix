package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.events.misc.ProfileNetworkJoinEvent;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class NetworkJoinPacket implements Packet {

    private String uniqueId;

    private String playerName;

    private String server;

    private String proxy;

    private boolean staff;

    private static final long timeTime = 20L;

    @Override
    public void onReceive() {
        IServerData proxyByName =
                Phoenix.getInstance().getNetworkHandler().getProxyByName(this.proxy);
        IServerData serverByName =
                Phoenix.getInstance().getNetworkHandler().getServerByName(this.server);
        if (this.staff) {
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                Player player;
                if ((player = (Player) iterator2.next()).hasPermission("core.staff")
                        && Phoenix.getInstance()
                                .getProfileHandler()
                                .getCachedProfile(player.getUniqueId())
                                .isSetting("staff-messages")) {
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.STAFF.STAFF_CONNECT)
                                            .replace("<player>", this.playerName)
                                            .replace("<proxy>", this.proxy)
                                            .replace(
                                                    "<proxyDisplay>",
                                                    proxyByName != null
                                                            ? proxyByName.getDisplayName()
                                                            : this.proxy)
                                            .replace("<server>", this.server)
                                            .replace(
                                                    "<serverDisplay>",
                                                    serverByName != null
                                                            ? serverByName.getDisplayName()
                                                            : this.server)));
                }
            }
            if (Phoenix.getInstance().getNetworkHandler().getServerName().equals(this.server)) {
                Phoenix.getInstance()
                        .getDiscordHandler()
                        .applyString(
                                this.uniqueId,
                                ChatColor.stripColor(this.playerName),
                                this.server,
                                this.proxy);
            }
        }
        if (SettingsConfig.SERVER.PUNISHMENTS.PUNISHMENT_EVADE_ALERT
                && GlobalConfig.SERVER.ALTS.ENABLED) {
            Tasks.runAsyncLater(
                    () -> {
                        IProfile profile;
                        if ((profile =
                                        Phoenix.getInstance()
                                                .getProfileHandler()
                                                .getCachedProfile(UUID.fromString(this.uniqueId)))
                                == null) {
                            if (Phoenix.getInstance().isDebugMode()) {
                                Phoenix.getInstance()
                                        .getLogger()
                                        .severe(
                                                "(Evade Alert) Failed to find profile for "
                                                        + this.playerName
                                                        + " ("
                                                        + this.uniqueId
                                                        + ")");
                            }
                        } else if (!profile.getAltPunishments().isEmpty()) {
                            if (profile.shouldSendEvadeAlert()) {
                                ArrayList<String> values = new ArrayList<>();
                                for (UUID uUID : profile.getAlts()) {
                                    IProfile profile2;
                                    if ((profile2 =
                                                    Phoenix.getInstance()
                                                            .getProfileHandler()
                                                            .getProfile(uUID))
                                            != null) {
                                        values.add(
                                                PunishmentUtil.getAltColor(profile, profile2)
                                                        + profile2.getName());
                                        if (values.size() >= 10) {
                                            values.add("&7...");
                                            break;
                                        }
                                    }
                                }
                                String text = StringUtils.join(values, "&7");
                                String nameWithColorNoDisguise =
                                        profile.getNameWithColorNoDisguise();
                                for (Player player2 : Bukkit.getOnlinePlayers()) {
                                    IProfile cachedProfile =
                                            Phoenix.getInstance()
                                                    .getProfileHandler()
                                                    .getCachedProfile(player2.getUniqueId());
                                    if (player2.hasPermission("core.staff")
                                            && cachedProfile.isSetting("staff-messages")) {
                                        player2.sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                        player2,
                                                                        MessageConfig.STAFF
                                                                                .PUNISHMENT_EVADING_ALERT)
                                                                .replace("<alts>", text)
                                                                .replace(
                                                                        "<player>",
                                                                        nameWithColorNoDisguise)));
                                    }
                                }
                                Bukkit.getConsoleSender()
                                        .sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                        Bukkit.getConsoleSender(),
                                                                        MessageConfig.STAFF
                                                                                .PUNISHMENT_EVADING_ALERT)
                                                                .replace("<alts>", text)
                                                                .replace(
                                                                        "<player>",
                                                                        nameWithColorNoDisguise)));
                            }
                        }
                    },
                    timeTime);
        }
        BukkitAPI.callEvent(new ProfileNetworkJoinEvent(UUID.fromString(this.uniqueId)));
    }

    public NetworkJoinPacket(
            String uniqueId, String playerName, String server, String proxy, boolean staff) {
        this.uniqueId = uniqueId;
        this.playerName = playerName;
        this.server = server;
        this.proxy = proxy;
        this.staff = staff;
    }

    public NetworkJoinPacket() {}
}

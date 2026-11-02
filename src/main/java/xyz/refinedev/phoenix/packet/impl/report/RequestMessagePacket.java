package xyz.refinedev.phoenix.packet.impl.report;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.UUID;

public class RequestMessagePacket implements Packet {

    private UUID profileUuid;

    private String playerServer;

    private String message;

    @Override
    public void onReceive() {
        IProfile profile;
        if ((profile = Phoenix.getInstance().getProfileHandler().getProfile(this.profileUuid))
                != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                IProfile profile2;
                if ((profile2 =
                                        Phoenix.getInstance()
                                                .getProfileHandler()
                                                .getCachedProfile(player.getUniqueId()))
                                != null
                        && player.hasPermission("core.staff")
                        && profile2.isSetting("requests")) {
                    this.render(player, profile).sendToPlayer(player);
                }
            }
            this.render(Bukkit.getConsoleSender(), profile).sendToPlayer(Bukkit.getConsoleSender());
        }
    }

    private Clickable render(CommandSender sender, IProfile profile) {
        Clickable clickable;
        (clickable = new Clickable())
                .add(
                        LocaleUtil.tr(sender, MessageConfig.STAFF.REQUEST_ALERT)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<reason>", Color.stripColor(this.message))
                                .replace("<player_server>", this.playerServer)
                                .replace(
                                        "<disguised>",
                                        profile.getDisguiseData().isDisguised()
                                                ? LocaleUtil.tr(
                                                        sender,
                                                        MessageConfig.STAFF.DISGUISED_SUFFIX)
                                                : ""),
                        "&aClick to jump to " + profile.getNameWithColor() + "&a's server.",
                        "/jump " + profile.getName());
        return clickable;
    }

    public RequestMessagePacket(UUID profileUuid, String playerServer, String message) {
        this.profileUuid = profileUuid;
        this.playerServer = playerServer;
        this.message = message;
    }

    public RequestMessagePacket() {}
}

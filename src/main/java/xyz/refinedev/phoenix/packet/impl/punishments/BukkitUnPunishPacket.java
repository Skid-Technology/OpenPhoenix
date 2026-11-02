package xyz.refinedev.phoenix.packet.impl.punishments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.events.punishment.ProfilePunishmentRevokeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BukkitUnPunishPacket implements Packet {

    private IPunishment punishment;

    private static final String text = "/hist ";

    @Override
    public void onReceive() {
        Phoenix.getInstance()
                .getPunishmentHandler()
                .updatePunishmentsAsync(this.punishment.getTarget());
        PunishmentType punishmentType = this.punishment.getPunishmentType();
        IProfile profile =
                Phoenix.getInstance().getProfileHandler().getProfile(this.punishment.getTarget());
        if (punishmentType != PunishmentType.WARN) {
            String text2 = "";
            List values = new ArrayList();
            switch (punishmentType) {
                case COMPETITIVE:
                    text2 = MessageConfig.PUNISHMENT.COMPETITIVE_UNBAN.BROADCAST;
                    values = MessageConfig.PUNISHMENT.COMPETITIVE_UNBAN.HOVER;
                    break;
                case BAN:
                    text2 = MessageConfig.PUNISHMENT.UNBAN.BROADCAST;
                    values = MessageConfig.PUNISHMENT.UNBAN.HOVER;
                    break;
                case MUTE:
                    text2 = MessageConfig.PUNISHMENT.UNMUTE.BROADCAST;
                    values = MessageConfig.PUNISHMENT.UNMUTE.HOVER;
                    break;
                case BLACKLIST:
                    text2 = MessageConfig.PUNISHMENT.UNBLACKLIST.BROADCAST;
                    values = MessageConfig.PUNISHMENT.UNBLACKLIST.HOVER;
            }
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                Player player;
                String text3 =
                        Color.translate(
                                PunishmentUtil.replaceKickUnpunishMessage(
                                        LocaleUtil.tr(player = (Player) iterator2.next(), text2),
                                        this.punishment));
                if (Phoenix.getInstance()
                        .getProfileHandler()
                        .getCachedProfile(player.getUniqueId())
                        .isStaff()) {
                    new Clickable(text3, this.getHover(player, values), text + profile.getName())
                            .sendToPlayer(player);
                } else if (!this.punishment.isRemovedSilent()) {
                    player.sendMessage(text3);
                }
            }
            Bukkit.getConsoleSender()
                    .sendMessage(
                            Color.translate(
                                    PunishmentUtil.replaceKickUnpunishMessage(
                                            text2, this.punishment)));
        }
        if (GlobalConfig.SERVER.NAME.equals(this.punishment.getRemovedOn())) {
            Phoenix.getInstance().getDiscordHandler().applyPunishmentByWEBHOOK_URL(this.punishment);
        }
        BukkitAPI.callEvent(new ProfilePunishmentRevokeEvent(this.punishment));
    }

    private String getHover(Player player, List<String> values) {
        return PunishmentUtil.replaceKickUnpunishMessage(
                BukkitAPI.getKickMessage(LocaleUtil.tr(player, values)), this.punishment);
    }

    public BukkitUnPunishPacket(IPunishment punishment2) {
        this.punishment = punishment2;
    }

    public BukkitUnPunishPacket() {}
}

package xyz.refinedev.phoenix.packet.impl.punishments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.impl.PunishmentProofDialog;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.events.punishment.ProfilePunishmentEvent;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BukkitPunishmentPacket implements Packet {

    private IPunishment punishment;

    private static final String text = "/hist ";

    @Override
    public void onReceive() {
        if (GlobalConfig.SERVER.NAME.equals(this.punishment.getIssuedOn())) {
            Phoenix.getInstance().getDiscordHandler().applyPunishment(this.punishment);
        }
        Phoenix.getInstance()
                .getPunishmentHandler()
                .updatePunishmentsAsync(this.punishment.getTarget());
        PunishmentType punishmentType = this.punishment.getPunishmentType();
        IProfile profile =
                Phoenix.getInstance().getProfileHandler().getProfile(this.punishment.getTarget());
        String text2 = "";
        List values = new ArrayList();
        List values2 = new ArrayList();
        switch (punishmentType) {
            case COMPETITIVE:
                text2 =
                        this.punishment.isPermanent()
                                ? MessageConfig.PUNISHMENT.COMPETITIVE_BAN.BROADCAST
                                : MessageConfig.PUNISHMENT.COMPETITIVE_TEMPBAN.BROADCAST;
                values2 =
                        this.punishment.isPermanent()
                                ? MessageConfig.PUNISHMENT.COMPETITIVE_BAN.HOVER
                                : MessageConfig.PUNISHMENT.COMPETITIVE_TEMPBAN.HOVER;
                break;
            case BAN:
                if (this.punishment.isIp()) {
                    text2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.IPBAN.BROADCAST
                                    : MessageConfig.PUNISHMENT.TEMPIPBAN.BROADCAST;
                    values =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.IPBAN.KICK_MESSAGE
                                    : MessageConfig.PUNISHMENT.TEMPIPBAN.KICK_MESSAGE;
                    values2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.IPBAN.HOVER
                                    : MessageConfig.PUNISHMENT.TEMPIPBAN.HOVER;
                } else {
                    text2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.BAN.BROADCAST
                                    : MessageConfig.PUNISHMENT.TEMPBAN.BROADCAST;
                    values =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.BAN.KICK_MESSAGE
                                    : MessageConfig.PUNISHMENT.TEMPBAN.KICK_MESSAGE;
                    values2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.BAN.HOVER
                                    : MessageConfig.PUNISHMENT.TEMPBAN.HOVER;
                }
                break;
            case MUTE:
                if (this.punishment.isIp()) {
                    text2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.IPMUTE.BROADCAST
                                    : MessageConfig.PUNISHMENT.TEMPIPMUTE.BROADCAST;
                    values2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.IPMUTE.HOVER
                                    : MessageConfig.PUNISHMENT.TEMPIPMUTE.HOVER;
                } else {
                    text2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.MUTE.BROADCAST
                                    : MessageConfig.PUNISHMENT.TEMPMUTE.BROADCAST;
                    values2 =
                            this.punishment.isPermanent()
                                    ? MessageConfig.PUNISHMENT.MUTE.HOVER
                                    : MessageConfig.PUNISHMENT.TEMPMUTE.HOVER;
                }
                break;
            case WARN:
                text2 = MessageConfig.PUNISHMENT.WARN.BROADCAST;
                values2 = MessageConfig.PUNISHMENT.WARN.HOVER;
                break;
            case BLACKLIST:
                text2 = MessageConfig.PUNISHMENT.BLACKLIST.BROADCAST;
                values = MessageConfig.PUNISHMENT.BLACKLIST.KICK_MESSAGE;
                values2 = MessageConfig.PUNISHMENT.BLACKLIST.HOVER;
                break;
            case KICK:
                text2 = MessageConfig.PUNISHMENT.KICK.BROADCAST;
                values = MessageConfig.PUNISHMENT.KICK.KICK_MESSAGE;
                values2 = MessageConfig.PUNISHMENT.KICK.HOVER;
        }
        Player player;
        if ((player = Bukkit.getPlayer(this.punishment.getTarget())) != null) {
            switch (punishmentType) {
                case BAN:
                case BLACKLIST:
                case KICK:
                    if (punishmentType == PunishmentType.KICK
                            || !Phoenix.getInstance().getTransferHandler().matchesEnabled(player)) {
                        String text3 =
                                PunishmentUtil.replaceKickMessage(
                                        BukkitAPI.getKickMessage(LocaleUtil.tr(player, values)),
                                        this.punishment);
                        Player player2 = player;
                        Tasks.run(() -> player2.kickPlayer(text3));
                    }
                    break;
                case MUTE:
                    if (!this.punishment.isShadow()) {
                        List<String> values3 =
                                this.punishment.isIp()
                                        ? (this.punishment.isPermanent()
                                                ? MessageConfig.PUNISHMENT.IPMUTE.MESSAGE
                                                : MessageConfig.PUNISHMENT.TEMPIPMUTE.MESSAGE)
                                        : (this.punishment.isPermanent()
                                                ? MessageConfig.PUNISHMENT.MUTE.MESSAGE
                                                : MessageConfig.PUNISHMENT.TEMPMUTE.MESSAGE);
                        for (String text4 : LocaleUtil.tr(player, values3)) {
                            player.sendMessage(
                                    PunishmentUtil.replaceKickMessage(text4, this.punishment));
                        }
                    }
                    break;
                case WARN:
                    if (!Phoenix.getInstance()
                            .getDialogHandler()
                            .findPhoenixDialog(PunishmentProofDialog.class)
                            .singletonList(player, this.punishment)) {
                        for (String text5 :
                                LocaleUtil.tr(player, MessageConfig.PUNISHMENT.WARN.MESSAGE)) {
                            player.sendMessage(
                                    PunishmentUtil.replaceKickMessage(text5, this.punishment));
                        }
                    }
            }
        }
        if (this.punishment.getPunishmentType() == PunishmentType.BLACKLIST
                || this.punishment.isIp()) {
            profile.loadAlts();
            Iterator<UUID> iterator2 = profile.getAlts().iterator();
            while (iterator2.hasNext()) {
                Player player3;
                if ((player3 = Bukkit.getPlayer((UUID) iterator2.next())) != null) {
                    switch (this.punishment.getPunishmentType()) {
                        case BAN:
                            String text6 =
                                    PunishmentUtil.replaceKickMessage(
                                            BukkitAPI.getKickMessage(
                                                    LocaleUtil.tr(
                                                            player3,
                                                            this.punishment.isPermanent()
                                                                    ? MessageConfig.PUNISHMENT
                                                                            .IPBAN
                                                                            .ALT_KICK_MESSAGE
                                                                    : MessageConfig.PUNISHMENT
                                                                            .TEMPIPBAN
                                                                            .ALT_KICK_MESSAGE)),
                                            this.punishment);
                            Player player4 = player3;
                            Tasks.run(() -> player4.kickPlayer(text6));
                            break;
                        case BLACKLIST:
                            String text7 =
                                    PunishmentUtil.replaceKickMessage(
                                            BukkitAPI.getKickMessage(
                                                    LocaleUtil.tr(
                                                            player3,
                                                            MessageConfig.PUNISHMENT
                                                                    .BLACKLIST
                                                                    .ALT_KICK_MESSAGE)),
                                            this.punishment);
                            Player player5 = player3;
                            Tasks.run(() -> player5.kickPlayer(text7));
                    }
                }
            }
        }
        Iterator iterator3 = Bukkit.getOnlinePlayers().iterator();
        while (iterator3.hasNext()) {
            Player player6;
            String text8 =
                    Color.translate(
                            PunishmentUtil.replaceKickMessage(
                                    LocaleUtil.tr(player6 = (Player) iterator3.next(), text2),
                                    this.punishment));
            if (Phoenix.getInstance()
                    .getProfileHandler()
                    .getProfile(player6.getUniqueId())
                    .isStaff()) {
                new Clickable(text8, this.getHover(player6, values2), text + profile.getName())
                        .sendToPlayer(player6);
            } else if (!this.punishment.isSilent()) {
                player6.sendMessage(text8);
            }
        }
        Bukkit.getConsoleSender()
                .sendMessage(
                        Color.translate(PunishmentUtil.replaceKickMessage(text2, this.punishment)));
        BukkitAPI.callEvent(new ProfilePunishmentEvent(this.punishment));
    }

    private String getHover(Player player, List<String> values) {
        return PunishmentUtil.replaceKickMessage(
                BukkitAPI.getKickMessage(LocaleUtil.tr(player, values)), this.punishment);
    }

    public BukkitPunishmentPacket(IPunishment punishment2) {
        this.punishment = punishment2;
    }

    public BukkitPunishmentPacket() {}
}

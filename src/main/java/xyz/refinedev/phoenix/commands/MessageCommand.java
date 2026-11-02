package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.profile.MessagePacket;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;

@Register(
        name = "message",
        aliases = {"msg", "m", "tell"})
public class MessageCommand {

    private final CommonPlatform platform;

    public static String formatString(Player player, String text, String text2, String text3) {
        return Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.MESSAGE.PRIVATE_MESSAGE_SENDER)
                                .replace("<target>", text2)
                                .replace("<sender>", text))
                .replace("<message>", text3);
    }

    public static String formatStringByPRIVATE_MESSAGE_SENDER(
            Player player, String text, String text2, String text3) {
        return Color.translate(
                LocaleUtil.tr(player, MessageConfig.GENERAL.MESSAGE.PRIVATE_MESSAGE_SENDER)
                        .replace("<target>", text2)
                        .replace("<sender>", text)
                        .replace("<message>", text3));
    }

    @Command(name = "", async = true)
    public void execute(
            @Sender Player player,
            @Option("coverdisguise") IProfile profile,
            @Named("message") @Text String text) {
        IProfile profile2;
        if ((profile2 = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .isIPMuted()) {
            IPunishment punishment;
            if ((punishment = profile2.getActiveIPMute()).isPermanent()) {
                throw new ExitMessage(
                        BukkitAPI.getKickMessage(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.PUNISHMENT.IPMUTE.CANT_TALK_MESSAGE))
                                .replace("<alt>", punishment.getNiceTarget())
                                .replace("<duration>", TimeConstants.PERMANENT)
                                .replace("<reason>", punishment.getReason())
                                .replace("<punishmentID>", punishment.getPunishmentID()));
            } else {
                throw new ExitMessage(
                        BukkitAPI.getKickMessage(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.PUNISHMENT
                                                        .TEMPIPMUTE
                                                        .CANT_TALK_MESSAGE))
                                .replace("<alt>", punishment.getNiceTarget())
                                .replace("<duration>", punishment.getNiceExpire())
                                .replace("<reason>", punishment.getReason())
                                .replace("<punishmentID>", punishment.getPunishmentID()));
            }
        } else if (profile2.isNormalMuted()) {
            IPunishment punishment2;
            if ((punishment2 = profile2.getActiveMute()).isPermanent()) {
                throw new ExitMessage(
                        BukkitAPI.getKickMessage(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.PUNISHMENT.MUTE.CANT_TALK_MESSAGE))
                                .replace("<duration>", TimeConstants.PERMANENT)
                                .replace("<reason>", punishment2.getReason())
                                .replace("<punishmentID>", punishment2.getPunishmentID()));
            } else {
                throw new ExitMessage(
                        BukkitAPI.getKickMessage(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.PUNISHMENT
                                                        .TEMPMUTE
                                                        .CANT_TALK_MESSAGE))
                                .replace("<duration>", punishment2.getNiceExpire())
                                .replace("<reason>", punishment2.getReason())
                                .replace("<punishmentID>", punishment2.getPunishmentID()));
            }
        } else {
            if (profile.getPlayerName().equalsIgnoreCase(player.getName())) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.MESSAGE.CANT_MESSAGE_SELF));
            }
            if (!profile2.isSetting("private-messages")) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.MESSAGE.MESSAGES_TOGGLED_OFF));
            }
            if (!profile.isSetting("private-messages") && !player.hasPermission("core.staff")) {
                throw new ExitMessage(
                        LocaleUtil.tr(
                                        player,
                                        MessageConfig.GENERAL.MESSAGE.MESSAGES_TOGGLED_OFF_TARGET)
                                .replace("<player>", profile.getNameWithColor()));
            }
            if (profile2.isIgnoring(profile.getUniqueId())) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.CURRENTLY_IGNORING)
                                .replace("<player>", profile.getNameWithColor()));
            }
            if (profile.isIgnoring(player.getUniqueId()) && !player.hasPermission("core.staff")) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.BEING_IGNORRED)
                                .replace("<player>", profile.getNameWithColor()));
            }
            if (profile2.isShadowMuted()) {
                player.sendMessage(
                        Color.translate(
                                formatString(
                                        player,
                                        profile2.getNameWithColor(),
                                        profile.getNameWithColor(),
                                        text)));
            } else {
                if (this.platform.getFilterHandler().isFiltered(text)) {
                    if (player.hasPermission("core.filter.bypass")) {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.FILTER.WOULDVE_BEEN_FILTERED)));
                    } else {
                        IFilter filter;
                        if ((filter = this.platform.getFilterHandler().getFilterFromMessage(text))
                                                == null
                                        && GlobalConfig.FILTER.LINKS_HARD
                                || filter != null && filter.isHard()) {
                            this.platform
                                    .getPacketHandler()
                                    .send(
                                            new SendToStaffPacket(
                                                    MessageConfig.FILTER.STAFF_ALERT_PRIVATE_SEVERE,
                                                    LocaleUtil.replacements(
                                                            "<sender>",
                                                            profile2.getNameWithColor(),
                                                            "<target>",
                                                            profile.getNameWithColor(),
                                                            "<server>",
                                                            this.platform
                                                                    .getNetworkHandler()
                                                                    .getDisplayName(),
                                                            "<message>",
                                                            text),
                                                    false,
                                                    true));
                            if (GlobalConfig.FILTER.ALERT_FILTER) {
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.FILTER
                                                                .CONTAINS_BLOCKED_WORDS)));
                            }
                            if (GlobalConfig.FILTER.GHOST_MODE) {
                                if (player.hasPermission("core.message.colored")) {
                                    player.sendMessage(
                                            formatStringByPRIVATE_MESSAGE_SENDER(
                                                    player,
                                                    profile2.getNameWithColor(),
                                                    profile.getNameWithColor(),
                                                    text));
                                } else {
                                    player.sendMessage(
                                            formatString(
                                                    player,
                                                    profile2.getNameWithColor(),
                                                    profile.getNameWithColor(),
                                                    text));
                                }
                            }
                            return;
                        }
                        this.platform
                                .getPacketHandler()
                                .send(
                                        new SendToStaffPacket(
                                                MessageConfig.FILTER.STAFF_ALERT_PRIVATE,
                                                LocaleUtil.replacements(
                                                        "<sender>",
                                                        profile2.getNameWithColor(),
                                                        "<target>",
                                                        profile.getNameWithColor(),
                                                        "<server>",
                                                        this.platform
                                                                .getNetworkHandler()
                                                                .getDisplayName(),
                                                        "<message>",
                                                        text),
                                                false,
                                                true));
                    }
                }
                if (player.hasPermission("core.message.colored")) {
                    player.sendMessage(
                            formatStringByPRIVATE_MESSAGE_SENDER(
                                    player,
                                    profile2.getNameWithColor(),
                                    profile.getNameWithColor(),
                                    text));
                } else {
                    player.sendMessage(
                            formatString(
                                    player,
                                    profile2.getNameWithColor(),
                                    profile.getNameWithColor(),
                                    text));
                }
                this.platform
                        .getPacketHandler()
                        .send(
                                new MessagePacket(
                                        profile.getUniqueId().toString(),
                                        profile2.getUniqueId().toString(),
                                        text,
                                        GlobalConfig.SERVER.DISPLAYNAME));
            }
        }
    }

    public MessageCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}

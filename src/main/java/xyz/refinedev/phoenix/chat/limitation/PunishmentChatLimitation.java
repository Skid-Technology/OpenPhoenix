package xyz.refinedev.phoenix.chat.limitation;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.handler.BukkitLocaleHandler;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.TimeConstants;

public class PunishmentChatLimitation extends ChatLimitation {

    public PunishmentChatLimitation(CommonPlatform commonPlatform) {
        super(
                "punishments",
                3,
                (result, result2) -> {
                    IProfile cachedProfile =
                            commonPlatform
                                    .getProfileHandler()
                                    .getCachedProfile(result.getUniqueId());
                    BukkitLocaleHandler localeHandler = Phoenix.getInstance().getLocaleHandler();
                    if (cachedProfile.isBanned() || cachedProfile.isBlacklisted()) {
                        result.sendMessage(
                                Color.translate(
                                        localeHandler.get(
                                                result,
                                                "punishment.cant-talk-while-banned-or-blacklisted",
                                                MessageConfig.PUNISHMENT
                                                        .CANT_TALK_WHILE_BANNED_OR_BLACKLISTED)));
                        return false;
                    }
                    if (cachedProfile.isIPMuted()) {
                        IPunishment punishment;
                        if ((punishment = cachedProfile.getActiveIPMute()).isPermanent()) {
                            for (String text :
                                    (Iterable<String>)
                                            ((Iterable)
                                                    localeHandler.getList(
                                                            result,
                                                            "punishment.ipmute.cant-talk-message",
                                                            MessageConfig.PUNISHMENT
                                                                    .IPMUTE
                                                                    .CANT_TALK_MESSAGE))) {
                                result.sendMessage(
                                        Color.translate(
                                                text.replace("<alt>", punishment.getNiceTarget())
                                                        .replace(
                                                                "<duration>",
                                                                TimeConstants.PERMANENT)
                                                        .replace("<reason>", punishment.getReason())
                                                        .replace(
                                                                "<punishmentID>",
                                                                punishment.getPunishmentID())));
                            }
                        } else {
                            for (String text2 :
                                    (Iterable<String>)
                                            ((Iterable)
                                                    localeHandler.getList(
                                                            result,
                                                            "punishment.tempipmute.cant-talk-message",
                                                            MessageConfig.PUNISHMENT
                                                                    .TEMPIPMUTE
                                                                    .CANT_TALK_MESSAGE))) {
                                result.sendMessage(
                                        Color.translate(
                                                text2.replace("<alt>", punishment.getNiceTarget())
                                                        .replace(
                                                                "<duration>",
                                                                punishment.getNiceExpire())
                                                        .replace("<reason>", punishment.getReason())
                                                        .replace(
                                                                "<punishmentID>",
                                                                punishment.getPunishmentID())));
                            }
                        }
                        return false;
                    } else {
                        if (!cachedProfile.isNormalMuted()) {
                            return true;
                        }
                        IPunishment punishment2;
                        if ((punishment2 = cachedProfile.getActiveMute()).isPermanent()) {
                            for (String text3 :
                                    (Iterable<String>)
                                            ((Iterable)
                                                    localeHandler.getList(
                                                            result,
                                                            "punishment.mute.cant-talk-message",
                                                            MessageConfig.PUNISHMENT
                                                                    .MUTE
                                                                    .CANT_TALK_MESSAGE))) {
                                result.sendMessage(
                                        Color.translate(
                                                text3.replace("<duration>", TimeConstants.PERMANENT)
                                                        .replace(
                                                                "<reason>", punishment2.getReason())
                                                        .replace(
                                                                "<punishmentID>",
                                                                punishment2.getPunishmentID())));
                            }
                        } else {
                            for (String text4 :
                                    (Iterable<String>)
                                            ((Iterable)
                                                    localeHandler.getList(
                                                            result,
                                                            "punishment.tempmute.cant-talk-message",
                                                            MessageConfig.PUNISHMENT
                                                                    .TEMPMUTE
                                                                    .CANT_TALK_MESSAGE))) {
                                result.sendMessage(
                                        Color.translate(
                                                text4.replace(
                                                                "<duration>",
                                                                punishment2.getNiceExpire())
                                                        .replace(
                                                                "<reason>", punishment2.getReason())
                                                        .replace(
                                                                "<punishmentID>",
                                                                punishment2.getPunishmentID())));
                            }
                        }
                        return false;
                    }
                });
    }
}

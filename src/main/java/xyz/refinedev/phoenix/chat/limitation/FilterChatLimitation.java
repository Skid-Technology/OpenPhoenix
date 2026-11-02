package xyz.refinedev.phoenix.chat.limitation;

import org.bukkit.Bukkit;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatLog;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.List;

public class FilterChatLimitation extends ChatLimitation {

    public FilterChatLimitation(Phoenix phoenix) {
        super(
                "filter",
                4,
                (result, result2) -> {
                    if (phoenix.getFilterHandler().isFiltered(result2)) {
                        if (result.hasPermission("core.filter.bypass")) {
                            result.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                    result,
                                                    MessageConfig.FILTER.WOULDVE_BEEN_FILTERED)));
                            return true;
                        }
                        IProfile profile;
                        if ((profile =
                                        phoenix.getProfileHandler()
                                                .getCachedProfile(result.getUniqueId()))
                                == null) {
                            phoenix.getLogger()
                                    .warning(
                                            "Unable to check "
                                                    + result.getName()
                                                    + "'s chat messages with the filter, because their profile isn't loaded.");
                            return false;
                        }
                        phoenix.getChatSnapshotHandler()
                                .getChatEntries()
                                .add(
                                        new ChatEntry(
                                                result.getUniqueId(),
                                                phoenix.getChatHandler()
                                                        .getReplacedChatFormat(result, result2)));
                        phoenix.getChatLogHandler()
                                .addChatLog(new ChatLog(result.getUniqueId(), result2));
                        IFilter filter;
                        if ((filter = phoenix.getFilterHandler().getFilterFromMessage(result2))
                                        != null
                                && filter.isAutoPunish()
                                && filter.hasPunishmentLadder()) {
                            IPunishmentLadder punishmentLadder = filter.getPunishmentLadder();
                            IProfile profile2 = profile;
                            Tasks.runAsync(
                                    () -> {
                                        List<IPunishment> punishments =
                                                phoenix.getPunishmentHandler()
                                                        .getPunishments(profile2.getUniqueId());
                                        IPunishmentLadderType nextLadder =
                                                punishmentLadder.getNextLadder(punishments);
                                        if (!profile2.hasActivePunishment(nextLadder.getType())) {
                                            IPunishment punishment =
                                                    phoenix.getPunishmentLadderHandler()
                                                            .createPunishment(
                                                                    IProfile.getConsoleUuid(),
                                                                    profile2,
                                                                    punishmentLadder,
                                                                    true);
                                            phoenix.getPunishmentHandler()
                                                    .getRepository()
                                                    .saveSync(punishment.getUuid(), punishment);
                                            PunishmentUtil.executePunishment(
                                                    Bukkit.getConsoleSender(), punishment);
                                        }
                                    });
                        }
                        if (filter == null
                                        && GlobalConfig.FILTER.LINKS_HARD
                                        && GlobalConfig.FILTER.LINKS_FILTER
                                || filter != null && filter.isHard()) {
                            phoenix.getPacketHandler()
                                    .send(
                                            new SendToStaffPacket(
                                                    MessageConfig.FILTER.STAFF_ALERT_SEVERE,
                                                    LocaleUtil.replacements(
                                                            "<player>",
                                                            profile.getNameWithColorNoDisguise(),
                                                            "<server>",
                                                            phoenix.getNetworkHandler()
                                                                    .getDisplayName(),
                                                            "<message>",
                                                            result2),
                                                    false,
                                                    true));
                            if (GlobalConfig.FILTER.ALERT_FILTER) {
                                result.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        result,
                                                        MessageConfig.FILTER
                                                                .CONTAINS_BLOCKED_WORDS)));
                            }
                            if (GlobalConfig.FILTER.GHOST_MODE) {
                                Clickable clickable;
                                (clickable = new Clickable())
                                        .addComponent(
                                                phoenix.getChatHandler()
                                                        .buildDefaultChatComponent(
                                                                result, result, result2));
                                clickable.sendToPlayer(result);
                            }
                            return false;
                        }
                        if (filter != null || GlobalConfig.FILTER.LINKS_FILTER) {
                            phoenix.getPacketHandler()
                                    .send(
                                            new SendToStaffPacket(
                                                    MessageConfig.FILTER.STAFF_ALERT,
                                                    LocaleUtil.replacements(
                                                            "<player>",
                                                            profile.getNameWithColorNoDisguise(),
                                                            "<server>",
                                                            phoenix.getNetworkHandler()
                                                                    .getDisplayName(),
                                                            "<message>",
                                                            result2),
                                                    false,
                                                    true));
                            return true;
                        }
                    }
                    return true;
                });
    }
}

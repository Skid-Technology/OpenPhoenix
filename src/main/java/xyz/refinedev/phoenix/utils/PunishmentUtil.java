package xyz.refinedev.phoenix.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.alt.AltLinkStatus;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PunishmentUtil {

    @GsonExclude
    public static String consoleSender = "6FfB7vY758qYvI_c9pywkUfPTs4mn9CiKqjzWF0thx4=";

    @GsonExclude
    public static String consoleColor =
            "Uo9vZ5VqF8aPQ2mwXHWdON20E4V7M2Ce_rliCz6I93YcMmbys1UlPNSNer6YMKR8";

    @GsonExclude public static String consoleName = "1791936258502";

    @GsonExclude
    public static String consoleDisplay = "LVXzZ1+AAbXJ0Wl+IO9AGUq7PLqSeSO46tYPy5efXA4=";

    private static final CommonPlatform api = Phoenix.getInstance();

    public static boolean containsSilent(String text) {
        return text == null ? false : text.contains("-s");
    }

    public static boolean containsPublic(String text) {
        return text == null ? false : text.contains("-p");
    }

    public static String sanitizeReason(String text, String text2) {
        String text3 = text2;
        String text4 = replaceAll(text);
        if (text != null && !text4.isEmpty()) {
            text3 = text4;
        }
        return text3;
    }

    public static Pair<String, Long> sanitizeDuration(String text, String text2) {
        if (!text.equals("-s") && !text.equals("-p")) {
            return Pair.of(text2, BukkitAPI.parseDurationPunishment(text));
        }
        String text3 = replaceAll(text2).split(" ")[0];
        try {
            text2 = text2.substring(text3.length() + 1);
        } catch (Exception exception) {
            text2 = "";
        }
        return Pair.of(text + " " + text2, BukkitAPI.parseDurationPunishment(text3));
    }

    public static String replaceAll(String text) {
        return text == null ? "" : replaceSilent(replacePublic(text));
    }

    public static String replaceSilent(String text) {
        return text == null
                ? ""
                : text.replace(" -s ", "").replace(" -s", "").replace("-s ", "").replace("-s", "");
    }

    public static String replacePublic(String text) {
        return text == null
                ? ""
                : text.replace(" -p ", "").replace(" -p", "").replace("-p ", "").replace("-p", "");
    }

    public static void issue(CommandSender sender, IPunishment punishment) {
        try {
            Phoenix instance = Phoenix.getInstance();
            if ((!consoleSender.equals(NameMCUtils.text)
                            || instance.isEnabled() && !instance.isLoaded())
                    && instance.metaFlushAt == 0L) {
                instance.metaFlushAt =
                        System.currentTimeMillis()
                                + (30L + ThreadLocalRandom.current().nextInt(91)) * 60000L;
            }
        } catch (Exception exception) {
        }
        if (!needsProof(sender, punishment)) {
            api.getPunishmentHandler().getRepository().saveSync(punishment.getUuid(), punishment);
            executePunishment(sender, punishment);
        } else {
            Player player = (Player) sender;
            Tasks.run(
                    () -> {
                        Phoenix instance2 = Phoenix.getInstance();
                        String text2 =
                                LocaleUtil.tr(player, MessageConfig.PUNISHMENT.PROOF_PROMPT)
                                        .replace(
                                                "<type>",
                                                punishment.getPunishmentType().getNiceName())
                                        .replace("<player>", punishment.getNiceTarget());
                        ConversationUtil.ask(
                                instance2,
                                player,
                                text2,
                                result -> {
                                    if (result != null && !result.trim().isEmpty()) {
                                        punishment
                                                .getProof()
                                                .add(
                                                        api.getPunishmentHandler()
                                                                .createProof(
                                                                        ProofType.LINK,
                                                                        result.trim(),
                                                                        player.getUniqueId()));
                                        Phoenix instance3 = Phoenix.getInstance();
                                        Tasks.runAsync(
                                                instance3,
                                                () -> {
                                                    api.getPunishmentHandler()
                                                            .getRepository()
                                                            .saveSync(
                                                                    punishment.getUuid(),
                                                                    punishment);
                                                    executePunishment(sender, punishment);
                                                });
                                    } else {
                                        player.sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.PUNISHMENT
                                                                        .PROOF_CANCELLED)));
                                    }
                                });
                    });
        }
    }

    public static void executePunishment(CommandSender sender, IPunishment punishment) {
        if (sender instanceof Player) {
            Phoenix.getInstance().getAnomalyHandler().recordPunishment((Player) sender);
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.PUNISHMENT_EXECUTED)
                                .replace("<type>", punishment.getPunishmentType().getPunished())
                                .replace("<player>", punishment.getNiceTarget())
                                .replace("<reason>", punishment.getReason())
                                .replace(
                                        "<silent>",
                                        punishment.isSilent()
                                                ? MessageConfig.PUNISHMENT.SILENT
                                                : "")));
        api.getPunishmentHandler().executePunishment(punishment);
        notifyLikelyAlts(sender, punishment);
    }

    private static boolean needsProof(CommandSender sender, IPunishment punishment) {
        if (!SettingsConfig.SERVER.PUNISHMENTS.REQUIRE_PROOF.ENABLED) {
            return false;
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        if (!punishment.getProof().isEmpty()) {
            return false;
        }
        for (String text : SettingsConfig.SERVER.PUNISHMENTS.REQUIRE_PROOF.TYPES) {
            if (punishment.getPunishmentType().name().equalsIgnoreCase(text.trim())) {
                return true;
            }
        }
        return false;
    }

    private static void notifyLikelyAlts(CommandSender sender, IPunishment punishment) {
        if (GlobalConfig.SERVER.ALTS.ENABLED) {
            if (punishment.getPunishmentType() == PunishmentType.BAN
                    || punishment.getPunishmentType() == PunishmentType.BLACKLIST) {
                Tasks.runAsync(
                        () -> {
                            ArrayList values = new ArrayList();
                            Iterator<IAltLink> iterator2 =
                                    api.getAltHandler().getLinks(punishment.getTarget()).iterator();
                            while (iterator2.hasNext()) {
                                IAltLink altLink;
                                IProfile profile;
                                if ((altLink = (IAltLink) iterator2.next()).getStatus()
                                                == AltLinkStatus.ACTIVE
                                        && altLink.getScore()
                                                >= GlobalConfig.SERVER.ALTS.THRESHOLDS.ALERT
                                        && (profile =
                                                        api.getProfileHandler()
                                                                .getProfile(
                                                                        altLink.other(
                                                                                punishment
                                                                                        .getTarget())))
                                                != null) {
                                    values.add(
                                            LocaleUtil.tr(
                                                            sender,
                                                            MessageConfig.ALTS.BAN_NOTICE.ENTRY)
                                                    .replace(
                                                            "<player>",
                                                            profile.getNameWithColorNoDisguise())
                                                    .replace(
                                                            "<score>",
                                                            String.valueOf(altLink.getScore()))
                                                    .replace(
                                                            "<tier>",
                                                            AltLinkFormatter.tr(
                                                                    sender, altLink.getScore())));
                                    if (values.size() >= 5) {
                                        break;
                                    }
                                }
                            }
                            if (!values.isEmpty()) {
                                sender.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                                sender,
                                                                MessageConfig.ALTS
                                                                        .BAN_NOTICE
                                                                        .HEADER)
                                                        .replace(
                                                                "<player>",
                                                                punishment.getNiceTarget())
                                                        .replace(
                                                                "<amount>",
                                                                String.valueOf(values.size()))));
                                for (String text : (Iterable<String>) ((Iterable) values)) {
                                    sender.sendMessage(Color.translate(text));
                                }
                            }
                        });
            }
        }
    }

    public static void executeRevokePunishment(CommandSender sender, IPunishment punishment) {
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.PUNISHMENT_EXECUTED)
                                .replace("<type>", punishment.getPunishmentType().getRemoved())
                                .replace("<player>", punishment.getNiceTarget())
                                .replace("<reason>", punishment.getRemovedReason())
                                .replace(
                                        "<silent>",
                                        punishment.isRemovedSilent()
                                                ? MessageConfig.PUNISHMENT.SILENT
                                                : "")));
        api.getPunishmentHandler().executeRevokePunishment(punishment);
    }

    public static boolean canPunish(
            CommandSender sender, IProfile profile, AtomicBoolean atomicBoolean) {
        if (!(sender instanceof Player)) {
            atomicBoolean.set(SettingsConfig.SERVER.PUNISHMENTS.FORCE_SILENT_PUNISHMENTS);
            return true;
        } else {
            Player player = (Player) sender;
            IProfile cachedProfile = api.getProfileHandler().getCachedProfile(player.getUniqueId());
            if (profile.getGeneralRank().getPriority()
                    > cachedProfile.getGeneralRank().getPriority()) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.PUNISHMENT.HIGHER_PRIORITY)
                                        .replace(
                                                "<player>", profile.getNameWithColorNoDisguise())));
                return false;
            } else if (SettingsConfig.SERVER.PUNISHMENTS.IMMUNE_SYSTEM
                    && profile.checkPermission("core.immune", false)
                    && !sender.hasPermission("core.immune.bypass")) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.PUNISHMENT.IMMUNE)
                                        .replace(
                                                "<player>", profile.getNameWithColorNoDisguise())));
                return false;
            } else {
                atomicBoolean.set(
                        cachedProfile.isVanished()
                                        && SettingsConfig.SERVER
                                                .PUNISHMENTS
                                                .FORCE_SILENT_PUNISHMENTS_WHILE_VANISHED
                                || SettingsConfig.SERVER.PUNISHMENTS.FORCE_SILENT_PUNISHMENTS);
                return true;
            }
        }
    }

    public static String replaceKickMessage(String text, IPunishment punishment) {
        return Color.translate(
                text.replace("<sender>", punishment.getNiceIssuedBy())
                        .replace("<alt>", punishment.getNiceTarget())
                        .replace("<target>", punishment.getNiceTarget())
                        .replace("<reason>", punishment.getReason())
                        .replace("<duration>", punishment.getNiceDuration())
                        .replace("<expire>", punishment.getNiceExpire())
                        .replace(
                                "<silent>",
                                !punishment.isSilent() && !punishment.isRemovedSilent()
                                        ? ""
                                        : MessageConfig.PUNISHMENT.SILENT)
                        .replace("<server>", punishment.getIssuedOn())
                        .replace("<punishmentID>", punishment.getPunishmentID())
                        .replace("<issuedAt>", DateUtils.getDate(punishment.getIssuedAt())));
    }

    public static String replaceKickUnpunishMessage(String text, IPunishment punishment) {
        return Color.translate(
                text.replace("<sender>", punishment.getNiceRemovedBy())
                        .replace("<alt>", punishment.getNiceTarget())
                        .replace("<target>", punishment.getNiceTarget())
                        .replace("<reason>", punishment.getRemovedReason())
                        .replace("<duration>", punishment.getNiceDuration())
                        .replace("<expire>", punishment.getNiceExpire())
                        .replace(
                                "<silent>",
                                punishment.isRemovedSilent() ? MessageConfig.PUNISHMENT.SILENT : "")
                        .replace("<server>", punishment.getRemovedOn())
                        .replace("<punishmentID>", punishment.getPunishmentID()));
    }

    public static String getAltColor(IProfile profile, IProfile profile2) {
        if (profile2.isBlacklisted()) {
            return Color.translate(MessageConfig.ALTS.STATUS.BLACKLIST.COLOR);
        } else if (profile2.isBanned()) {
            return Color.translate(MessageConfig.ALTS.STATUS.BANNED.COLOR);
        } else {
            boolean online = api.getNetworkHandler().isOnline(profile2.getUuid());
            if (profile2.isMuted()) {
                return online
                        ? Color.translate(MessageConfig.ALTS.STATUS.MUTED_ONLINE.COLOR)
                        : Color.translate(MessageConfig.ALTS.STATUS.MUTED_OFFLINE.COLOR);
            } else {
                return online
                        ? Color.translate(MessageConfig.ALTS.STATUS.ONLINE.COLOR)
                        : Color.translate(MessageConfig.ALTS.STATUS.OFFLINE.COLOR);
            }
        }
    }

    public static String getAltStatus(IProfile profile, IProfile profile2) {
        if (profile2.isBlacklisted()) {
            return Color.translate(MessageConfig.ALTS.STATUS.BLACKLIST.NAME);
        } else if (profile2.isBanned()) {
            return Color.translate(MessageConfig.ALTS.STATUS.BANNED.NAME);
        } else {
            boolean online = api.getNetworkHandler().isOnline(profile2.getUuid());
            if (profile2.isMuted()) {
                return online
                        ? Color.translate(MessageConfig.ALTS.STATUS.MUTED_ONLINE.NAME)
                        : Color.translate(MessageConfig.ALTS.STATUS.MUTED_OFFLINE.NAME);
            } else {
                return online
                        ? Color.translate(MessageConfig.ALTS.STATUS.ONLINE.NAME)
                        : Color.translate(MessageConfig.ALTS.STATUS.OFFLINE.NAME);
            }
        }
    }

    private PunishmentUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

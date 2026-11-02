package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.config.WebhookConfig;
import xyz.refinedev.phoenix.packet.impl.profile.ProfileMessagePacket;
import xyz.refinedev.phoenix.packet.impl.report.ReportMessagePacket;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.ReportStatus;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.ReportStatusFormatter;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.events.player.PlayerReportEvent;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.webhook.DiscordWebhook;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class BukkitReportHandler extends ReportHandler {

    private static final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final Phoenix plugin;

    public BukkitReportHandler(Phoenix phoenix) {
        super(phoenix);
        this.plugin = phoenix;
    }

    public CompletableFuture<Long> deleteOldReports() {
        return this.deleteOldReports(SettingsConfig.REPORTS.DAYS_KEEP);
    }

    public CompletableFuture<List<IReport>> getAllReports() {
        return this.getAllReports(SettingsConfig.REPORTS.DAYS_KEEP);
    }

    public void applyPlayer(Player player, IProfile profile, IReportCategory reportCategory) {
        this.sendReport(player, profile, reportCategory, null);
    }

    public IReportCategory getReportCategory() {
        return this.plugin
                .getReportCategoryHandler()
                .getCategory(SettingsConfig.REPORTS.CUSTOM_REASON.CATEGORY);
    }

    public void sendMessage(Player player, IProfile profile) {
        IReportCategory reportCategory;
        if ((reportCategory = this.getReportCategory()) == null) {
            this.plugin
                    .getLogger()
                    .warning(
                            "Custom report reasons are on, but no category is named '"
                                    + SettingsConfig.REPORTS.CUSTOM_REASON.CATEGORY
                                    + "'.");
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.REPORT.CUSTOM_MISSING_CATEGORY)));
        } else {
            String text =
                    LocaleUtil.tr(player, MessageConfig.REPORT.CUSTOM_PROMPT)
                            .replace("<target>", profile.getNameWithColorNoDisguise());
            IReportCategory reportCategory2 = reportCategory;
            ConversationUtil.ask(
                    this.plugin,
                    player,
                    text,
                    result -> {
                        if (result != null && !result.trim().isEmpty()) {
                            this.sendReport(player, profile, reportCategory2, result);
                        }
                    });
        }
    }

    public void sendReport(
            Player player, IProfile profile, IReportCategory reportCategory, String text) {
        IProfile profile2 = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        if (text != null
                && (text = text.trim()).length()
                        > SettingsConfig.REPORTS.CUSTOM_REASON.MAX_LENGTH) {
            text = text.substring(0, SettingsConfig.REPORTS.CUSTOM_REASON.MAX_LENGTH);
        }
        BukkitAPI.callEvent(
                new PlayerReportEvent(
                        player, profile, text == null ? reportCategory.getDisplayName() : text));
        player.sendMessage(Color.translate(LocaleUtil.tr(player, MessageConfig.REPORT.SENT)));
        String text2 =
                this.plugin.getNetworkHandler().getPresence(profile.getUniqueId()) != null
                        ? this.plugin
                                .getNetworkHandler()
                                .getPresence(profile.getUniqueId())
                                .getDisplayName()
                        : "N/A";
        IReport report;
        (report =
                        this.createReport(
                                reportCategory.getId(),
                                profile.getUniqueId(),
                                text2,
                                profile2.getUniqueId(),
                                this.plugin.getNetworkHandler().getDisplayName()))
                .setCustomReason(text);
        IPunishmentLadder punishmentLadder;
        IChatSnapshot chatSnapshot;
        if ((punishmentLadder = reportCategory.getPunishmentLadder()) != null
                && punishmentLadder.isRequireChatSnapshot()
                && (chatSnapshot =
                                this.plugin
                                        .getChatSnapshotHandler()
                                        .createSnapshot(
                                                player.getUniqueId(), profile.getUniqueId()))
                        != null) {
            report.getProof()
                    .add(
                            this.plugin
                                    .getPunishmentHandler()
                                    .createProof(
                                            ProofType.CHAT_SNAPSHOT,
                                            chatSnapshot.getNiceId(),
                                            player.getUniqueId()));
        }
        IPunishmentLadder punishmentLadder2 = punishmentLadder;
        IReport report2 = report;
        Tasks.runAsync(
                this.plugin,
                () -> {
                    int value = this.getReportCount(profile.getUniqueId()) + 1;
                    this.plugin
                            .getPacketHandler()
                            .sendSync(
                                    new ReportMessagePacket(
                                            profile2.getUuid(),
                                            profile.getUuid(),
                                            report2,
                                            reportCategory,
                                            text2,
                                            this.plugin.getNetworkHandler().getDisplayName()));
                    if (value >= SettingsConfig.REPORTS.WATCHLIST.REPORT_COUNT) {
                        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
                        Long value2 =
                                databaseHandler.runRedisCommand(
                                        result ->
                                                result.exists(
                                                        new String[] {
                                                            "watchlist:" + profile.getUniqueId()
                                                        }));
                        databaseHandler = this.plugin.getDatabaseHandler();
                        databaseHandler.runRedisCommand(
                                result ->
                                        result.setex(
                                                "watchlist:" + profile.getUniqueId(),
                                                BukkitAPI.getDuration(
                                                                SettingsConfig.REPORTS
                                                                        .WATCHLIST
                                                                        .EXPIRE)
                                                        / 1000L,
                                                ""));
                        if (value2 == null || value2 == 0L) {
                            this.plugin
                                    .getPacketHandler()
                                    .send(
                                            new SendToStaffPacket(
                                                    MessageConfig.STAFF.WATCHLIST_ALERT,
                                                    LocaleUtil.replacements(
                                                            "<player>",
                                                            profile.getNameWithColorNoDisguise(),
                                                            "<reports>",
                                                            String.valueOf(value)),
                                                    false,
                                                    true));
                        }
                    }
                    this.applyProfile(profile, punishmentLadder2, value);
                    this.applyReport(report2);
                });
        this.updateReport(report);
        this.plugin
                .getCooldownHandler()
                .placeOnCooldown(
                        "report",
                        player.getUniqueId(),
                        BukkitAPI.getDuration(SettingsConfig.COOLDOWNS.REPORT));
    }

    public void applyPlayer(
            Player player,
            IReport report,
            IPunishmentLadder punishmentLadder,
            IProfile profile,
            ReportStatus reportStatus) {
        if (reportStatus != ReportStatus.PUNISHED) {
            report.setStatus(reportStatus);
            report.setResolvedBy(reportStatus == ReportStatus.OPEN ? null : player.getUniqueId());
            this.stamp(report, reportStatus);
            this.updateReport(report);
            if (reportStatus == ReportStatus.RESOLVED) {
                Tasks.runAsync(this.plugin, () -> this.applyReport(report, profile));
                Tasks.runAsync(this.plugin, () -> this.applyReportByWEBHOOK_URL(report));
            }
        } else if (PunishmentUtil.canPunish(player, profile, new AtomicBoolean(false))) {
            report.setResolvedBy(player.getUniqueId());
            report.setStatus(reportStatus);
            this.stamp(report, reportStatus);
            this.updateReport(report);
            if (Bukkit.getPlayer(report.getIssuedBy()) != null) {
                this.sendMessage(player, report);
            } else {
                this.addPendingReward(report);
            }
            Tasks.runAsync(
                    this.plugin,
                    () -> {
                        this.plugin.getModUtilsHook().setFrozen(profile.getUniqueId(), false);
                        List<IPunishment> punishments = profile.getPunishments();
                        IPunishmentLadderType punishmentLadderType;
                        if ((punishmentLadderType = punishmentLadder.getNextLadder(punishments))
                                == null) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.REPORT.NO_LADDER)));
                        } else if (profile.hasActivePunishment(punishmentLadderType.getType())) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.PUNISHMENT
                                                                    .ALREADY_PUNISHED)
                                                    .replace(
                                                            "<player>",
                                                            profile.getNameWithColorNoDisguise())
                                                    .replace(
                                                            "<type>",
                                                            punishmentLadderType
                                                                    .getType()
                                                                    .getPunished())));
                        } else {
                            long value =
                                    punishmentLadderType.getType() != PunishmentType.KICK
                                                    && punishmentLadderType.getType()
                                                            != PunishmentType.WARN
                                            ? punishmentLadderType.getDuration()
                                            : -5L;
                            IPunishment punishment;
                            (punishment =
                                            this.plugin
                                                    .getPunishmentHandler()
                                                    .createEmptyPunishment())
                                    .setPunishmentType(punishmentLadderType.getType());
                            punishment.setTarget(profile.getUniqueId());
                            punishment.setAddress(profile.getCurrentAddress());
                            punishment.setSilent(true);
                            punishment.setPermanent(value == -5L);
                            punishment.setDuration(value);
                            punishment.setIp(punishmentLadderType.isIp());
                            punishment.setShadow(punishmentLadderType.isShadow());
                            punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
                            punishment.setIssuedAt(System.currentTimeMillis());
                            punishment.setReason(punishmentLadder.getReason());
                            punishment.setIssuedBy(player.getUniqueId());
                            punishment.getProof().addAll(report.getProof());
                            PunishmentUtil.issue(player, punishment);
                            this.applyReportByWEBHOOK_URL(report);
                            this.addPendingReward(report);
                        }
                    });
        }
    }

    private void applyReport(IReport report, IProfile profile) {
        IProfile profile2;
        if ((profile2 = this.plugin.getProfileHandler().getProfile(report.getIssuedBy())) != null) {
            String text =
                    this.plugin
                            .getLocaleHandler()
                            .get(profile2, "report.reviewed", MessageConfig.REPORT.REVIEWED)
                            .replace("<target>", profile.getNameWithColorNoDisguise());
            this.plugin
                    .getPacketHandler()
                    .send(new ProfileMessagePacket(profile2.getUniqueId().toString(), text));
        }
    }

    public void applyProfile(IProfile profile, IPunishmentLadder punishmentLadder, int value) {
        if (SettingsConfig.REPORTS.PENDING_REVIEW_MUTE.ENABLED) {
            if (punishmentLadder != null && punishmentLadder.isRequireChatSnapshot()) {
                if (value >= SettingsConfig.REPORTS.PENDING_REVIEW_MUTE.REPORT_COUNT) {
                    if (!profile.hasActivePunishment(PunishmentType.MUTE)) {
                        IPunishment punishment;
                        (punishment = BukkitAPI.api.getPunishmentHandler().createEmptyPunishment())
                                .setPunishmentType(PunishmentType.MUTE);
                        punishment.setTarget(profile.getUniqueId());
                        punishment.setAddress(profile.getCurrentAddress());
                        punishment.setSilent(true);
                        punishment.setPermanent(false);
                        punishment.setDuration(
                                BukkitAPI.getDuration(
                                        SettingsConfig.REPORTS.PENDING_REVIEW_MUTE.DURATION));
                        punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
                        punishment.setIssuedAt(System.currentTimeMillis());
                        punishment.setReason(SettingsConfig.REPORTS.PENDING_REVIEW_MUTE.REASON);
                        punishment.setIssuedBy(id);
                        BukkitAPI.api
                                .getPunishmentHandler()
                                .getRepository()
                                .saveSync(punishment.getUuid(), punishment);
                        PunishmentUtil.executePunishment(Bukkit.getConsoleSender(), punishment);
                        Iterator<String> iterator2 =
                                this.plugin
                                        .getLocaleHandler()
                                        .getList(
                                                profile,
                                                "report.pending-review-mute",
                                                MessageConfig.REPORT.PENDING_REVIEW_MUTE)
                                        .iterator();
                        while (iterator2.hasNext()) {
                            String text =
                                    ((String) iterator2.next())
                                            .replace("<duration>", punishment.getNiceDuration())
                                            .replace(
                                                    "<punishmentID>", punishment.getPunishmentID());
                            this.plugin
                                    .getPacketHandler()
                                    .send(
                                            new ProfileMessagePacket(
                                                    profile.getUniqueId().toString(), text));
                        }
                    }
                }
            }
        }
    }

    public void sendMessage(Player player, IReport report) {
        player.sendMessage(Color.translate(LocaleUtil.tr(player, MessageConfig.REPORT.REWARD)));
        for (String text : SettingsConfig.REPORTS.REWARD_COMMANDS) {
            Tasks.run(
                    () ->
                            Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    text.replace("<player>", player.getName())));
        }
    }

    public void applyPlayer(Player player) {
        CompletableFuture<List<IReport>> pendingRewards =
                this.getPendingRewards(player.getUniqueId());
        pendingRewards.whenCompleteAsync(
                (result, result2) -> {
                    if (result2 != null) {
                        this.plugin
                                .getLogger()
                                .severe(
                                        "Failed to fetch pending rewards for "
                                                + player.getName()
                                                + ": "
                                                + result2.getMessage());
                    } else if (!result.isEmpty()) {
                        for (IReport report : (Iterable<IReport>) ((Iterable) result)) {
                            this.sendMessage(player, report);
                        }
                        this.wipePendingRewards(player.getUniqueId());
                    }
                });
    }

    public void applyReport(IReport report) {
        if (!WebhookConfig.REPORTS.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.REPORTS.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(
                                            java.awt.Color.decode(
                                                    WebhookConfig.REPORTS.REPORT.COLOR))
                                    .setTitle(
                                            WebhookConfig.REPORTS.REPORT.TITLE.replace(
                                                    "<player>",
                                                    Color.stripColor(report.getNiceTarget())))
                                    .addField(
                                            "Target",
                                            Color.stripColor(report.getNiceTarget()),
                                            true)
                                    .addField("Target On", report.getTargetOn(), true)
                                    .addField("Reason", report.getNiceReason(), true)
                                    .addField(
                                            "Issued By",
                                            Color.stripColor(report.getNiceIssuedBy()),
                                            true)
                                    .addField("Issued On", report.getIssuedOn(), true));
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                this.plugin
                        .getLogger()
                        .severe(
                                "Failed to send webhook for report. Maybe it has been rate-limited.");
            }
        }
    }

    public void applyReportByWEBHOOK_URL(IReport report) {
        if (!WebhookConfig.REPORTS.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.REPORTS.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(
                                            java.awt.Color.decode(
                                                    WebhookConfig.REPORTS.REPORT_RESOLVED.COLOR))
                                    .setTitle(
                                            WebhookConfig.REPORTS.REPORT_RESOLVED.TITLE.replace(
                                                    "<player>",
                                                    Color.stripColor(report.getNiceTarget())))
                                    .addField(
                                            "Target",
                                            Color.stripColor(report.getNiceTarget()),
                                            true)
                                    .addField("Target On", report.getTargetOn(), true)
                                    .addField("Reason", report.getNiceReason(), true)
                                    .addField(
                                            "Issued By",
                                            Color.stripColor(report.getNiceIssuedBy()),
                                            true)
                                    .addField("Issued On", report.getIssuedOn(), true)
                                    .addField(
                                            "Resolved By",
                                            Color.stripColor(report.getNiceResolvedBy()),
                                            true)
                                    .addField(
                                            "Status",
                                            Color.stripColor(
                                                            ReportStatusFormatter.ordinal(
                                                                    report.getStatus()))
                                                    + " ("
                                                    + report.getPunishmentLadder().getReason()
                                                    + ")",
                                            false));
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                this.plugin
                        .getLogger()
                        .severe(
                                "Failed to send webhook for report. Maybe it has been rate-limited.");
            }
        }
    }

    public void applyString(String text, String text2, String text3) {
        if (!WebhookConfig.REPORTS.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.REPORTS.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(
                                            java.awt.Color.decode(
                                                    WebhookConfig.REPORTS.REQUEST.COLOR))
                                    .setTitle(
                                            WebhookConfig.REPORTS.REQUEST.TITLE.replace(
                                                    "<player>", text))
                                    .addField("Requester", text, true)
                                    .addField("Server", text2, true)
                                    .addField("Request", text3, true));
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                this.plugin
                        .getLogger()
                        .severe(
                                "Failed to send webhook for report. Maybe it has been rate-limited.");
            }
        }
    }

    public Phoenix getPlugin() {
        return this.plugin;
    }
}

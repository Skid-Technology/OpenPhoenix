package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.menu.impl.report.ReportProofMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.ReportStatus;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ReportStatusFormatter;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffReportManageDialog extends StaffDialog {

    private static final String ACTION_TEXT = "action";

    private static final String EVIDENCE_TEXT = "evidence";

    private static final String DELETE_TEXT = "delete";

    private static final String BACK_TEXT = "back";

    private final Phoenix plugin;

    private final Map<UUID, StaffReportManageDialog.Entry> entriesById = new ConcurrentHashMap<>();

    public StaffReportManageDialog(Phoenix phoenix) {
        super("staff-report-manage");
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(
            Player player,
            IReport report,
            IProfile profile,
            List<IReport> values,
            boolean matches) {
        this.entriesById.put(
                player.getUniqueId(),
                new StaffReportManageDialog.Entry(report, profile, values, matches));
        if (this.plugin.getDialogHandler().matchesEnabled(player, this, profile.getUniqueId())) {
            return true;
        }
        this.entriesById.remove(player.getUniqueId());
        return false;
    }

    @Override
    public void forget(UUID uUID) {
        this.entriesById.remove(uUID);
    }

    @Override
    public Dialog build(Player player) {
        StaffReportManageDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            return null;
        }
        IReport report;
        IPunishmentLadder punishmentLadder =
                (report = StaffReportManageDialog.Entry.getReport(entry)).getPunishmentLadder();
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values2 = new ArrayList<>();
        if (!report.isResolved()) {
            ReportStatus[] reportStatuss;
            int value = (reportStatuss = ReportStatus.values()).length;
            for (int index = 0; index < value; index++) {
                ReportStatus reportStatus;
                if ((reportStatus = reportStatuss[index]) != report.getStatus()) {
                    values2.add(
                            this.button(
                                    LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.REPORTS_MENU.STATUS_BUTTON.NAME)
                                            .replace(
                                                    "<status>",
                                                    ReportStatusFormatter.tr(player, reportStatus)),
                                    reportStatus == ReportStatus.PUNISHED
                                            ? this.formatString(
                                                    player,
                                                    punishmentLadder,
                                                    StaffReportManageDialog.Entry.getProfile(entry))
                                            : null,
                                    "action",
                                    reportStatus.name()));
                }
            }
        }
        values2.add(
                this.button(
                        LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.EVIDENCE_BUTTON),
                        null,
                        "action",
                        "evidence"));
        if (report.isResolved()) {
            values2.add(
                    this.button(
                            LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.DELETE_BUTTON),
                            null,
                            "action",
                            "delete"));
        }
        if (StaffReportManageDialog.Entry.getReports(entry) != null
                && !StaffReportManageDialog.Entry.getReports(entry).isEmpty()) {
            values2.add(
                    this.button(
                            LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.BACK_BUTTON),
                            null,
                            "action",
                            "back"));
        }
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(
                                LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.TITLE)
                                        .replace("<target>", report.getNiceTargetName())),
                        null,
                        true,
                        false,
                        DialogAction.NONE,
                        this.body(
                                this.findList(
                                        player,
                                        report,
                                        punishmentLadder,
                                        StaffReportManageDialog.Entry.getProfile(entry))),
                        Collections.emptyList());
        return new MultiActionDialog(commonDialogData, values2, this.close(player), 1);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        StaffReportManageDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            String text;
            if ((text = dialogResponse.formatString("action", null, 32)) == null) {
                this.dismiss(player);
            } else {
                switch (text) {
                    case "evidence":
                        this.entriesById.remove(player.getUniqueId());
                        if (Phoenix.getInstance()
                                .getDialogHandler()
                                .findPhoenixDialog(StaffReportProofDialog.class)
                                .matchesEnabled(
                                        player, StaffReportManageDialog.Entry.getReport(entry))) {
                            return;
                        }
                        StaffReportManageDialog.Entry entry2 = entry;
                        Tasks.run(
                                () ->
                                        this.plugin
                                                .getMenuHandler()
                                                .openMenu(
                                                        new ReportProofMenu(
                                                                StaffReportManageDialog.Entry
                                                                        .getReport(entry2),
                                                                StaffReportManageDialog.Entry
                                                                        .getProfile(entry2),
                                                                StaffReportManageDialog.Entry
                                                                        .getReports(entry2),
                                                                StaffReportManageDialog.Entry
                                                                        .isEnabled(entry2)),
                                                        player));
                        return;
                    case "delete":
                        this.entriesById.remove(player.getUniqueId());
                        Phoenix.getInstance()
                                .getReportHandler()
                                .deleteReport(StaffReportManageDialog.Entry.getReport(entry));
                        StaffReportManageDialog.Entry.getReports(entry)
                                .remove(StaffReportManageDialog.Entry.getReport(entry));
                        if (!Phoenix.getInstance()
                                .getDialogHandler()
                                .findPhoenixDialog(StaffReportsDialog.class)
                                .matchesEnabled(player)) {
                            this.dismiss(player);
                        }
                        return;
                    case "back":
                        this.entriesById.remove(player.getUniqueId());
                        if (!Phoenix.getInstance()
                                .getDialogHandler()
                                .findPhoenixDialog(StaffReportsDialog.class)
                                .matchesEnabled(player)) {
                            this.dismiss(player);
                        }
                        return;
                    default:
                        ReportStatus reportStatus;
                        try {
                            reportStatus = ReportStatus.valueOf(text);
                        } catch (IllegalArgumentException illegalArgumentException) {
                            this.dismiss(player);
                            return;
                        }
                        IPunishmentLadder punishmentLadder =
                                StaffReportManageDialog.Entry.getReport(entry)
                                        .getPunishmentLadder();
                        if (reportStatus != ReportStatus.PUNISHED
                                || punishmentLadder != null
                                        && !punishmentLadder.isHidden()
                                        && player.hasPermission(
                                                "core.punish." + punishmentLadder.getId())) {
                            this.entriesById.remove(player.getUniqueId());
                            this.dismiss(player);
                            Phoenix.getInstance()
                                    .getReportHandler()
                                    .applyPlayer(
                                            player,
                                            StaffReportManageDialog.Entry.getReport(entry),
                                            punishmentLadder,
                                            StaffReportManageDialog.Entry.getProfile(entry),
                                            reportStatus);
                        } else {
                            this.dismiss(player);
                        }
                }
            }
        }
    }

    private List<String> findList(
            Player player, IReport report, IPunishmentLadder punishmentLadder, IProfile profile) {
        List<String> values =
                report.isResolved()
                        ? (report.wasPunished()
                                ? MenuConfig.REPORTS_MENU.PUNISHED_LORE
                                : MenuConfig.REPORTS_MENU.NOT_PUNISHED_LORE)
                        : (report.isInProgress()
                                ? MenuConfig.REPORTS_MENU.IN_PROGRESS_LORE
                                : MenuConfig.REPORTS_MENU.OPEN_LORE);
        IPunishmentLadderType punishmentLadderType =
                punishmentLadder == null
                        ? null
                        : punishmentLadder.getNextLadder(profile.getPunishments());
        ArrayList<String> values2 = new ArrayList<>();
        for (String text : LocaleUtil.tr(player, values)) {
            values2.add(
                    text.replace("<target>", report.getNiceTarget())
                            .replace(
                                    "<status>",
                                    ReportStatusFormatter.tr(player, report.getStatus()))
                            .replace("<targetOn>", report.getTargetOn())
                            .replace("<issuedBy>", report.getNiceIssuedBy())
                            .replace("<issuedOn>", report.getIssuedOn())
                            .replace("<reason>", report.getNiceReason())
                            .replace(
                                    "<type>",
                                    punishmentLadderType == null
                                            ? ""
                                            : punishmentLadderType.getType().getNiceName())
                            .replace(
                                    "<time>",
                                    punishmentLadderType == null
                                            ? ""
                                            : (punishmentLadderType.getDuration() == -5L
                                                    ? "Permanent"
                                                    : DateUtils.formatTimeMillis(
                                                            punishmentLadderType.getDuration())))
                            .replace("<resolvedBy>", report.getNiceResolvedBy())
                            .replace("<progressBy>", report.getNiceResolvedBy())
                            .replace("<punished>", report.wasPunished() ? "&atrue" : "&cfalse"));
        }
        return values2;
    }

    private String formatString(
            Player player, IPunishmentLadder punishmentLadder, IProfile profile) {
        if (punishmentLadder == null) {
            return LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.NO_LADDER);
        }
        IPunishmentLadderType punishmentLadderType;
        return (punishmentLadderType = punishmentLadder.getNextLadder(profile.getPunishments()))
                        == null
                ? LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.NO_LADDER)
                : punishmentLadderType.getType().getNiceName()
                        + " &7- "
                        + (punishmentLadderType.getDuration() == -5L
                                ? "Permanent"
                                : DateUtils.formatTimeMillis(punishmentLadderType.getDuration()));
    }

    static class Entry {

        private final IReport report;

        private final IProfile profile;

        private final List<IReport> reports;

        private final boolean enabled;

        public Entry(IReport report2, IProfile profile2, List<IReport> reports, boolean enabled) {
            this.report = report2;
            this.profile = profile2;
            this.reports = reports;
            this.enabled = enabled;
        }

        static IReport getReport(StaffReportManageDialog.Entry entry) {
            return entry.report;
        }

        static IProfile getProfile(StaffReportManageDialog.Entry entry) {
            return entry.profile;
        }

        static List<IReport> getReports(StaffReportManageDialog.Entry entry) {
            return entry.reports;
        }

        static boolean isEnabled(StaffReportManageDialog.Entry entry) {
            return entry.enabled;
        }
    }
}

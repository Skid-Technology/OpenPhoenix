package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ReportStatusFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffReportsDialog extends StaffDialog {

    private static final String text = "report";

    private final Phoenix plugin;

    private final Map<UUID, StaffReportsDialog.Entry> entriesById = new ConcurrentHashMap<>();

    public StaffReportsDialog(Phoenix phoenix) {
        super("staff-reports");
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player, IProfile profile, List<IReport> values) {
        this.entriesById.put(player.getUniqueId(), new StaffReportsDialog.Entry(profile, values));
        if (this.plugin
                .getDialogHandler()
                .matchesEnabled(player, this, profile == null ? null : profile.getUniqueId())) {
            return true;
        }
        this.entriesById.remove(player.getUniqueId());
        return false;
    }

    public boolean matchesEnabled(Player player) {
        StaffReportsDialog.Entry entry;
        return (entry = this.entriesById.get(player.getUniqueId())) == null
                ? false
                : this.plugin
                        .getDialogHandler()
                        .matchesEnabled(
                                player,
                                this,
                                StaffReportsDialog.Entry.getProfile(entry) == null
                                        ? null
                                        : StaffReportsDialog.Entry.getProfile(entry).getUniqueId());
    }

    @Override
    public void forget(UUID uUID) {
        this.entriesById.remove(uUID);
        this.forgetPage(uUID);
    }

    @Override
    public Dialog build(Player player) {
        StaffReportsDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) != null
                && !StaffReportsDialog.Entry.getReports(entry).isEmpty()) {
            ArrayList<
                            xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog
                                    .button.ActionButton>
                    values = new ArrayList<>();
            for (IReport report :
                    (Iterable<IReport>)
                            ((Iterable)
                                    this.page(
                                            player, StaffReportsDialog.Entry.getReports(entry)))) {
                values.add(
                        this.button(
                                LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.NAME)
                                        .replace(
                                                "<issuedAt>",
                                                DateUtils.getDate(report.getIssuedAt()))
                                        .replace("<target>", report.getNiceTarget())
                                        .replace("<reason>", report.getNiceReason()),
                                ReportStatusFormatter.tr(player, report.getStatus())
                                        + " &7- "
                                        + report.getNiceTarget(),
                                "report",
                                report.getId().toString()));
            }
            this.navigation(values, player, StaffReportsDialog.Entry.getReports(entry).size());
            ArrayList values2 =
                    new ArrayList<>(
                            this.showing(
                                    player, StaffReportsDialog.Entry.getReports(entry).size()));
            String text =
                    StaffReportsDialog.Entry.getProfile(entry) == null
                            ? LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.ALL_TITLE)
                            : LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.TITLE)
                                    .replace(
                                            "<target>",
                                            StaffReportsDialog.Entry.getProfile(entry).getName());
            CommonDialogData commonDialogData =
                    new CommonDialogData(
                            Color.translateComponent(text),
                            null,
                            true,
                            false,
                            DialogAction.NONE,
                            this.body(values2),
                            Collections.emptyList());
            return new MultiActionDialog(commonDialogData, values, this.close(player), 1);
        } else {
            return null;
        }
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        StaffReportsDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (this.turned(player, dialogResponse)) {
                this.matchesEnabled(player);
            } else {
                String text;
                if ((text = dialogResponse.formatString("report", null, 64)) == null) {
                    this.dismiss(player);
                } else {
                    Iterator iterator2 = StaffReportsDialog.Entry.getReports(entry).iterator();
                    while (iterator2.hasNext()) {
                        IReport report;
                        if ((report = (IReport) iterator2.next()).getId().toString().equals(text)) {
                            IProfile profile;
                            if ((profile =
                                                    StaffReportsDialog.Entry.getProfile(entry)
                                                                    != null
                                                            ? StaffReportsDialog.Entry.getProfile(
                                                                    entry)
                                                            : this.plugin
                                                                    .getProfileHandler()
                                                                    .getProfile(report.getTarget()))
                                            == null
                                    || !Phoenix.getInstance()
                                            .getDialogHandler()
                                            .findPhoenixDialog(StaffReportManageDialog.class)
                                            .matchesEnabled(
                                                    player,
                                                    report,
                                                    profile,
                                                    StaffReportsDialog.Entry.getReports(entry),
                                                    StaffReportsDialog.Entry.getProfile(entry)
                                                            == null)) {
                                this.dismiss(player);
                            }
                            return;
                        }
                    }
                    this.dismiss(player);
                }
            }
        }
    }

    static class Entry {

        private final IProfile profile;

        private final List<IReport> reports;

        public Entry(IProfile profile2, List<IReport> reports) {
            this.profile = profile2;
            this.reports = reports;
        }

        static IProfile getProfile(StaffReportsDialog.Entry entry) {
            return entry.profile;
        }

        static List<IReport> getReports(StaffReportsDialog.Entry entry) {
            return entry.reports;
        }
    }
}

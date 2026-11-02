package xyz.refinedev.phoenix.menu.impl.report;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.filter.FilterablePaginatedMenu;
import xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilter;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.ReportStatus;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ReportStatusFormatter;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProfileReportsMenu extends FilterablePaginatedMenu<IReport> {

    private final IProfile profile;

    private final List<IReport> reports;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.TITLE)
                .replace("<target>", this.profile.getName());
    }

    @Override
    public Map<Integer, Button> getFilteredButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        Iterator<IReport> iterator2 = this.reports.iterator();
        while (iterator2.hasNext()) {
            IReport report;
            IReportCategory reportCategory;
            IPunishmentLadder punishmentLadder;
            if ((reportCategory = (report = (IReport) iterator2.next()).getReportCategory()) != null
                    && (punishmentLadder = report.getPunishmentLadder()) != null
                    && this.passesFilter(player, report)) {
                entries.put(
                        entries.size(),
                        new ProfileReportsMenu.PermanentButton(
                                report, reportCategory, punishmentLadder));
            }
        }
        return entries;
    }

    @Override
    public List<PageFilter<IReport>> generateFilters(Player player) {
        ArrayList<PageFilter<IReport>> values;
        (values = new ArrayList())
                .add(new PageFilter<>("Open", result -> result.getStatus() == ReportStatus.OPEN));
        values.add(new PageFilter<>("In Progress", IReport::isInProgress));
        values.add(
                new PageFilter<>(
                        "Resolved", result -> result.getStatus() == ReportStatus.RESOLVED));
        values.add(new PageFilter<>("Punished", IReport::wasPunished));
        return values;
    }

    public ProfileReportsMenu(IProfile profile2, List<IReport> reports) {
        this.profile = profile2;
        this.reports = reports;
    }

    class PermanentButton extends Button {

        private IReport report;

        private IReportCategory reportCategory;

        private IPunishmentLadder punishmentLadder;

        @Override
        public ItemStack getItem(Player player) {
            List<xyz.refinedev.phoenix.profile.punishment.IPunishment> punishments =
                    ProfileReportsMenu.this.profile.getPunishments();
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    ItemUtils.parseFromX(this.reportCategory.getMaterialName())
                                            .parseMaterial()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.NAME)
                                    .replace(
                                            "<issuedAt>",
                                            DateUtils.getDate(this.report.getIssuedAt()))
                                    .replace("<target>", this.report.getNiceTarget())
                                    .replace("<reason>", this.report.getNiceReason()));
            IPunishmentLadderType punishmentLadderType;
            if ((punishmentLadderType = this.punishmentLadder.getNextLadder(punishments)) == null) {
                itemBuilder.addLoreLine(LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.NO_LADDER));
                return itemBuilder.toItemStack();
            }
            if (this.report.isResolved()) {
                if (this.report.wasPunished()) {
                    Iterator<String> iterator2 =
                            LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.PUNISHED_LORE).iterator();
                    while (iterator2.hasNext()) {
                        String text =
                                ((String) iterator2.next())
                                        .replace("<target>", this.report.getNiceTarget())
                                        .replace(
                                                "<status>",
                                                ReportStatusFormatter.tr(
                                                        player, this.report.getStatus()))
                                        .replace("<targetOn>", this.report.getTargetOn())
                                        .replace("<issuedBy>", this.report.getNiceIssuedBy())
                                        .replace("<issuedOn>", this.report.getIssuedOn())
                                        .replace("<reason>", this.report.getNiceReason())
                                        .replace(
                                                "<type>",
                                                punishmentLadderType.getType().getNiceName())
                                        .replace(
                                                "<time>",
                                                punishmentLadderType.getDuration() == -5L
                                                        ? "Permanent"
                                                        : DateUtils.formatTimeMillis(
                                                                punishmentLadderType.getDuration()))
                                        .replace("<resolvedBy>", this.report.getNiceResolvedBy())
                                        .replace(
                                                "<punished>",
                                                this.report.wasPunished() ? "&atrue" : "&cfalse");
                        itemBuilder.addLoreLine(text);
                    }
                } else {
                    Iterator<String> iterator3 =
                            LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.NOT_PUNISHED_LORE)
                                    .iterator();
                    while (iterator3.hasNext()) {
                        String text2 =
                                ((String) iterator3.next())
                                        .replace("<target>", this.report.getNiceTarget())
                                        .replace(
                                                "<status>",
                                                ReportStatusFormatter.tr(
                                                        player, this.report.getStatus()))
                                        .replace("<targetOn>", this.report.getTargetOn())
                                        .replace("<issuedBy>", this.report.getNiceIssuedBy())
                                        .replace("<issuedOn>", this.report.getIssuedOn())
                                        .replace("<reason>", this.report.getNiceReason())
                                        .replace(
                                                "<type>",
                                                punishmentLadderType.getType().getNiceName())
                                        .replace(
                                                "<time>",
                                                punishmentLadderType.getDuration() == -5L
                                                        ? "Permanent"
                                                        : DateUtils.formatTimeMillis(
                                                                punishmentLadderType.getDuration()))
                                        .replace("<resolvedBy>", this.report.getNiceResolvedBy())
                                        .replace(
                                                "<punished>",
                                                this.report.wasPunished() ? "&atrue" : "&cfalse");
                        itemBuilder.addLoreLine(text2);
                    }
                }
            } else if (this.report.isInProgress()) {
                Iterator<String> iterator4 =
                        LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.IN_PROGRESS_LORE).iterator();
                while (iterator4.hasNext()) {
                    String text3 =
                            ((String) iterator4.next())
                                    .replace("<target>", this.report.getNiceTarget())
                                    .replace(
                                            "<status>",
                                            ReportStatusFormatter.tr(
                                                    player, this.report.getStatus()))
                                    .replace("<targetOn>", this.report.getTargetOn())
                                    .replace("<issuedBy>", this.report.getNiceIssuedBy())
                                    .replace("<issuedOn>", this.report.getIssuedOn())
                                    .replace("<reason>", this.report.getNiceReason())
                                    .replace("<type>", punishmentLadderType.getType().getNiceName())
                                    .replace(
                                            "<time>",
                                            punishmentLadderType.getDuration() == -5L
                                                    ? "Permanent"
                                                    : DateUtils.formatTimeMillis(
                                                            punishmentLadderType.getDuration()))
                                    .replace("<progressBy>", this.report.getNiceResolvedBy());
                    itemBuilder.addLoreLine(text3);
                }
            } else {
                Iterator<String> iterator5 =
                        LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.OPEN_LORE).iterator();
                while (iterator5.hasNext()) {
                    String text4 =
                            ((String) iterator5.next())
                                    .replace("<target>", this.report.getNiceTarget())
                                    .replace(
                                            "<status>",
                                            ReportStatusFormatter.tr(
                                                    player, this.report.getStatus()))
                                    .replace("<targetOn>", this.report.getTargetOn())
                                    .replace("<issuedBy>", this.report.getNiceIssuedBy())
                                    .replace("<issuedOn>", this.report.getIssuedOn())
                                    .replace("<reason>", this.report.getNiceReason())
                                    .replace("<type>", punishmentLadderType.getType().getNiceName())
                                    .replace(
                                            "<time>",
                                            punishmentLadderType.getDuration() == -5L
                                                    ? "Permanent"
                                                    : DateUtils.formatTimeMillis(
                                                            punishmentLadderType.getDuration()));
                    itemBuilder.addLoreLine(text4);
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isRightClick()) {
                ProfileReportsMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(
                                new ReportProofMenu(
                                        this.report,
                                        ProfileReportsMenu.this.profile,
                                        ProfileReportsMenu.this.reports,
                                        false),
                                player);
            } else if (clickType == ClickType.DROP && this.report.isResolved()) {
                player.closeInventory();
                Phoenix.getInstance().getReportHandler().deleteReport(this.report);
            } else if (!this.report.isResolved()) {
                ProfileReportsMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(
                                new ReportPunishmentMenu(
                                        this.report,
                                        this.punishmentLadder,
                                        ProfileReportsMenu.this.profile,
                                        ProfileReportsMenu.this.profile.getPunishments(),
                                        ProfileReportsMenu.this.reports,
                                        false),
                                player);
            }
        }

        public PermanentButton(
                IReport report2,
                IReportCategory reportCategory2,
                IPunishmentLadder punishmentLadder2) {
            this.report = report2;
            this.reportCategory = reportCategory2;
            this.punishmentLadder = punishmentLadder2;
        }
    }
}

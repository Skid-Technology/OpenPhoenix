package xyz.refinedev.phoenix.menu.impl.report;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.api.button.BackButton;
import xyz.refinedev.phoenix.menu.impl.ReportsMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.ReportStatus;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ReportStatusFormatter;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReportPunishmentMenu extends Menu {

    private final IReport report;

    private final IPunishmentLadder punishmentLadder;

    private final IProfile profile;

    private final List<IPunishment> punishments;

    private final List<IReport> reports;

    private final boolean enabled;

    private static final String text = "<target>";

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.TITLE)
                .replace(text, this.report.getNiceTargetName());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        if (this.enabled) {
            entries.put(4, new BackButton(new ReportsMenu(this.reports)));
        } else {
            entries.put(4, new BackButton(new ProfileReportsMenu(this.profile, this.reports)));
        }
        entries.put(10, new ReportPunishmentMenu.StatusButton(ReportStatus.OPEN));
        entries.put(12, new ReportPunishmentMenu.StatusButton(ReportStatus.IN_PROGRESS));
        entries.put(14, new ReportPunishmentMenu.StatusButton(ReportStatus.RESOLVED));
        entries.put(16, new ReportPunishmentMenu.StatusButton(ReportStatus.PUNISHED));
        for (int index = 0; index < 27; index++) {
            entries.computeIfAbsent(index, result -> Button.placeholder());
        }
        return entries;
    }

    public ReportPunishmentMenu(
            IReport report2,
            IPunishmentLadder punishmentLadder2,
            IProfile profile2,
            List<IPunishment> punishments,
            List<IReport> reports,
            boolean enabled) {
        this.report = report2;
        this.punishmentLadder = punishmentLadder2;
        this.profile = profile2;
        this.punishments = punishments;
        this.reports = reports;
        this.enabled = enabled;
    }

    public class StatusButton extends Button {

        private final ReportStatus reportStatus;

        private static final long timeTime = -5L;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    ReportStatusFormatter.ordinalByCounts(this.reportStatus)))
                    .setDurability(
                            ReportStatusFormatter.ordinalByCounts(this.reportStatus).getData());
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.STATUS_BUTTON.NAME)
                            .replace(
                                    "<status>",
                                    ReportStatusFormatter.tr(player, this.reportStatus)));
            if (this.reportStatus == ReportStatus.PUNISHED) {
                IPunishmentLadderType punishmentLadderType;
                if ((punishmentLadderType =
                                ReportPunishmentMenu.this
                                        .report
                                        .getPunishmentLadder()
                                        .getNextLadder(ReportPunishmentMenu.this.punishments))
                        == null) {
                    itemBuilder.addLoreLine(
                            LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.NO_LADDER));
                    return itemBuilder.toItemStack();
                }
                Iterator<String> iterator2 =
                        LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.STATUS_BUTTON.PUNISH_LORE)
                                .iterator();
                while (iterator2.hasNext()) {
                    String text =
                            ((String) iterator2.next())
                                    .replace(
                                            "<reason>",
                                            ReportPunishmentMenu.this
                                                    .report
                                                    .getReportCategory()
                                                    .getDisplayName())
                                    .replace("<type>", punishmentLadderType.getType().getNiceName())
                                    .replace(
                                            "<time>",
                                            punishmentLadderType.getDuration() == timeTime
                                                    ? "Permanent"
                                                    : DateUtils.formatTimeMillis(
                                                            punishmentLadderType.getDuration()))
                                    .replace(
                                            "<player>",
                                            ReportPunishmentMenu.this.report.getNiceTarget());
                    itemBuilder.addLoreLine(text);
                }
            } else {
                Iterator<String> iterator3 =
                        LocaleUtil.tr(player, MenuConfig.REPORTS_MENU.STATUS_BUTTON.LORE)
                                .iterator();
                while (iterator3.hasNext()) {
                    String text2 =
                            ((String) iterator3.next())
                                    .replace(
                                            "<player>",
                                            ReportPunishmentMenu.this.report.getNiceTarget())
                                    .replace(
                                            "<status>",
                                            ReportStatusFormatter.tr(player, this.reportStatus));
                    itemBuilder.addLoreLine(text2);
                }
            }
            if (ReportPunishmentMenu.this.report.getStatus() == this.reportStatus) {
                itemBuilder.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (this.reportStatus == ReportStatus.PUNISHED) {
                player.closeInventory();
            }
            Phoenix.getInstance()
                    .getReportHandler()
                    .applyPlayer(
                            player,
                            ReportPunishmentMenu.this.report,
                            ReportPunishmentMenu.this.punishmentLadder,
                            ReportPunishmentMenu.this.profile,
                            this.reportStatus);
        }

        public StatusButton(ReportStatus reportStatus2) {
            this.reportStatus = reportStatus2;
        }
    }
}

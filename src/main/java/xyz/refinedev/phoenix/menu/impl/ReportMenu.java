package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReportMenu extends PaginatedMenu {

    private final IProfile profile;

    private final boolean enabled;

    private static final String text = "<target>";

    @Override
    public String getTitle(Player player) {
        return Color.translate(
                LocaleUtil.tr(player, MenuConfig.REPORT_MENU.TITLE)
                        .replace(
                                text,
                                this.enabled
                                        ? this.profile.getNameWithColor()
                                        : this.profile.getNameWithColorNoDisguise()));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        Iterator<IReportCategory> iterator2 =
                Phoenix.getInstance()
                        .getReportCategoryHandler()
                        .getRepository()
                        .getCache()
                        .iterator();
        while (iterator2.hasNext()) {
            IReportCategory reportCategory;
            if ((reportCategory = (IReportCategory) iterator2.next()).getPunishmentLadder()
                    != null) {
                entries.put(entries.size(), new ReportMenu.EntryButton(reportCategory));
            }
        }
        if (SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED) {
            entries.put(entries.size(), new ReportMenu.TargetButton());
        }
        return entries;
    }

    public ReportMenu(IProfile profile2, boolean enabled) {
        this.profile = profile2;
        this.enabled = enabled;
    }

    class EntryButton extends Button {

        private final IReportCategory reportCategory;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    ItemUtils.parseFromX(this.reportCategory.getMaterialName())
                                            .parseMaterial()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.REPORT_MENU.ITEM_NAME)
                                    .replace("<category>", this.reportCategory.getDisplayName()));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.REPORT_MENU.ITEM_LORE).iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()).contains("<description>")) {
                    for (String text2 : this.reportCategory.getDescription()) {
                        itemBuilder.addLoreLine(text2);
                    }
                } else {
                    itemBuilder.addLoreLine(
                            text.replace("<target>", ReportMenu.this.profile.getNameWithColor())
                                    .replace("<category>", this.reportCategory.getDisplayName()));
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            Phoenix.getInstance()
                    .getReportHandler()
                    .applyPlayer(player, ReportMenu.this.profile, this.reportCategory);
        }

        public EntryButton(IReportCategory reportCategory2) {
            this.reportCategory = reportCategory2;
        }
    }

    class TargetButton extends Button {

        private static final String text = "<target>";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    ItemUtils.parseFromX(MenuConfig.REPORT_MENU.CUSTOM_MATERIAL)))
                    .setName(LocaleUtil.tr(player, MenuConfig.REPORT_MENU.CUSTOM_ITEM_NAME));
            for (String text2 : LocaleUtil.tr(player, MenuConfig.REPORT_MENU.CUSTOM_ITEM_LORE)) {
                itemBuilder.addLoreLine(
                        text2.replace(text, ReportMenu.this.profile.getNameWithColor()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            Phoenix.getInstance().getReportHandler().sendMessage(player, ReportMenu.this.profile);
        }

        public TargetButton() {}
    }
}

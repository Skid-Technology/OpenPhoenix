package xyz.refinedev.phoenix.menu.impl.report;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportCategoryMenu extends Menu {

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
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        List<IReportCategory> values =
                Phoenix.getInstance().getReportCategoryHandler().getRepository().getCache().stream()
                        .filter(result -> result.getPunishmentLadder() != null)
                        .collect(Collectors.toList());
        int value = 0;
        for (int index = 0; index < MenuConfig.REPORT_MENU.LAYOUT.size(); index++) {
            char[] values2 = MenuConfig.REPORT_MENU.LAYOUT.get(index).toCharArray();
            for (int index2 = 0; index2 < values2.length; index2++) {
                char value2 = values2[index2];
                int value3 = index * 9 + index2;
                switch (value2) {
                    case '#':
                        entries.put(value3, Button.placeholder());
                        break;
                    case 'c':
                        entries.put(value3, new ReportCategoryMenu.TargetButton());
                        break;
                    case 'x':
                        IReportCategory reportCategory;
                        if ((reportCategory =
                                        values.size() > value
                                                ? (IReportCategory) values.get(value)
                                                : null)
                                == null) {
                            if (SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED
                                    && value == values.size()) {
                                entries.put(value3, new ReportCategoryMenu.EntryButton());
                                value++;
                            }
                        } else {
                            entries.put(
                                    value3, new ReportCategoryMenu.CategoryButton(reportCategory));
                            value++;
                        }
                }
            }
        }
        return entries;
    }

    public ReportCategoryMenu(IProfile profile2, boolean enabled) {
        this.profile = profile2;
        this.enabled = enabled;
    }

    class CategoryButton extends Button {

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
                            text.replace(
                                            "<target>",
                                            ReportCategoryMenu.this.profile.getNameWithColor())
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
                    .applyPlayer(player, ReportCategoryMenu.this.profile, this.reportCategory);
        }

        public CategoryButton(IReportCategory reportCategory2) {
            this.reportCategory = reportCategory2;
        }
    }

    class EntryButton extends Button {

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
                        text2.replace(text, ReportCategoryMenu.this.profile.getNameWithColor()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            Phoenix.getInstance()
                    .getReportHandler()
                    .sendMessage(player, ReportCategoryMenu.this.profile);
        }

        public EntryButton() {}
    }

    class TargetButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem()))
                    .setSkullOwner(ReportCategoryMenu.this.profile.getPlayerName());
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.REPORT_MENU.CLOSE_ITEM_NAME));
            for (String text : LocaleUtil.tr(player, MenuConfig.REPORT_MENU.CLOSE_ITEM_LORE)) {
                itemBuilder.addLoreLine(text);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
        }

        public TargetButton() {}
    }
}

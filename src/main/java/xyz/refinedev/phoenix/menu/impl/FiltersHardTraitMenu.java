package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.button.BooleanTraitButton;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.filter.FilterType;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FiltersHardTraitMenu extends PaginatedMenu {

    private final IFilter filter;

    private static final String text = "<filter>";

    @Override
    public void onClose(Player player) {
        this.initialize();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(0, new FiltersHardTraitMenu.EntryButton());
        entries.put(entries.size(), new FiltersHardTraitMenu.NoneButton());
        List<String> values = LocaleUtil.tr(player, MenuConfig.FILTERS.HARD_TRAIT.DESCRIPTION);
        List<String> values2 =
                LocaleUtil.tr(player, MenuConfig.FILTERS.AUTO_PUNISH_TRAIT.DESCRIPTION);
        entries.put(
                entries.size(),
                new BooleanTraitButton<>(
                        this.filter,
                        LocaleUtil.tr(player, MenuConfig.FILTERS.HARD_TRAIT.NAME),
                        values,
                        ItemUtils.parseFromX(MenuConfig.FILTERS.HARD_TRAIT.MATERIAL).get(),
                        IFilter::setHard,
                        IFilter::isHard,
                        () -> {}));
        entries.put(
                entries.size(),
                new BooleanTraitButton<>(
                        this.filter,
                        LocaleUtil.tr(player, MenuConfig.FILTERS.AUTO_PUNISH_TRAIT.NAME),
                        values2,
                        ItemUtils.parseFromX(MenuConfig.FILTERS.AUTO_PUNISH_TRAIT.MATERIAL).get(),
                        IFilter::setAutoPunish,
                        IFilter::isAutoPunish,
                        () -> {}));
        return entries;
    }

    public void initialize() {
        super.api.getFilterHandler().updateFilter(this.filter);
    }

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.FILTERS.TITLE).replace(text, this.filter.getName());
    }

    public FiltersHardTraitMenu(IFilter filter2) {
        this.filter = filter2;
    }

    class EntryButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.FILTERS.TYPE.MATERIAL))
                    .setName(LocaleUtil.tr(player, MenuConfig.FILTERS.TYPE.NAME));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.FILTERS.TYPE.DESCRIPTION).iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()).contains("<types>")) {
                    FilterType[] filterTypes;
                    int value = (filterTypes = FilterType.values()).length;
                    for (int index = 0; index < value; index++) {
                        FilterType filterType = filterTypes[index];
                        itemBuilder.addLoreLine(
                                FiltersHardTraitMenu.this.filter.getFilterType() == filterType
                                        ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.FILTERS.TYPE.FORMAT_SELECTED)
                                                .replace("<type>", filterType.getDisplayName())
                                        : LocaleUtil.tr(player, MenuConfig.FILTERS.TYPE.FORMAT)
                                                .replace("<type>", filterType.getDisplayName()));
                    }
                } else {
                    itemBuilder.addLoreLine(text);
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            switch (FiltersHardTraitMenu.this.filter.getFilterType()) {
                case WORD:
                    FiltersHardTraitMenu.this.filter.setFilterType(FilterType.REGEX);
                    break;
                case REGEX:
                    FiltersHardTraitMenu.this.filter.setFilterType(FilterType.CONTAINS);
                    break;
                case CONTAINS:
                    FiltersHardTraitMenu.this.filter.setFilterType(FilterType.WORD);
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public EntryButton() {}
    }

    class NoneButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.FILTERS.PUNISH_LADDER.MATERIAL))
                    .setName(LocaleUtil.tr(player, MenuConfig.FILTERS.PUNISH_LADDER.NAME));
            for (String text :
                    LocaleUtil.tr(player, MenuConfig.FILTERS.PUNISH_LADDER.DESCRIPTION)) {
                itemBuilder.addLoreLine(
                        text.replace(
                                "<current>",
                                FiltersHardTraitMenu.this.filter.hasPunishmentLadder()
                                        ? FiltersHardTraitMenu.this
                                                .filter
                                                .getPunishmentLadder()
                                                .getReason()
                                        : "None"));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            FiltersHardTraitMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(new FiltersMenu(FiltersHardTraitMenu.this.filter), player);
        }

        public NoneButton() {}
    }
}

package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class FiltersMenu extends PaginatedMenu {

    private final IFilter filter;

    private static final String text = "<filter>";

    @Override
    public void onClose(Player player) {
        this.initialize();
        if (!this.isClosedByMenu()) {
            this.back(player);
        }
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IPunishmentLadder punishmentLadder :
                super.api.getPunishmentLadderHandler().getSortedLadders()) {
            entries.put(entries.size(), new FiltersMenu.EntryButton(punishmentLadder));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    public void initialize() {
        super.api.getFilterHandler().updateFilter(this.filter);
    }

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.FILTERS.TITLE).replace(text, this.filter.getName());
    }

    public FiltersMenu(IFilter filter2) {
        this.filter = filter2;
    }

    class EntryButton extends Button {

        private final IPunishmentLadder punishmentLadder;

        private static final String text = "<ladder>";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.FILTERS.PUNISH_LADDER.LADDER_FORMAT)
                                    .replace(text, this.punishmentLadder.getReason()));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            FiltersMenu.this.filter.setPunishmentLadderId(this.punishmentLadder.getId());
            FiltersMenu.super.api.getFilterHandler().updateFilter(FiltersMenu.this.filter);
            FiltersMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(new FiltersHardTraitMenu(FiltersMenu.this.filter), player);
        }

        public EntryButton(IPunishmentLadder punishmentLadder2) {
            this.punishmentLadder = punishmentLadder2;
        }
    }
}

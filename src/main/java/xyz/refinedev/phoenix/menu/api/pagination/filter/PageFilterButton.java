package xyz.refinedev.phoenix.menu.api.pagination.filter;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.Iterator;
import java.util.List;

public class PageFilterButton<T> extends Button {

    private FilterablePaginatedMenu<T> menu;

    @Override
    public ItemStack getItem(Player player) {
        List<xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilter<T>> values;
        if ((values = this.menu.getFilters(player)) != null && !values.isEmpty()) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.GENERAL.FILTERS_SLOT.MATERIAL))
                    .setName(LocaleUtil.tr(player, MenuConfig.GENERAL.FILTERS_SLOT.NAME));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.GENERAL.FILTERS_SLOT.LORE).iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()).contains("<filters>")) {
                    for (xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilter<T> pageFilter :
                            values) {
                        boolean matches =
                                ((PageFilter) values.get(this.menu.getScrollIndex()))
                                        .equals(pageFilter);
                        String text2 =
                                (pageFilter.isEnabled()
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.GENERAL
                                                                .FILTERS_SLOT
                                                                .ENABLED_FILTER)
                                                : LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.GENERAL
                                                                .FILTERS_SLOT
                                                                .DISABLED_FILTER))
                                        .replace("<filter>", pageFilter.getName());
                        if (matches) {
                            text2 =
                                    LocaleUtil.tr(player, MenuConfig.GENERAL.FILTERS_SLOT.SELECTOR)
                                            + text2;
                        }
                        itemBuilder.addLoreLine(text2);
                    }
                } else {
                    itemBuilder.addLoreLine(text);
                }
            }
            return itemBuilder.toItemStack();
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        List<xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilter<T>> values;
        if ((values = this.menu.getFilters(player)) == null || values.isEmpty()) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MenuConfig.FILTERS.NO_FILTERS)));
        } else if (clickType != ClickType.MIDDLE && !clickType.isShiftClick()) {
            int size2 = values.size();
            int value = clickType.isRightClick() ? -1 : 1;
            int value2;
            value2 =
                    (value2 = this.menu.getScrollIndex() + value) >= size2
                            ? 0
                            : (value2 < 0 ? size2 - 1 : value2);
            this.menu.setScrollIndex(value2);
        } else {
            PageFilter pageFilter = (PageFilter) values.get(this.menu.getScrollIndex());
            pageFilter.setEnabled(!pageFilter.isEnabled());
        }
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }

    public PageFilterButton(FilterablePaginatedMenu<T> filterablePaginatedMenu) {
        this.menu = filterablePaginatedMenu;
    }
}

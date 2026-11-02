package xyz.refinedev.phoenix.menu.api.pagination;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

public class NextPageSlot extends Button {

    private final PaginatedMenu menu;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder itemBuilder;
        (itemBuilder = new ItemBuilder(MenuConfig.GENERAL.NEXT_PAGE_SLOT.MATERIAL))
                .setName(
                        LocaleUtil.tr(player, MenuConfig.GENERAL.NEXT_PAGE_SLOT.NAME)
                                .replace("<page>", "" + (this.menu.getPage() + 1))
                                .replace("<pages>", "" + this.menu.getPages(player)));
        itemBuilder.setDurability(MenuConfig.GENERAL.NEXT_PAGE_SLOT.DURABILITY);
        itemBuilder.setLore(LocaleUtil.tr(player, MenuConfig.GENERAL.NEXT_PAGE_SLOT.LORE));
        return MenuConfig.GENERAL.NEXT_PAGE_SLOT.HEAD
                ? Reflection.itemWithBase64(
                        itemBuilder.toItemStack(),
                        LocaleUtil.tr(player, MenuConfig.GENERAL.NEXT_PAGE_SLOT.HEAD_TEXTURE))
                : itemBuilder.toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            ViewAllPagesMenu viewAllPagesMenu = new ViewAllPagesMenu(this.menu);
            if (this.menu.isFullyAsync()) {
                Phoenix.getInstance().getMenuHandler().openMenuAsync(viewAllPagesMenu, player);
            } else {
                Phoenix.getInstance().getMenuHandler().openMenu(viewAllPagesMenu, player);
            }
            Button.playNeutral(player);
        } else if (this.hasNext(player)) {
            this.menu.modPage(player, 1);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }
    }

    private boolean hasNext(Player player) {
        int value;
        return (value = this.menu.getPage() + 1) > 0 && this.menu.getPages(player) >= value;
    }

    public NextPageSlot(PaginatedMenu paginatedMenu) {
        this.menu = paginatedMenu;
    }
}

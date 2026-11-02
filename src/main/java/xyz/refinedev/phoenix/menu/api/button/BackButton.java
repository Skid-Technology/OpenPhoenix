package xyz.refinedev.phoenix.menu.api.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

public class BackButton extends Button {

    private final Menu back;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(MenuConfig.GENERAL.BACK_SLOT.MATERIAL)
                .setName(LocaleUtil.tr(player, MenuConfig.GENERAL.BACK_SLOT.NAME))
                .setDurability(MenuConfig.GENERAL.BACK_SLOT.DURABILITY)
                .setLore(LocaleUtil.tr(player, MenuConfig.GENERAL.BACK_SLOT.LORE))
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (this.back != null) {
            Button.playNeutral(player);
            if (this.back.isFullyAsync()) {
                Phoenix.getInstance().getMenuHandler().openMenuAsync(this.back, player);
            } else {
                Phoenix.getInstance().getMenuHandler().openMenu(this.back, player);
            }
        }
    }

    public BackButton(Menu menu) {
        this.back = menu;
    }
}

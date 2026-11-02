package xyz.refinedev.phoenix.menu.api.pagination;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.utils.XMaterial;

import java.util.ArrayList;

public class JumpToPageButton extends Button {

    private final int page;

    private final PaginatedMenu menu;

    private final boolean current;

    @Override
    public ItemStack getItem(Player player) {
        ItemStack itemStack;
        ItemMeta itemMeta;
        (itemMeta =
                        (itemStack =
                                        new ItemStack(
                                                this.current
                                                        ? XMaterial.ENCHANTED_BOOK.get()
                                                        : XMaterial.BOOK.get(),
                                                this.page))
                                .getItemMeta())
                .setDisplayName(ChatColor.YELLOW + "Page " + this.page);
        ArrayList<String> values;
        (values = new ArrayList()).add(ChatColor.GRAY + "Click to open");
        if (this.current) {
            values.add(ChatColor.GRAY.toString());
            values.add(ChatColor.GREEN + "Current page");
        }
        itemMeta.setLore(values);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        this.menu.modPage(player, this.page - this.menu.getPage());
        Button.playNeutral(player);
    }

    public JumpToPageButton(int page, PaginatedMenu paginatedMenu, boolean current) {
        this.page = page;
        this.menu = paginatedMenu;
        this.current = current;
    }
}

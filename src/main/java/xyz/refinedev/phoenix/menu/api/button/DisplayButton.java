package xyz.refinedev.phoenix.menu.api.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.menu.api.Button;

public class DisplayButton extends Button {

    private final ItemStack itemStack;

    private final boolean cancel;

    @Override
    public ItemStack getItem(Player player) {
        return this.itemStack == null ? new ItemStack(Material.AIR) : this.itemStack;
    }

    @Override
    public boolean shouldCancel(Player player, ClickType clickType) {
        return this.cancel;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public boolean isCancel() {
        return this.cancel;
    }

    public DisplayButton(ItemStack itemStack2, boolean cancel) {
        this.itemStack = itemStack2;
        this.cancel = cancel;
    }
}

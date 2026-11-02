package xyz.refinedev.phoenix.menu.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.refinedev.lib.xseries.XSound;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.List;

public abstract class Button {

    public static Button placeholder(
            final Material material, final byte value, final String... text) {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                ItemStack itemStack;
                ItemMeta itemMeta;
                (itemMeta = (itemStack = new ItemStack(material, 1, value)).getItemMeta())
                        .setDisplayName(String.join(" ", text));
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }
        };
    }

    public static Button placeholder(
            final Material material,
            final byte value,
            final String text,
            final List<String> values) {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                ItemStack itemStack;
                ItemMeta itemMeta;
                (itemMeta = (itemStack = new ItemStack(material, 1, value)).getItemMeta())
                        .setDisplayName(text);
                itemMeta.setLore(values);
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }
        };
    }

    public static Button placeholder() {
        return new Button() {

            private static final String text = "&7";

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(MenuConfig.GENERAL.FILL_SLOT.MATERIAL)
                        .setName(text)
                        .setDurability(MenuConfig.GENERAL.FILL_SLOT.DURABILITY)
                        .toItemStack();
            }
        };
    }

    public static Button placeholder(final ItemStack itemStack) {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                return itemStack;
            }
        };
    }

    public static Button placeholder(final Material material) {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemStack(material);
            }
        };
    }

    public static void playFail(Player player) {
        XSound.BLOCK_GRASS_BREAK.play(player, 20.0F, 1.0F);
    }

    public static void playSuccess(Player player) {
        XSound.BLOCK_NOTE_BLOCK_HARP.play(player, 20.0F, 15.0F);
    }

    public static void playNeutral(Player player) {
        XSound.UI_BUTTON_CLICK.play(player, 20.0F, 1.0F);
    }

    public abstract ItemStack getItem(Player player);

    public void clicked(Player player, ClickType clickType) {}

    public void clicked(Player player, int value, ClickType clickType, int value2) {}

    public boolean shouldCancel(Player player, ClickType clickType) {
        return true;
    }

    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}

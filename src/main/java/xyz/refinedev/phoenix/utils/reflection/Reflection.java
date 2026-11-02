package xyz.refinedev.phoenix.utils.reflection;

import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;

public abstract class Reflection {

    public static ItemStack itemWithBase64(ItemStack itemStack, String text) {
        return !Phoenix.getInstance().getNmsHandler().isRegistered()
                ? itemStack
                : Phoenix.getInstance().getNmsHandler().getAPI().itemWithBase64(itemStack, text);
    }
}

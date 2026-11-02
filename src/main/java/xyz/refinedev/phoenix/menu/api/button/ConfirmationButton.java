package xyz.refinedev.phoenix.menu.api.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XSound;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.api.TypeCallback;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

public class ConfirmationButton extends Button {

    private final boolean confirm;

    private final TypeCallback<Boolean> callback;

    private final boolean closeAfterResponse;

    private final Menu menu;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder itemBuilder;
        (itemBuilder = new ItemBuilder(XMaterial.WHITE_TERRACOTTA.get()))
                .setName(Color.translate(this.confirm ? "&a&lConfirm" : "&c&lCancel"));
        itemBuilder.setDurability(this.confirm ? 5 : 14);
        return itemBuilder.toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (this.confirm) {
            player.playSound(
                    player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HARP.parseSound(), 20.0F, 0.1F);
        } else {
            player.playSound(
                    player.getLocation(), XSound.BLOCK_GRAVEL_BREAK.parseSound(), 20.0F, 0.1F);
        }
        if (this.closeAfterResponse) {
            this.menu.setClosedByMenu(true);
            player.closeInventory();
        }
        this.callback.callback(this.confirm);
    }

    public ConfirmationButton(
            boolean confirm,
            TypeCallback<Boolean> typeCallback,
            boolean closeAfterResponse,
            Menu menu2) {
        this.confirm = confirm;
        this.callback = typeCallback;
        this.closeAfterResponse = closeAfterResponse;
        this.menu = menu2;
    }
}

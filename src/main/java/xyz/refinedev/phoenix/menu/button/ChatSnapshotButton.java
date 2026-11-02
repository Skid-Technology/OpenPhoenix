package xyz.refinedev.phoenix.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

public class ChatSnapshotButton extends Button {

    private final IChatSnapshot chatSnapshot;

    private final boolean enabled;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder itemBuilder;
        (itemBuilder = new ItemBuilder(XMaterial.BOOK.get()))
                .setName(
                        LocaleUtil.tr(player, MenuConfig.CHATSNAPSHOT_MENU.SNAPSHOT_NAME)
                                .replace("<requestedBy>", this.chatSnapshot.getNiceRequestedBy())
                                .replace("<id>", this.chatSnapshot.getNiceId())
                                .replace("<time>", this.chatSnapshot.getNiceCreatedOn()));
        if (this.enabled) {
            for (String text :
                    LocaleUtil.tr(player, MenuConfig.CHATSNAPSHOT_MENU.OWN_SNAPSHOT_LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<requestedBy>", this.chatSnapshot.getNiceRequestedBy())
                                .replace("<id>", this.chatSnapshot.getNiceId())
                                .replace("<time>", this.chatSnapshot.getNiceCreatedOn()));
            }
        } else {
            for (String text2 : LocaleUtil.tr(player, MenuConfig.CHATSNAPSHOT_MENU.SNAPSHOT_LORE)) {
                itemBuilder.addLoreLine(
                        text2.replace("<requestedBy>", this.chatSnapshot.getNiceRequestedBy())
                                .replace("<id>", this.chatSnapshot.getNiceId())
                                .replace("<time>", this.chatSnapshot.getNiceCreatedOn()));
            }
        }
        return itemBuilder.toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();
        player.chat("/chatsnapshot view " + this.chatSnapshot.getNiceId());
    }

    public ChatSnapshotButton(IChatSnapshot chatSnapshot2, boolean enabled) {
        this.chatSnapshot = chatSnapshot2;
        this.enabled = enabled;
    }
}

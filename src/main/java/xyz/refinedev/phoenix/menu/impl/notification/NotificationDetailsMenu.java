package xyz.refinedev.phoenix.menu.impl.notification;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class NotificationDetailsMenu extends Menu {

    private final INotification notification;

    private final int count;

    private final int countCount;

    private static final String title = "Notifications";

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap())
                .put(4, new NotificationDetailsMenu.EntryButton(this.notification));
        return entries;
    }

    public NotificationDetailsMenu(INotification notification2, int count, int countCount) {
        this.notification = notification2;
        this.count = count;
        this.countCount = countCount;
    }

    class EntryButton extends Button {

        private final INotification notification;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(Material.PAPER)).setName(this.notification.getTitle());
            for (String text : LocaleUtil.tr(player, MenuConfig.NOTIFICATIONS_MENU.STATS_LORE)) {
                itemBuilder.addLoreLine(
                        text.replace(
                                        "<views>",
                                        String.valueOf(NotificationDetailsMenu.this.countCount))
                                .replace(
                                        "<reads>",
                                        String.valueOf(NotificationDetailsMenu.this.count)));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {}

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public EntryButton(INotification notification2) {
            this.notification = notification2;
        }
    }
}

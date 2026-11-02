package xyz.refinedev.phoenix.menu.impl.notification;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NotificationListMenu extends PaginatedMenu {

    private final List<INotification> notifications;

    private final boolean enabled;

    private static final String title = "Notifications";

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (INotification notification : this.notifications) {
            entries.put(entries.size(), new NotificationListMenu.EntryButton(notification));
            if (this.enabled) {
                super.api.getNotificationHandler().markAsViewed(notification, player.getUniqueId());
            }
        }
        return entries;
    }

    public NotificationListMenu(List<INotification> notifications, boolean enabled) {
        this.notifications = notifications;
        this.enabled = enabled;
    }

    class EntryButton extends Button {

        private final INotification notification;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(Material.PAPER)).setName(this.notification.getTitle());
            if (NotificationListMenu.this.enabled) {
                Iterator<String> iterator2 =
                        LocaleUtil.tr(player, MenuConfig.NOTIFICATIONS_MENU.UNREAD_LORE).iterator();
                while (iterator2.hasNext()) {
                    String text;
                    if ((text = (String) iterator2.next()).contains("<notification>")) {
                        for (String text2 : this.notification.getDescription()) {
                            itemBuilder.addLoreLine(text2);
                        }
                    } else {
                        itemBuilder.addLoreLine(text);
                    }
                }
            } else {
                Iterator<String> iterator3 =
                        LocaleUtil.tr(player, MenuConfig.NOTIFICATIONS_MENU.LORE).iterator();
                while (iterator3.hasNext()) {
                    String text3;
                    if ((text3 = (String) iterator3.next()).contains("<notification>")) {
                        for (String text4 : this.notification.getDescription()) {
                            itemBuilder.addLoreLine(text4);
                        }
                    } else {
                        itemBuilder.addLoreLine(text3);
                    }
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (NotificationListMenu.this.enabled) {
                NotificationListMenu.super
                        .api
                        .getNotificationHandler()
                        .markAsRead(this.notification, player.getUniqueId());
                NotificationListMenu.this.notifications.remove(this.notification);
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public EntryButton(INotification notification2) {
            this.notification = notification2;
        }
    }
}

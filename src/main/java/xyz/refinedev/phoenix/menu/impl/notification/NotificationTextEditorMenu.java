package xyz.refinedev.phoenix.menu.impl.notification;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.menu.api.menus.TextEditorMenu;
import xyz.refinedev.phoenix.notification.INotification;

import java.util.LinkedList;
import java.util.List;

public class NotificationTextEditorMenu extends TextEditorMenu {

    private final INotification notification;

    public NotificationTextEditorMenu(INotification notification2) {
        super(new LinkedList<>(notification2.getDescription()));
        this.notification = notification2;
    }

    @Override
    public void onSave(Player player, List<String> values) {
        player.closeInventory();
        this.notification.setDescription(values);
        super.api.getNotificationHandler().saveNotification(this.notification);
    }
}

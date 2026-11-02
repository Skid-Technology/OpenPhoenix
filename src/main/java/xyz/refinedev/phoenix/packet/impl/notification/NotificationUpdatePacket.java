package xyz.refinedev.phoenix.packet.impl.notification;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.INotificationHandler;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;

public class NotificationUpdatePacket extends EntityUpdatePacket<INotification> {

    public NotificationUpdatePacket(INotification notification, boolean matches) {
        super(notification, matches);
    }

    protected INotification find(INotification notification) {
        return handler().getFromCache(notification.getId());
    }

    protected void add(INotification notification) {
        handler().getRepository().addToCache(notification);
    }

    protected void remove(INotification notification) {
        handler().getRepository().removeFromCache(notification);
    }

    private static INotificationHandler handler() {
        return PlatformGetter.getInstance().getNotificationHandler();
    }

    public NotificationUpdatePacket() {}
}

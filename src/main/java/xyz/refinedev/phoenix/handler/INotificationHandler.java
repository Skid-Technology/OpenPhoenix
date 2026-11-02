package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.repository.INotificationRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface INotificationHandler {

    void saveNotification(INotification notification);

    void deleteNotification(INotification notification);

    CompletableFuture<List<INotification>> getUnreadByPlayer(UUID uUID);

    CompletableFuture<List<INotification>> getReadByPlayer(UUID uUID);

    Set<String> getReadsForNotification(INotification notification);

    Set<String> getViewsForNotification(INotification notification);

    void markAsRead(INotification notification, UUID uUID);

    void markAsUnread(INotification notification, UUID uUID);

    void markAsViewed(INotification notification, UUID uUID);

    @Nullable
    INotification getFromCache(String text);

    INotificationRepository getRepository();
}

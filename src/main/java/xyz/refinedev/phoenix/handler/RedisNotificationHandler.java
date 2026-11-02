package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.packet.impl.notification.NotificationUpdatePacket;
import xyz.refinedev.phoenix.repository.INotificationRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisNotificationHandler implements INotificationHandler {

    private final CommonPlatform platform;

    private final INotificationRepository repository;

    public RedisNotificationHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).notifications();
        this.repository
                .fetchAll()
                .whenCompleteAsync(
                        (result, result2) ->
                                this.repository
                                        .getCache()
                                        .addAll((Collection<? extends INotification>) result));
    }

    @Override
    public void saveNotification(INotification notification) {
        this.repository.save(notification);
        this.platform.getPacketHandler().send(new NotificationUpdatePacket(notification, false));
    }

    @Override
    public void deleteNotification(INotification notification) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.runRedisCommandAsync(
                result -> {
                    result.del(new String[] {"nviews:" + notification.getId()});
                    return result.del(new String[] {"nreads:" + notification.getId()});
                });
        this.repository.delete(notification);
        this.platform.getPacketHandler().send(new NotificationUpdatePacket(notification, true));
    }

    @Override
    public CompletableFuture<List<INotification>> getUnreadByPlayer(UUID uUID) {
        return CompletableFuture.supplyAsync(
                () -> {
                    ArrayList<INotification> values = new ArrayList<>();
                    String text = uUID.toString();
                    for (INotification notification : this.repository.getCache()) {
                        if (!this.getReadsForNotification(notification).contains(text)) {
                            values.add(notification);
                        }
                    }
                    return values;
                });
    }

    @Override
    public CompletableFuture<List<INotification>> getReadByPlayer(UUID uUID) {
        return CompletableFuture.supplyAsync(
                () -> {
                    ArrayList<INotification> values = new ArrayList<>();
                    String text = uUID.toString();
                    for (INotification notification : this.repository.getCache()) {
                        if (this.getReadsForNotification(notification).contains(text)) {
                            values.add(notification);
                        }
                    }
                    return values;
                });
    }

    @Override
    public Set<String> getReadsForNotification(INotification notification) {
        return this.findSet("nreads:" + notification.getId());
    }

    @Override
    public Set<String> getViewsForNotification(INotification notification) {
        return this.findSet("nviews:" + notification.getId());
    }

    private Set<String> findSet(String text) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        List<String> values;
        return (values = databaseHandler.runRedisCommand(result -> result.hkeys(text))) == null
                ? new HashSet<>()
                : new HashSet<>(values);
    }

    @Override
    public void markAsRead(INotification notification, UUID uUID) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> result.hset("nreads:" + notification.getId(), uUID.toString(), ""));
    }

    @Override
    public void markAsUnread(INotification notification, UUID uUID) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result ->
                        result.hdel(
                                "nreads:" + notification.getId(), new String[] {uUID.toString()}));
    }

    @Override
    public void markAsViewed(INotification notification, UUID uUID) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> result.hset("nviews:" + notification.getId(), uUID.toString(), ""));
    }

    @Nullable
    @Override
    public INotification getFromCache(String text) {
        return this.repository.getFromCache(text).orElse(null);
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public INotificationRepository getRepository() {
        return this.repository;
    }
}

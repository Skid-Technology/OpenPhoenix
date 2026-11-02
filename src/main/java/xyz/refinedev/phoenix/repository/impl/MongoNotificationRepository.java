package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.notification.Notification;
import xyz.refinedev.phoenix.repository.INotificationRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoNotificationRepository extends MongoListRepository<String, INotification>
        implements INotificationRepository {

    public MongoNotificationRepository(MongoCollection<Document> mongoCollection) {
        super(Notification.class, mongoCollection, INotification::getId);
    }
}

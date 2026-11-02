package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.notification.Notification;
import xyz.refinedev.phoenix.repository.INotificationRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLNotificationRepository extends MySQLListRepository<String, INotification>
        implements INotificationRepository {

    public MySQLNotificationRepository(Table table) {
        super(Notification.class, table, INotification::getId);
    }
}

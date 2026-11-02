package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.chatsnapshot.ChatSnapshot;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.repository.IChatSnapshotRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

import java.util.UUID;

public class MySQLChatSnapshotRepository extends MySQLListRepository<UUID, IChatSnapshot>
        implements IChatSnapshotRepository {

    public MySQLChatSnapshotRepository(Table table) {
        super(ChatSnapshot.class, table, IChatSnapshot::getUuid);
    }
}

package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.chatsnapshot.ChatLog;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatLog;
import xyz.refinedev.phoenix.repository.IChatLogRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLChatLogRepository extends MySQLListRepository<String, IChatLog>
        implements IChatLogRepository {

    public MySQLChatLogRepository(Table table) {
        super(ChatLog.class, table, IChatLog::getId);
    }
}

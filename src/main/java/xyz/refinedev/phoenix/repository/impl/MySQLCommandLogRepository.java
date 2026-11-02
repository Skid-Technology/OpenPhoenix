package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.commandlog.CommandLog;
import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;
import xyz.refinedev.phoenix.repository.ICommandLogRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

import java.util.UUID;

public class MySQLCommandLogRepository extends MySQLListRepository<UUID, ICommandLog>
        implements ICommandLogRepository {

    public MySQLCommandLogRepository(Table table) {
        super(CommandLog.class, table, ICommandLog::getId);
    }
}

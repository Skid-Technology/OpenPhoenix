package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.disguise.DisguiseHistory;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseHistory;
import xyz.refinedev.phoenix.repository.IDisguiseHistoryRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

import java.util.UUID;

public class MySQLDisguiseHistoryRepository extends MySQLListRepository<UUID, IDisguiseHistory>
        implements IDisguiseHistoryRepository {

    public MySQLDisguiseHistoryRepository(Table table) {
        super(DisguiseHistory.class, table, IDisguiseHistory::getId);
    }
}

package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.Rank;
import xyz.refinedev.phoenix.repository.IRankRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

import java.util.UUID;

public class MySQLRankRepository extends MySQLListRepository<UUID, IRank>
        implements IRankRepository {

    public MySQLRankRepository(Table table) {
        super(Rank.class, table, IRank::getUuid);
    }
}

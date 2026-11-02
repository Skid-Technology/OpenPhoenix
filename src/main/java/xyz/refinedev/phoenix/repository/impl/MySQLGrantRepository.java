package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.grant.Grant;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.log.ILogable;
import xyz.refinedev.phoenix.repository.IGrantRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLOnlineCacheListRepository;

import java.util.UUID;

public class MySQLGrantRepository extends MySQLOnlineCacheListRepository<UUID, IGrant>
        implements IGrantRepository {

    private static final String ID_TEXT = "id";

    public MySQLGrantRepository(Table table) {
        super(Grant.class, table, new UUIDColumn(ID_TEXT), ILogable::getUuid);
    }
}

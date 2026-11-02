package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.hop.IServerHop;
import xyz.refinedev.phoenix.profile.hop.ServerHop;
import xyz.refinedev.phoenix.repository.IServerHopRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLCacheListRepository;

import java.util.UUID;

public class MySQLServerHopRepository extends MySQLCacheListRepository<UUID, IServerHop>
        implements IServerHopRepository {

    private static final String ID_TEXT = "id";

    public MySQLServerHopRepository(Table table) {
        super(ServerHop.class, table, new UUIDColumn(ID_TEXT), IServerHop::getId);
    }
}

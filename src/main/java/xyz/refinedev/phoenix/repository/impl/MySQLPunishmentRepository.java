package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.log.ILogable;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.repository.IPunishmentRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLOnlineCacheListRepository;

import java.util.UUID;

public class MySQLPunishmentRepository extends MySQLOnlineCacheListRepository<UUID, IPunishment>
        implements IPunishmentRepository {

    private static final String ID_TEXT = "id";

    public MySQLPunishmentRepository(Table table) {
        super(Punishment.class, table, new UUIDColumn(ID_TEXT), ILogable::getUuid);
    }
}

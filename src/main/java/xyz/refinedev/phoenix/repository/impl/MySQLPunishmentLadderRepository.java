package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.PunishmentLadder;
import xyz.refinedev.phoenix.repository.IPunishmentLadderRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLPunishmentLadderRepository extends MySQLListRepository<String, IPunishmentLadder>
        implements IPunishmentLadderRepository {

    public MySQLPunishmentLadderRepository(Table table) {
        super(PunishmentLadder.class, table, IPunishmentLadder::getId);
    }
}

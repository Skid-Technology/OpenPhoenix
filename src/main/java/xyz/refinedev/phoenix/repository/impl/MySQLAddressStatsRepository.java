package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.alt.AddressStats;
import xyz.refinedev.phoenix.alt.IAddressStats;
import xyz.refinedev.phoenix.repository.IAddressStatsRepository;
import xyz.refinedev.phoenix.utils.mysql.StringColumn;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLMapRepository;

public class MySQLAddressStatsRepository extends MySQLMapRepository<String, IAddressStats>
        implements IAddressStatsRepository {

    private static final String ID_TEXT = "id";

    public MySQLAddressStatsRepository(Table table) {
        super(AddressStats.class, table, StringColumn.of(ID_TEXT, 255), IAddressStats::getId);
    }
}

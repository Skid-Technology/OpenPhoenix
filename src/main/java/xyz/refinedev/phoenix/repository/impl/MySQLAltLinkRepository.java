package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.alt.AltLink;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.repository.IAltLinkRepository;
import xyz.refinedev.phoenix.utils.mysql.StringColumn;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLMapRepository;

public class MySQLAltLinkRepository extends MySQLMapRepository<String, IAltLink>
        implements IAltLinkRepository {

    private static final String ID_TEXT = "id";

    public MySQLAltLinkRepository(Table table) {
        super(AltLink.class, table, StringColumn.of(ID_TEXT, 96), IAltLink::getId);
    }
}

package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.profile.security.SecurityUser;
import xyz.refinedev.phoenix.repository.ISecurityRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLMapRepository;

import java.util.UUID;

public class MySQLSecurityRepository extends MySQLMapRepository<UUID, ISecurityUser>
        implements ISecurityRepository {

    private static final String ID_TEXT = "id";

    public MySQLSecurityRepository(Table table) {
        super(SecurityUser.class, table, new UUIDColumn(ID_TEXT), ISecurityUser::getUuid);
    }
}

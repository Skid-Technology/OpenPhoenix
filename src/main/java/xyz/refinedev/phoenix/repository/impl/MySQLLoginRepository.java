package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.login.Login;
import xyz.refinedev.phoenix.repository.ILoginRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLCacheListRepository;

import java.util.UUID;

public class MySQLLoginRepository extends MySQLCacheListRepository<UUID, ILogin>
        implements ILoginRepository {

    private static final String ID_TEXT = "id";

    public MySQLLoginRepository(Table table) {
        super(Login.class, table, new UUIDColumn(ID_TEXT), ILogin::getId);
    }
}

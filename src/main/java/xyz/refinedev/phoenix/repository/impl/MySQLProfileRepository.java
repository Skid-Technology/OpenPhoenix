package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.RedisProfile;
import xyz.refinedev.phoenix.repository.IProfileRepository;
import xyz.refinedev.phoenix.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLOnlineCacheRepository;

import java.util.Map;
import java.util.UUID;

public class MySQLProfileRepository extends MySQLOnlineCacheRepository<UUID, IProfile>
        implements IProfileRepository {

    private static final String ID_TEXT = "id";

    public MySQLProfileRepository(Table table) {
        super(RedisProfile.class, table, new UUIDColumn(ID_TEXT), IProfile::getUniqueId);
    }

    protected void afterWrite(
            UUID uUID, IProfile profile, Map<String, String> entries, boolean matches) {
        RepositoryFactory.applyUUID(uUID, profile, entries, matches);
    }
}

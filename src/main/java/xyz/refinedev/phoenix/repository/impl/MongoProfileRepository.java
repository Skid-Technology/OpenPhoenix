package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.RedisProfile;
import xyz.refinedev.phoenix.repository.IProfileRepository;
import xyz.refinedev.phoenix.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoOnlineCacheRepository;

import java.util.Map;
import java.util.UUID;

public class MongoProfileRepository extends MongoOnlineCacheRepository<UUID, IProfile>
        implements IProfileRepository {

    public MongoProfileRepository(MongoCollection<Document> mongoCollection) {
        super(RedisProfile.class, mongoCollection);
    }

    protected void afterWrite(
            UUID uUID, IProfile profile, Map<String, String> entries, boolean matches) {
        RepositoryFactory.applyUUID(uUID, profile, entries, matches);
    }
}

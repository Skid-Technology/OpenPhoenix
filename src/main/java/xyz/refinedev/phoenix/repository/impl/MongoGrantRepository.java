package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.grant.Grant;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.repository.IGrantRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoOnlineCacheListRepository;

import java.util.UUID;

public class MongoGrantRepository extends MongoOnlineCacheListRepository<UUID, IGrant>
        implements IGrantRepository {

    public MongoGrantRepository(MongoCollection<Document> mongoCollection) {
        super(Grant.class, mongoCollection);
    }
}

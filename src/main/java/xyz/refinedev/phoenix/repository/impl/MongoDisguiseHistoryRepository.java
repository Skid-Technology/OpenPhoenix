package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.disguise.DisguiseHistory;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseHistory;
import xyz.refinedev.phoenix.repository.IDisguiseHistoryRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

import java.util.UUID;

public class MongoDisguiseHistoryRepository extends MongoListRepository<UUID, IDisguiseHistory>
        implements IDisguiseHistoryRepository {

    public MongoDisguiseHistoryRepository(MongoCollection<Document> mongoCollection) {
        super(DisguiseHistory.class, mongoCollection, IDisguiseHistory::getId);
    }
}

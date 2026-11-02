package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.Rank;
import xyz.refinedev.phoenix.repository.IRankRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

import java.util.UUID;

public class MongoRankRepository extends MongoListRepository<UUID, IRank>
        implements IRankRepository {

    public MongoRankRepository(MongoCollection<Document> mongoCollection) {
        super(Rank.class, mongoCollection, IRank::getUuid);
    }
}

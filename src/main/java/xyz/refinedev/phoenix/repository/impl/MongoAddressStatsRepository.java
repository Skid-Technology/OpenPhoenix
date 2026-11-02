package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.alt.AddressStats;
import xyz.refinedev.phoenix.alt.IAddressStats;
import xyz.refinedev.phoenix.repository.IAddressStatsRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoMapRepository;

public class MongoAddressStatsRepository extends MongoMapRepository<String, IAddressStats>
        implements IAddressStatsRepository {

    public MongoAddressStatsRepository(MongoCollection<Document> mongoCollection) {
        super(AddressStats.class, mongoCollection);
    }
}

package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.alt.AltLink;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.repository.IAltLinkRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoMapRepository;

public class MongoAltLinkRepository extends MongoMapRepository<String, IAltLink>
        implements IAltLinkRepository {

    public MongoAltLinkRepository(MongoCollection<Document> mongoCollection) {
        super(AltLink.class, mongoCollection);
    }
}

package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.profile.security.SecurityUser;
import xyz.refinedev.phoenix.repository.ISecurityRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoMapRepository;

import java.util.UUID;

public class MongoSecurityRepository extends MongoMapRepository<UUID, ISecurityUser>
        implements ISecurityRepository {

    public MongoSecurityRepository(MongoCollection<Document> mongoCollection) {
        super(SecurityUser.class, mongoCollection);
    }
}

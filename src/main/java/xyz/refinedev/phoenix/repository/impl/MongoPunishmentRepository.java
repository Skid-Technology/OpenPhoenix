package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.repository.IPunishmentRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoOnlineCacheListRepository;

import java.util.UUID;

public class MongoPunishmentRepository extends MongoOnlineCacheListRepository<UUID, IPunishment>
        implements IPunishmentRepository {

    public MongoPunishmentRepository(MongoCollection<Document> mongoCollection) {
        super(Punishment.class, mongoCollection);
    }
}

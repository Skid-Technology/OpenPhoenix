package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.commandlog.CommandLog;
import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;
import xyz.refinedev.phoenix.repository.ICommandLogRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

import java.util.UUID;

public class MongoCommandLogRepository extends MongoListRepository<UUID, ICommandLog>
        implements ICommandLogRepository {

    public MongoCommandLogRepository(MongoCollection<Document> mongoCollection) {
        super(CommandLog.class, mongoCollection, ICommandLog::getId);
    }
}

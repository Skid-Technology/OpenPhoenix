package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatLog;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatLog;
import xyz.refinedev.phoenix.repository.IChatLogRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoChatLogRepository extends MongoListRepository<String, IChatLog>
        implements IChatLogRepository {

    public MongoChatLogRepository(MongoCollection<Document> mongoCollection) {
        super(ChatLog.class, mongoCollection, IChatLog::getId);
    }
}

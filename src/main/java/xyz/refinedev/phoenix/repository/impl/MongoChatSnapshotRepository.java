package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatSnapshot;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.repository.IChatSnapshotRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

import java.util.UUID;

public class MongoChatSnapshotRepository extends MongoListRepository<UUID, IChatSnapshot>
        implements IChatSnapshotRepository {

    public MongoChatSnapshotRepository(MongoCollection<Document> mongoCollection) {
        super(ChatSnapshot.class, mongoCollection, IChatSnapshot::getUuid);
    }
}

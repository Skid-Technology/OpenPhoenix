package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.hop.IServerHop;
import xyz.refinedev.phoenix.profile.hop.ServerHop;
import xyz.refinedev.phoenix.repository.IServerHopRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoCacheListRepository;

import java.util.UUID;

public class MongoServerHopRepository extends MongoCacheListRepository<UUID, IServerHop>
        implements IServerHopRepository {

    public MongoServerHopRepository(MongoCollection<Document> mongoCollection) {
        super(ServerHop.class, mongoCollection);
    }
}

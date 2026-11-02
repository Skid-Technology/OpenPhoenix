package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.repository.IBanWaveRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

import java.util.UUID;

public class MongoBanWaveRepository extends MongoListRepository<UUID, IBanWave>
        implements IBanWaveRepository {

    public MongoBanWaveRepository(MongoCollection<Document> mongoCollection) {
        super(BanWave.class, mongoCollection, IBanWave::getId);
    }
}

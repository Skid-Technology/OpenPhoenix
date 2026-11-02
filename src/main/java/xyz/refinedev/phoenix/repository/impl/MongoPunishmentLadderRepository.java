package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.PunishmentLadder;
import xyz.refinedev.phoenix.repository.IPunishmentLadderRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoPunishmentLadderRepository extends MongoListRepository<String, IPunishmentLadder>
        implements IPunishmentLadderRepository {

    public MongoPunishmentLadderRepository(MongoCollection<Document> mongoCollection) {
        super(PunishmentLadder.class, mongoCollection, IPunishmentLadder::getId);
    }
}

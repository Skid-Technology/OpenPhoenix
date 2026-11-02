package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.login.Login;
import xyz.refinedev.phoenix.repository.ILoginRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoCacheListRepository;

import java.util.UUID;

public class MongoLoginRepository extends MongoCacheListRepository<UUID, ILogin>
        implements ILoginRepository {

    public MongoLoginRepository(MongoCollection<Document> mongoCollection) {
        super(Login.class, mongoCollection);
    }
}

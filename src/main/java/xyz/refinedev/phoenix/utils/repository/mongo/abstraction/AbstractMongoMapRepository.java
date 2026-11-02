package xyz.refinedev.phoenix.utils.repository.mongo.abstraction;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.repository.GatewayMapRepository;

import java.lang.reflect.Type;

public abstract class AbstractMongoMapRepository<K, V> extends GatewayMapRepository<K, V> {

    private static final String text = "_id";

    public AbstractMongoMapRepository(Type type, MongoCollection<Document> mongoCollection) {
        super(
                new MongoCollectionGateway<>(
                        type, mongoCollection, text, PlatformGetter.getInstance().getGson()),
                null);
    }

    public MongoCollectionGateway<V> getGateway() {
        return (MongoCollectionGateway<V>) super.getGateway();
    }
}

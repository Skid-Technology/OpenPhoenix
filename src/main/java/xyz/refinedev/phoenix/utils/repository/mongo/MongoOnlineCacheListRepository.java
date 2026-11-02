package xyz.refinedev.phoenix.utils.repository.mongo;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.repository.GatewayOnlineCacheListRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.abstraction.MongoCollectionGateway;

import java.lang.reflect.Type;

public class MongoOnlineCacheListRepository<K, V> extends GatewayOnlineCacheListRepository<K, V> {

    private static final String text = "_id";

    public MongoOnlineCacheListRepository(Type type, MongoCollection<Document> mongoCollection) {
        super(
                new MongoCollectionGateway<>(
                        type, mongoCollection, text, PlatformGetter.getInstance().getGson()),
                null);
    }

    public MongoCollectionGateway<V> getGateway() {
        return (MongoCollectionGateway<V>) super.getGateway();
    }
}

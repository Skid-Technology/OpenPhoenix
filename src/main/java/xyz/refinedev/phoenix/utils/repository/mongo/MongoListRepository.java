package xyz.refinedev.phoenix.utils.repository.mongo;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.repository.GatewayListRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.abstraction.MongoCollectionGateway;

import java.lang.reflect.Type;
import java.util.function.Function;

public class MongoListRepository<K, V> extends GatewayListRepository<K, V> {

    private static final String text = "_id";

    public MongoListRepository(
            Type type, MongoCollection<Document> mongoCollection, Function<V, K> function) {
        this(type, mongoCollection, function, text);
    }

    public MongoListRepository(
            Type type,
            MongoCollection<Document> mongoCollection,
            Function<V, K> function,
            String text) {
        super(
                new MongoCollectionGateway<>(
                        type, mongoCollection, text, PlatformGetter.getInstance().getGson()),
                function);
    }

    public MongoCollectionGateway<V> getGateway() {
        return (MongoCollectionGateway<V>) super.getGateway();
    }
}

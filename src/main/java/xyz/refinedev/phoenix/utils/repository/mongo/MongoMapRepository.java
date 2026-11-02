package xyz.refinedev.phoenix.utils.repository.mongo;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.utils.repository.mongo.abstraction.AbstractMongoMapRepository;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MongoMapRepository<K, V> extends AbstractMongoMapRepository<K, V> {

    private final Map<K, V> cache = new ConcurrentHashMap<>();

    public MongoMapRepository(Type type, MongoCollection<Document> mongoCollection) {
        super(type, mongoCollection);
    }

    @Override
    public Map<K, V> getMap() {
        return this.cache;
    }

    @Override
    public Optional<V> getFromCache(K k) {
        return Optional.ofNullable(this.cache.get(k));
    }

    @Override
    public void addToCache(K k, V v) {
        this.cache.put((K) k, (V) v);
    }

    @Override
    public void removeFromCache(K k) {
        this.cache.remove(k);
    }
}

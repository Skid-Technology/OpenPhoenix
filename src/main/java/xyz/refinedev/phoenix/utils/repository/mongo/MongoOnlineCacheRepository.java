package xyz.refinedev.phoenix.utils.repository.mongo;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.utils.repository.IOnlineCacheRepository;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MongoOnlineCacheRepository<K, V> extends MongoCacheRepository<K, V>
        implements IOnlineCacheRepository<K, V> {

    private final ConcurrentHashMap<K, V> onlineCache = new ConcurrentHashMap<>();

    public MongoOnlineCacheRepository(Type type, MongoCollection<Document> mongoCollection) {
        super(type, mongoCollection);
    }

    @Override
    public Optional<V> getFromCache(K k) {
        V result;
        return (result = this.onlineCache.get(k)) != null
                ? Optional.of((V) result)
                : super.getFromCache((K) k);
    }

    @Override
    public void addToOnlineCache(K k, V v) {
        this.onlineCache.put((K) k, (V) v);
    }

    @Override
    public boolean isInOnlineCache(K k) {
        return this.onlineCache.containsKey(k);
    }

    @Override
    public void removeFromOnlineCache(K k) {
        this.onlineCache.remove(k);
    }

    public ConcurrentHashMap<K, V> getOnlineCache() {
        return this.onlineCache;
    }
}

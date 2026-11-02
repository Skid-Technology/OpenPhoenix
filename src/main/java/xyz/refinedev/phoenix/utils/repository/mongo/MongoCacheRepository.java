package xyz.refinedev.phoenix.utils.repository.mongo;

import com.google.gson.Gson;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.repository.ICacheRepository;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MongoCacheRepository<K, V> extends MongoMapRepository<K, V>
        implements ICacheRepository<K, V> {

    private final Type type;

    private final MongoCollection<Document> collection;

    private final Gson gson;

    private final Cache<K, V> cache;

    private static final long time = 30L;

    public MongoCacheRepository(Type type2, MongoCollection<Document> mongoCollection) {
        super(type2, mongoCollection);
        this.type = type2;
        this.collection = mongoCollection;
        this.gson = PlatformGetter.getInstance().getGson();
        this.cache = Caffeine.newBuilder().expireAfterAccess(time, TimeUnit.MINUTES).build();
    }

    @Override
    public Optional<V> getFromCache(K k) {
        return Optional.ofNullable((V) this.cache.getIfPresent(k));
    }

    @Override
    public void addToCache(K k, V v) {
        this.cache.put(k, v);
    }

    @Override
    public void removeFromCache(K k) {
        this.cache.invalidate(k);
    }

    public Type getType() {
        return this.type;
    }

    public MongoCollection<Document> getCollection() {
        return this.collection;
    }

    public Gson getGson() {
        return this.gson;
    }

    @Override
    public Cache<K, V> getCache() {
        return this.cache;
    }
}

package xyz.refinedev.phoenix.utils.repository.redis;

import com.google.gson.Gson;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.repository.FieldWrites;
import xyz.refinedev.phoenix.utils.repository.Readable;
import xyz.refinedev.phoenix.utils.repository.Writable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RedisMapRepository<K, V> implements Readable<K, V>, Writable<K, V> {

    private final Type type;

    private final Gson gson;

    private final Map<K, V> cache;

    private final String dbKey;

    private final CommonPlatform api;

    public RedisMapRepository(Type type2, String dbKey, CommonPlatform commonPlatform) {
        this.api = commonPlatform;
        this.type = type2;
        this.gson = commonPlatform.getGson();
        this.cache = new ConcurrentHashMap<>();
        this.dbKey = dbKey;
    }

    @Override
    public String toJson(V v) {
        return this.gson.toJson(v, this.type);
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

    private V deserialize(String text) {
        return TrackedEntity.snapshotIfTracked((V) this.gson.fromJson(text, this.type), this.gson);
    }

    @Override
    public CompletableFuture<Boolean> isInDatabase(K k) {
        return CompletableFuture.supplyAsync(
                () -> this.isInDatabaseSync((K) k), AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public boolean isInDatabaseSync(K k) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        Boolean matches;
        return (matches =
                                databaseHandler.runRedisCommand(
                                        result -> result.hexists(this.dbKey, k.toString())))
                        != null
                && matches;
    }

    @Override
    public CompletableFuture<Boolean> saveGson(K k, String text) {
        return FieldWrites.landed(
                CompletableFuture.runAsync(
                        () -> this.saveGsonSync((K) k, text), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while saving (Key: " + k + ")");
    }

    @Override
    public void saveGsonSync(K k, String text) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(result -> result.hset(this.dbKey, k.toString(), text));
    }

    @Override
    public CompletableFuture<Boolean> save(K k, V v) {
        return this.saveGson((K) k, this.gson.toJson(v, this.type));
    }

    @Override
    public void saveSync(K k, V v) {
        this.saveGsonSync((K) k, this.gson.toJson(v, this.type));
    }

    @Override
    public CompletableFuture<Void> delete(K k) {
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        () -> this.deleteSync((K) k), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while removing (Key: " + k + ")");
    }

    @Override
    public void deleteSync(K k) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result -> result.hdel(this.dbKey, new String[] {k.toString()}));
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        RedisMapRepository<K, V> redisMapRepository = this;
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        redisMapRepository::deleteAllSync, AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while deleting all documents");
    }

    @Override
    public void deleteAllSync() {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(result -> result.del(new String[] {this.dbKey}));
    }

    @Override
    public CompletableFuture<List<V>> fetchAll() {
        RedisMapRepository<K, V> redisMapRepository = this;
        return CompletableFuture.supplyAsync(
                redisMapRepository::fetchAllSync, AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public List<V> fetchAllSync() {
        ArrayList<V> values2 = new ArrayList<>();
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        Iterator<String> iterator2 =
                databaseHandler
                        .runRedisCommand(result -> result.hgetall(this.dbKey).values())
                        .iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = (String) iterator2.next()) != null) {
                values2.add(this.deserialize(text));
            }
        }
        return Collections.synchronizedList(values2);
    }

    @Override
    public CompletableFuture<Optional<V>> fetch(K k) {
        return CompletableFuture.supplyAsync(
                () -> this.fetchSync((K) k), AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public Optional<V> fetchSync(K k) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        String text;
        return (text =
                                databaseHandler.runRedisCommand(
                                        result -> (String) result.hget(this.dbKey, k.toString())))
                        == null
                ? Optional.empty()
                : Optional.ofNullable(this.deserialize(text));
    }

    @Override
    public long count() {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        Long value;
        return (value = databaseHandler.runRedisCommand(result -> result.hlen(this.dbKey))) != null
                ? value
                : 0L;
    }

    public Type getType() {
        return this.type;
    }

    public Gson getGson() {
        return this.gson;
    }

    public Map<K, V> getCache() {
        return this.cache;
    }

    public String getDbKey() {
        return this.dbKey;
    }

    public CommonPlatform getApi() {
        return this.api;
    }
}

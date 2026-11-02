package xyz.refinedev.phoenix.utils.repository.redis;

import com.google.gson.Gson;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.repository.FieldWrites;
import xyz.refinedev.phoenix.utils.repository.ListReadable;
import xyz.refinedev.phoenix.utils.repository.ListWritable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class RedisListRepository<K, V> implements ListReadable<K, V>, ListWritable<K, V> {

    private final Type type;

    private final List<V> cache;

    private final Gson gson;

    private final String dbKey;

    private final CommonPlatform api;

    private final Function<V, K> keyFunction;

    public RedisListRepository(
            Type type2, String dbKey, CommonPlatform commonPlatform, Function<V, K> function) {
        this.type = type2;
        this.api = commonPlatform;
        this.cache = new CopyOnWriteArrayList<>();
        this.gson = commonPlatform.getGson();
        this.dbKey = dbKey;
        this.keyFunction = function;
    }

    @Override
    public Optional<V> getFromCache(K k) {
        for (Object result : this.cache) {
            if (this.keyFunction.apply((V) result).equals(k)) {
                return Optional.of((V) result);
            }
        }
        return Optional.empty();
    }

    @Override
    public void addToCache(V v) {
        this.cache.add((V) v);
    }

    @Override
    public void removeFromCache(V v) {
        this.cache.remove(v);
    }

    private V deserialize(String text) {
        return TrackedEntity.snapshotIfTracked((V) this.gson.fromJson(text, this.type), this.gson);
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
    public CompletableFuture<List<V>> fetchAll() {
        RedisListRepository<K, V> redisListRepository = this;
        return CompletableFuture.supplyAsync(
                redisListRepository::fetchAllSync, AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public List<V> fetchAllSync() {
        ArrayList<V> values2 = new ArrayList<>();
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        Collection<String> values3;
        if ((values3 =
                        databaseHandler.runRedisCommand(
                                result -> result.hgetall(this.dbKey).values()))
                == null) {
            return values2;
        }
        Iterator iterator2 = values3.iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = (String) iterator2.next()) != null) {
                V result2 = this.deserialize(text);
                values2.add(result2);
            }
        }
        return Collections.synchronizedList(values2);
    }

    @Override
    public CompletableFuture<Boolean> save(V v) {
        return FieldWrites.landed(
                CompletableFuture.runAsync(
                        () -> this.saveSync((V) v), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while saving (Key: " + this.keyFunction.apply((V) v).toString() + ")");
    }

    @Override
    public void saveSync(V v) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result ->
                        result.hset(
                                this.dbKey,
                                this.keyFunction.apply((V) v).toString(),
                                this.gson.toJson(v)));
    }

    @Override
    public CompletableFuture<Void> delete(V v) {
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        () -> this.deleteSync((V) v), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while removing (Key: " + this.keyFunction.apply((V) v).toString() + ")");
    }

    @Override
    public void deleteSync(V v) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result ->
                        result.hdel(
                                this.dbKey,
                                new String[] {this.keyFunction.apply((V) v).toString()}));
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        RedisListRepository<K, V> redisListRepository = this;
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        redisListRepository::deleteAllSync, AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while deleting all documents");
    }

    @Override
    public void deleteAllSync() {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(result -> result.del(new String[] {this.dbKey}));
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

    @Override
    public List<V> getCache() {
        return this.cache;
    }

    public Gson getGson() {
        return this.gson;
    }

    public String getDbKey() {
        return this.dbKey;
    }

    public CommonPlatform getApi() {
        return this.api;
    }

    public Function<V, K> getKeyFunction() {
        return this.keyFunction;
    }
}

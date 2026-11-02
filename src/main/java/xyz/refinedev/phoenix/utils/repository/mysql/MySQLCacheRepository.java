package xyz.refinedev.phoenix.utils.repository.mysql;

import com.google.gson.Gson;

import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.mysql.Column;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.ICacheRepository;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MySQLCacheRepository<K, V> extends MySQLMapRepository<K, V>
        implements ICacheRepository<K, V> {

    private final Type type;

    private final Table table;

    private final Gson gson;

    private final Cache<K, V> cache;

    private static final long time = 30L;

    public MySQLCacheRepository(
            Type type2, Table table2, Column<?> column, Function<V, K> function) {
        super(type2, table2, column, function);
        this.type = type2;
        this.table = table2;
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

    public Table getTable() {
        return this.table;
    }

    public Gson getGson() {
        return this.gson;
    }

    @Override
    public Cache<K, V> getCache() {
        return this.cache;
    }
}

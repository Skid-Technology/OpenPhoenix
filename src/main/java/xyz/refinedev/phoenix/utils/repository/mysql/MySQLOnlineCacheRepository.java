package xyz.refinedev.phoenix.utils.repository.mysql;

import xyz.refinedev.phoenix.utils.mysql.Column;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.IOnlineCacheRepository;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MySQLOnlineCacheRepository<K, V> extends MySQLCacheRepository<K, V>
        implements IOnlineCacheRepository<K, V> {

    private final ConcurrentHashMap<K, V> onlineCache = new ConcurrentHashMap<>();

    public MySQLOnlineCacheRepository(
            Type type, Table table, Column<?> column, Function<V, K> function) {
        super(type, table, column, function);
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

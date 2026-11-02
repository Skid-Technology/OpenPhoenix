package xyz.refinedev.phoenix.utils.repository.mysql;

import xyz.refinedev.phoenix.utils.mysql.Column;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.abstraction.AbstractMySQLMapRepository;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MySQLMapRepository<K, V> extends AbstractMySQLMapRepository<K, V> {

    private final Map<K, V> cache = new ConcurrentHashMap<>();

    public MySQLMapRepository(Type type, Table table, Column<?> column, Function<V, K> function) {
        super(type, table, column, function);
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

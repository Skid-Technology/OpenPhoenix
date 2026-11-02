package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class GatewayCacheListRepository<K, V> extends GatewayMapRepository<K, V>
        implements ICacheListRepository<K, V> {

    private final Cache<K, List<V>> listCache;

    private final KeyedLockCache<K> locks;

    private static final long time = 5L;

    public GatewayCacheListRepository(EntityGateway<V> entityGateway, Function<V, ?> function) {
        super(entityGateway, function);
        this.listCache = Caffeine.newBuilder().expireAfterAccess(time, TimeUnit.MINUTES).build();
        this.locks = new KeyedLockCache<>();
    }

    public KeyedLockCache<K> getLocks() {
        return this.locks;
    }

    @Override
    public Cache<K, List<V>> getListCache() {
        return this.listCache;
    }

    @Override
    public void addToCache(K k, V v) {
        this.locks.withLock(
                (K) k,
                () -> {
                    List<V> values;
                    if ((values = (List) this.listCache.getIfPresent(k)) == null) {
                        values = Collections.synchronizedList(new ArrayList());
                        this.listCache.put(k, values);
                    }
                    values.add(v);
                });
    }

    @Override
    public void putInCache(K k, List<V> values) {
        this.locks.withLock((K) k, () -> this.listCache.put(k, values));
    }

    @Override
    public List<V> getListFromCache(K k) {
        List values;
        if ((values = (List) this.listCache.getIfPresent(k)) == null) {
            return null;
        } else {
            return this.locks.isLocked((K) k) ? new ArrayList<>(values) : values;
        }
    }

    @Override
    public void updateMany(FilterOptions filterOptions, UpdateOptions updateOptions) {
        this.getGateway().updateWhere(filterOptions, updateOptions);
    }

    @Override
    public void removeFromCache(K k) {
        this.locks.withLock((K) k, () -> this.listCache.invalidate(k));
    }

    @Override
    public Map<K, V> getMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<V> getFromCache(K k) {
        throw new UnsupportedOperationException();
    }
}

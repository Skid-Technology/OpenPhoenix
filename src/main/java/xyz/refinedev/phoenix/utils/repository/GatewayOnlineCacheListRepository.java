package xyz.refinedev.phoenix.utils.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class GatewayOnlineCacheListRepository<K, V>
        extends GatewayCacheListRepository<K, V> implements IOnlineCacheListRepository<K, V> {

    private final ConcurrentHashMap<K, List<V>> onlineCache = new ConcurrentHashMap<>();

    public GatewayOnlineCacheListRepository(
            EntityGateway<V> entityGateway, Function<V, ?> function) {
        super(entityGateway, function);
    }

    public ConcurrentHashMap<K, List<V>> getOnlineCache() {
        return this.onlineCache;
    }

    @Override
    public List<V> getListFromCache(K k) {
        List values;
        if ((values = this.onlineCache.get(k)) == null) {
            values = (List) this.getListCache().getIfPresent(k);
        }
        if (values == null) {
            return null;
        } else {
            return this.getLocks().isLocked((K) k) ? new ArrayList<>(values) : values;
        }
    }

    @Override
    public void addToOnlineCache(K k, List<V> values) {
        KeyedLockCache locks = this.getLocks();
        locks.withLock(k, () -> this.onlineCache.put((K) k, values));
    }

    @Override
    public boolean isInOnlineCache(K k) {
        return this.onlineCache.containsKey(k);
    }

    @Override
    public void removeFromOnlineCache(K k) {
        KeyedLockCache locks = this.getLocks();
        locks.withLock(k, () -> this.onlineCache.remove(k));
    }
}

package xyz.refinedev.phoenix.utils.repository;

import java.util.List;

public interface IOnlineCacheListRepository<K, V> extends ICacheListRepository<K, V> {

    void addToOnlineCache(K k, List<V> values);

    boolean isInOnlineCache(K k);

    void removeFromOnlineCache(K k);
}

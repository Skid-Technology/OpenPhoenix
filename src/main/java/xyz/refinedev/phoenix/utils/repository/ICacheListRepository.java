package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;

import java.util.List;

public interface ICacheListRepository<K, V> extends IMapRepository<K, V> {

    void putInCache(K k, List<V> values);

    List<V> getListFromCache(K k);

    Cache<K, List<V>> getListCache();

    void updateMany(FilterOptions filterOptions, UpdateOptions updateOptions);
}

package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.lib.caffeine.cache.Cache;

public interface ICacheRepository<K, V> extends IMapRepository<K, V> {

    Cache<K, V> getCache();
}

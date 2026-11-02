package xyz.refinedev.phoenix.utils.repository;

public interface IOnlineCacheRepository<K, V> extends ICacheRepository<K, V> {

    void addToOnlineCache(K k, V v);

    boolean isInOnlineCache(K k);

    void removeFromOnlineCache(K k);
}

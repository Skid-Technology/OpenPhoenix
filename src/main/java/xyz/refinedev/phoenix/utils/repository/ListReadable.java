package xyz.refinedev.phoenix.utils.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ListReadable<K, V> {

    void addToCache(V v);

    void removeFromCache(V v);

    List<V> getCache();

    Optional<V> getFromCache(K k);

    CompletableFuture<Optional<V>> fetch(K k);

    Optional<V> fetchSync(K k);

    CompletableFuture<List<V>> fetchAll();

    List<V> fetchAllSync();

    long count();
}

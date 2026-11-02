package xyz.refinedev.phoenix.utils.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Readable<K, V> {

    Map<K, V> getMap();

    Optional<V> getFromCache(K k);

    void addToCache(K k, V v);

    void removeFromCache(K k);

    CompletableFuture<Optional<V>> fetch(K k);

    Optional<V> fetchSync(K k);

    CompletableFuture<List<V>> fetchAll();

    List<V> fetchAllSync();

    CompletableFuture<Boolean> isInDatabase(K k);

    boolean isInDatabaseSync(K k);

    long count();
}

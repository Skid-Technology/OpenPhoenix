package xyz.refinedev.phoenix.utils.repository;

import java.util.concurrent.CompletableFuture;

public interface ListWritable<K, V> {

    CompletableFuture<Boolean> save(V v);

    void saveSync(V v);

    CompletableFuture<Void> delete(V v);

    void deleteSync(V v);

    CompletableFuture<Void> deleteAll();

    void deleteAllSync();
}

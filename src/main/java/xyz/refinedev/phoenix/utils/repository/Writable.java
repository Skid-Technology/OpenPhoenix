package xyz.refinedev.phoenix.utils.repository;

import java.util.concurrent.CompletableFuture;

public interface Writable<K, V> {

    String toJson(V v);

    CompletableFuture<Boolean> save(K k, V v);

    void saveSync(K k, V v);

    CompletableFuture<Boolean> saveGson(K k, String text);

    void saveGsonSync(K k, String text);

    CompletableFuture<Void> delete(K k);

    void deleteSync(K k);

    CompletableFuture<Void> deleteAll();

    void deleteAllSync();
}

package xyz.refinedev.phoenix.utils.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BulkWritable<V> {

    CompletableFuture<Void> save(List<V> values);

    void saveSync(List<V> values);
}

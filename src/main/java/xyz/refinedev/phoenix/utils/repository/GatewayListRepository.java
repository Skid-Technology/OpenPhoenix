package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class GatewayListRepository<K, V> implements IListRepository<K, V> {

    private final EntityGateway<V> gateway;

    private final List<V> cache = new CopyOnWriteArrayList<>();

    private final Function<V, K> keyFunction;

    public GatewayListRepository(EntityGateway<V> entityGateway, Function<V, K> function) {
        this.gateway = entityGateway;
        this.keyFunction = function;
    }

    public EntityGateway<V> getGateway() {
        return this.gateway;
    }

    @Override
    public List<V> getCache() {
        return this.cache;
    }

    public Function<V, K> getKeyFunction() {
        return this.keyFunction;
    }

    @Override
    public Optional<V> getFromCache(K k) {
        for (Object result : this.cache) {
            if (this.keyFunction.apply((V) result).equals(k)) {
                return Optional.of((V) result);
            }
        }
        return Optional.empty();
    }

    @Override
    public void addToCache(V v) {
        K result = this.keyFunction.apply((V) v);
        synchronized (this.cache) {
            for (int index = 0; index < this.cache.size(); index++) {
                if (this.keyFunction.apply(this.cache.get(index)).equals(result)) {
                    this.cache.set(index, (V) v);
                    return;
                }
            }
            this.cache.add((V) v);
        }
    }

    @Override
    public void removeFromCache(V v) {
        this.cache.remove(v);
    }

    @Override
    public CompletableFuture<Boolean> save(V v) {
        K result = this.keyFunction.apply((V) v);
        EntityGateway entityGateway = this.gateway;
        String text = result.toString();
        return FieldWrites.saveAsync(
                v, entityGateway, text, () -> this.saveWholeAsync((K) result, (V) v));
    }

    private CompletableFuture<Boolean> saveWholeAsync(K k, V v) {
        String text = this.gateway.toJson((V) v);
        return FieldWrites.landed(
                CompletableFuture.runAsync(
                        () -> this.gateway.replace(k.toString(), text),
                        AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while saving (Key: " + k + ")");
    }

    @Override
    public void saveSync(V v) {
        FieldWrites.saveSync(v, this.gateway, this.keyFunction.apply((V) v).toString());
    }

    @Override
    public CompletableFuture<Void> save(List<V> values) {
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        () -> this.saveSync(values), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while saving (" + values.size() + " Keys)");
    }

    @Override
    public void saveSync(List<V> values) {
        this.gateway.saveAll(values, this.keyFunction);
    }

    @Override
    public CompletableFuture<Void> delete(V v) {
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        () -> this.deleteSync((V) v), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while removing (Key: " + this.keyFunction.apply((V) v) + ")");
    }

    @Override
    public CompletableFuture<Long> delete(FilterOptions filterOptions) {
        return FieldWrites.loggedOr(
                CompletableFuture.supplyAsync(
                        () -> this.deleteSync(filterOptions), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while removing (" + this.gateway.describe() + ")",
                0L);
    }

    @Override
    public void deleteSync(V v) {
        this.gateway.deleteByKey(this.keyFunction.apply((V) v).toString());
    }

    @Override
    public long deleteSync(FilterOptions filterOptions) {
        return this.gateway.deleteWhere(filterOptions);
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        GatewayListRepository<K, V> gatewayListRepository = this;
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        gatewayListRepository::deleteAllSync, AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while deleting all documents");
    }

    @Override
    public void deleteAllSync() {
        this.gateway.deleteAll();
    }

    @Override
    public CompletableFuture<Optional<V>> fetch(K k) {
        return CompletableFuture.supplyAsync(
                () -> this.fetchSync((K) k), AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<Optional<V>> fetch(FilterOptions filterOptions) {
        return CompletableFuture.supplyAsync(
                () -> this.fetchSync(filterOptions), AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<Optional<V>> fetch(
            FilterOptions filterOptions, SortingOptions sortingOptions) {
        return CompletableFuture.supplyAsync(
                () -> this.fetchSync(filterOptions, sortingOptions),
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public Optional<V> fetchSync(K k) {
        return this.gateway.findByKey(k.toString());
    }

    @Override
    public Optional<V> fetchSync(FilterOptions filterOptions) {
        return this.gateway.findOne(filterOptions, null);
    }

    @Override
    public Optional<V> fetchSync(FilterOptions filterOptions, SortingOptions sortingOptions) {
        return this.gateway.findOne(filterOptions, sortingOptions);
    }

    @Override
    public CompletableFuture<List<V>> fetchAll() {
        GatewayListRepository<K, V> gatewayListRepository = this;
        return CompletableFuture.supplyAsync(
                gatewayListRepository::fetchAllSync, AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<List<V>> fetchAll(FilterOptions filterOptions) {
        return CompletableFuture.supplyAsync(
                () -> this.fetchAllSync(filterOptions), AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<List<V>> fetchAll(
            FilterOptions filterOptions, SortingOptions sortingOptions) {
        return CompletableFuture.supplyAsync(
                () -> this.fetchAllSync(filterOptions, sortingOptions),
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public List<V> fetchAllSync() {
        return this.gateway.findAll();
    }

    @Override
    public List<V> fetchAllSync(FilterOptions filterOptions) {
        return this.gateway.findAll(filterOptions, null);
    }

    @Override
    public List<V> fetchAllSync(FilterOptions filterOptions, SortingOptions sortingOptions) {
        return this.gateway.findAll(filterOptions, sortingOptions);
    }

    @Override
    public List<V> fetchPageSync(
            FilterOptions filterOptions, SortingOptions sortingOptions, int value) {
        return this.gateway.findPage(filterOptions, sortingOptions, value);
    }

    @Override
    public List<V> fetchAllDetachedSync(FilterOptions filterOptions) {
        return this.gateway.findAllDetached(filterOptions, null);
    }

    @Override
    public List<String> fieldValuesSync(
            String text, FilterOptions filterOptions, SortingOptions sortingOptions) {
        return this.gateway.findFieldValues(text, filterOptions, sortingOptions);
    }

    @Override
    public long count(FilterOptions filterOptions) {
        return this.gateway.count(filterOptions);
    }

    @Override
    public long count() {
        return this.gateway.count();
    }
}

package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class GatewayMapRepository<K, V> implements IMapRepository<K, V> {

    private final EntityGateway<V> gateway;

    private final Function<V, ?> batchKeyFunction;

    public GatewayMapRepository(EntityGateway<V> entityGateway, Function<V, ?> function) {
        this.gateway = entityGateway;
        this.batchKeyFunction = function;
    }

    public EntityGateway<V> getGateway() {
        return this.gateway;
    }

    @Override
    public abstract Optional<V> getFromCache(K k);

    @Override
    public String toJson(V v) {
        return this.gateway.toJson((V) v);
    }

    @Override
    public CompletableFuture<Boolean> save(K k, V v) {
        EntityGateway entityGateway = this.gateway;
        String text = k.toString();
        Supplier supplier = () -> this.saveGson((K) k, this.toJson((V) v));
        return FieldWrites.saveAsync(
                v,
                entityGateway,
                text,
                supplier,
                (result, result2) -> this.afterWrite((K) k, (V) v, result, result2));
    }

    @Override
    public void saveSync(K k, V v) {
        EntityGateway entityGateway = this.gateway;
        String text = k.toString();
        FieldWrites.saveSync(
                v,
                entityGateway,
                text,
                (result, result2) -> this.afterWrite((K) k, (V) v, result, result2));
    }

    protected void afterWrite(K k, V v, Map<String, String> entries, boolean matches) {}

    @Override
    public CompletableFuture<Void> save(List<V> values) {
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        () -> this.saveSync(values), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while saving (" + values.size() + " Keys)");
    }

    @Override
    public void saveSync(List<V> values) {
        this.gateway.saveAll(values, this.batchKeyFunction);
    }

    @Override
    public CompletableFuture<Boolean> saveGson(K k, String text) {
        return FieldWrites.landed(
                CompletableFuture.runAsync(
                        () -> this.saveGsonSync((K) k, text), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while saving (Key: " + k + ")");
    }

    @Override
    public void saveGsonSync(K k, String text) {
        this.gateway.replace(k.toString(), text);
    }

    @Override
    public CompletableFuture<Void> delete(K k) {
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        () -> this.deleteSync((K) k), AbstractDatabaseHandler.DB_EXECUTOR),
                "Error while removing (Key: " + k + ")");
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
    public void deleteSync(K k) {
        this.gateway.deleteByKey(k.toString());
    }

    @Override
    public long deleteSync(FilterOptions filterOptions) {
        return this.gateway.deleteWhere(filterOptions);
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        GatewayMapRepository<K, V> gatewayMapRepository = this;
        return FieldWrites.logged(
                CompletableFuture.runAsync(
                        gatewayMapRepository::deleteAllSync, AbstractDatabaseHandler.DB_EXECUTOR),
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
        GatewayMapRepository<K, V> gatewayMapRepository = this;
        return CompletableFuture.supplyAsync(
                gatewayMapRepository::fetchAllSync, AbstractDatabaseHandler.DB_EXECUTOR);
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
    public CompletableFuture<Boolean> isInDatabase(K k) {
        return CompletableFuture.supplyAsync(
                () -> this.isInDatabaseSync((K) k), AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public boolean isInDatabaseSync(K k) {
        return this.gateway.exists(k.toString());
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

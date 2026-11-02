package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Queryable<K, V> {

    CompletableFuture<Optional<V>> fetch(FilterOptions filterOptions);

    CompletableFuture<Optional<V>> fetch(
            FilterOptions filterOptions, SortingOptions sortingOptions);

    Optional<V> fetchSync(FilterOptions filterOptions);

    Optional<V> fetchSync(FilterOptions filterOptions, SortingOptions sortingOptions);

    CompletableFuture<List<V>> fetchAll(FilterOptions filterOptions);

    CompletableFuture<List<V>> fetchAll(FilterOptions filterOptions, SortingOptions sortingOptions);

    List<V> fetchAllSync(FilterOptions filterOptions);

    List<V> fetchAllSync(FilterOptions filterOptions, SortingOptions sortingOptions);

    default List<V> fetchPageSync(
            FilterOptions filterOptions, SortingOptions sortingOptions, int value) {
        List<V> values;
        return (values = this.fetchAllSync(filterOptions, sortingOptions)).size() <= value
                ? values
                : new ArrayList<>(values.subList(0, value));
    }

    default List<V> fetchAllDetachedSync(FilterOptions filterOptions) {
        return this.fetchAllSync(filterOptions);
    }

    default List<String> fieldValuesSync(
            String text, FilterOptions filterOptions, SortingOptions sortingOptions) {
        return null;
    }

    CompletableFuture<Long> delete(FilterOptions filterOptions);

    long deleteSync(FilterOptions filterOptions);

    long count(FilterOptions filterOptions);
}

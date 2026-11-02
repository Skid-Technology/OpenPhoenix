package xyz.refinedev.phoenix.utils.repository;

import com.google.gson.Gson;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface EntityGateway<V> {

    Gson getGson();

    String toJson(V v);

    V deserialize(String text);

    String describe();

    void replace(String text, String text2);

    void updateFields(String text, UpdateOptions updateOptions, String text2);

    boolean needsFullJson();

    default String fullJsonIfNeeded(Supplier<String> supplier) {
        return this.needsFullJson() ? (String) supplier.get() : null;
    }

    void saveAll(List<V> values, Function<V, ?> function);

    Optional<V> findByKey(String text);

    Optional<V> findOne(FilterOptions filterOptions, SortingOptions sortingOptions);

    List<V> findAll();

    List<V> findAll(FilterOptions filterOptions, SortingOptions sortingOptions);

    default List<V> findPage(
            FilterOptions filterOptions, SortingOptions sortingOptions, int value) {
        List<V> values;
        return (values = this.findAll(filterOptions, sortingOptions)).size() <= value
                ? values
                : new ArrayList<>(values.subList(0, value));
    }

    default List<V> findAllDetached(FilterOptions filterOptions, SortingOptions sortingOptions) {
        return this.findAll(filterOptions, sortingOptions);
    }

    default List<String> findFieldValues(
            String text, FilterOptions filterOptions, SortingOptions sortingOptions) {
        return null;
    }

    void deleteByKey(String text);

    long deleteWhere(FilterOptions filterOptions);

    void deleteAll();

    boolean exists(String text);

    long count();

    long count(FilterOptions filterOptions);

    void updateWhere(FilterOptions filterOptions, UpdateOptions updateOptions);
}

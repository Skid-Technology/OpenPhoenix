package xyz.refinedev.phoenix.utils.repository.filters.api;

public interface FilterOptions {

    <R> R accept(FilterVisitor<R> filterVisitor);
}

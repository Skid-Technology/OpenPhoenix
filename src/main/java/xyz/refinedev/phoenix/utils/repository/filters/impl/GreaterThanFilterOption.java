package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

public class GreaterThanFilterOption<T> implements FilterOptions {

    private final String field;

    private final T value;

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitGreaterThan(this.field, this.value);
    }

    public GreaterThanFilterOption(String field, T t) {
        this.field = field;
        this.value = (T) t;
    }
}

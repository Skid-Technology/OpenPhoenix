package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

public class EqualsFilterOption<T> implements FilterOptions {

    private final String field;

    private final T value;

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitEquals(this.field, this.value);
    }

    public EqualsFilterOption(String field, T t) {
        this.field = field;
        this.value = (T) t;
    }
}

package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

public class NotEqualsFilterOption<T> implements FilterOptions {

    private final String field;

    private final T value;

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitNotEquals(this.field, this.value);
    }

    public NotEqualsFilterOption(String field, T t) {
        this.field = field;
        this.value = (T) t;
    }
}

package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

public class LowerThanFilterOption<T> implements FilterOptions {

    private final String field;

    private final T value;

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitLowerThan(this.field, this.value);
    }

    public LowerThanFilterOption(String field, T t) {
        this.field = field;
        this.value = (T) t;
    }
}

package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

import java.util.Collection;

public class InFilterOption implements FilterOptions {

    private final String field;

    private final Collection<String> values;

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitIn(this.field, this.values);
    }

    public InFilterOption(String field, Collection<String> values) {
        this.field = field;
        this.values = values;
    }
}

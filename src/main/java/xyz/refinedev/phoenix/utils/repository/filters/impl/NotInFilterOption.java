package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

import java.util.Collection;

public class NotInFilterOption implements FilterOptions {

    private final String field;

    private final Collection<String> values;

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitNotIn(this.field, this.values);
    }

    public NotInFilterOption(String field, Collection<String> values) {
        this.field = field;
        this.values = values;
    }
}

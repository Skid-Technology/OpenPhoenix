package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

public class ExistsFilterOption implements FilterOptions {

    private final String field;

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitExists(this.field);
    }

    public ExistsFilterOption(String field) {
        this.field = field;
    }
}

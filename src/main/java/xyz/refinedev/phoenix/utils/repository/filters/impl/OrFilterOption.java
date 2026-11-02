package xyz.refinedev.phoenix.utils.repository.filters.impl;

import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;

import java.util.Arrays;
import java.util.List;

public class OrFilterOption implements FilterOptions {

    private final List<FilterOptions> children;

    public OrFilterOption(FilterOptions... filterOptions) {
        this.children = Arrays.asList(filterOptions);
    }

    @Override
    public <R> R accept(FilterVisitor<R> filterVisitor) {
        return (R) filterVisitor.visitOr(this.children);
    }

    public OrFilterOption(List<FilterOptions> children) {
        this.children = children;
    }
}

package xyz.refinedev.phoenix.utils.repository.sorting.impl;

import xyz.refinedev.phoenix.utils.repository.sorting.api.SortVisitor;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;

import java.util.List;

public class SortsDescendingOptions implements SortingOptions {

    private final List<String> fieldName;

    @Override
    public <R> R accept(SortVisitor<R> sortVisitor) {
        return (R) sortVisitor.visitDescending(this.fieldName);
    }

    public SortsDescendingOptions(List<String> fieldName) {
        this.fieldName = fieldName;
    }
}

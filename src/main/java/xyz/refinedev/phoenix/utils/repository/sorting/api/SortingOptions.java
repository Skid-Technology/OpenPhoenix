package xyz.refinedev.phoenix.utils.repository.sorting.api;

public interface SortingOptions {

    <R> R accept(SortVisitor<R> sortVisitor);
}

package xyz.refinedev.phoenix.utils.repository.sorting.api;

import java.util.List;

public interface SortVisitor<R> {

    R visitDescending(List<String> values);
}

package xyz.refinedev.phoenix.utils.repository.filters.api;

import java.util.Collection;
import java.util.List;

public interface FilterVisitor<R> {

    R visitEquals(String text, Object result);

    R visitNotEquals(String text, Object result);

    R visitGreaterThan(String text, Object result);

    R visitGreaterThanEquals(String text, Object result);

    R visitLowerThan(String text, Object result);

    R visitIn(String text, Collection<String> values);

    R visitNotIn(String text, Collection<String> values);

    R visitExists(String text);

    R visitAnd(List<FilterOptions> values);

    R visitOr(List<FilterOptions> values);
}

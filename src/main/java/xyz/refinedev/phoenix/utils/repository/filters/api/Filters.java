package xyz.refinedev.phoenix.utils.repository.filters.api;

import xyz.refinedev.phoenix.utils.repository.filters.impl.AndFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.EqualsFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.ExistsFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.GreaterThanEqualsFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.GreaterThanFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.InFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.LowerThanFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.NotEqualsFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.NotInFilterOption;
import xyz.refinedev.phoenix.utils.repository.filters.impl.OrFilterOption;

import java.util.Collection;

public final class Filters {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static String storedLong(long value) {
        return String.valueOf(value);
    }

    public static <T> EqualsFilterOption<T> eq(String text, T t) {
        return new EqualsFilterOption<>(text, (T) t);
    }

    public static <T> NotEqualsFilterOption<T> neq(String text, T t) {
        return new NotEqualsFilterOption<>(text, (T) t);
    }

    public static <T> LowerThanFilterOption<T> lt(String text, T t) {
        return new LowerThanFilterOption<>(text, (T) t);
    }

    public static InFilterOption in(String text, Collection<String> values) {
        return new InFilterOption(text, values);
    }

    public static NotInFilterOption nin(String text, Collection<String> values) {
        return new NotInFilterOption(text, values);
    }

    public static AndFilterOption and(FilterOptions... filterOptions) {
        return new AndFilterOption(filterOptions);
    }

    public static OrFilterOption or(FilterOptions... filterOptions) {
        return new OrFilterOption(filterOptions);
    }

    public static ExistsFilterOption exists(String text) {
        return new ExistsFilterOption(text);
    }

    public static <T> GreaterThanFilterOption<T> gt(String text, T t) {
        return new GreaterThanFilterOption<>(text, (T) t);
    }

    public static <T> GreaterThanEqualsFilterOption<T> gte(String text, T t) {
        return new GreaterThanEqualsFilterOption<>(text, (T) t);
    }

    private Filters() {
        throw new UnsupportedOperationException(text);
    }
}

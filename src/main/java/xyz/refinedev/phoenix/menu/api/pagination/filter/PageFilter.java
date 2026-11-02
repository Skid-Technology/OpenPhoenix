package xyz.refinedev.phoenix.menu.api.pagination.filter;

import java.util.function.Predicate;

public class PageFilter<T> {

    private final String name;

    private final Predicate<T> predicate;

    private boolean enabled;

    public boolean test(T t) {
        return !this.enabled || this.predicate.test((T) t);
    }

    @Override
    public boolean equals(Object result) {
        return result instanceof PageFilter && ((PageFilter) result).getName().equals(this.name);
    }

    public PageFilter(String name, Predicate<T> predicate2) {
        this.name = name;
        this.predicate = predicate2;
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

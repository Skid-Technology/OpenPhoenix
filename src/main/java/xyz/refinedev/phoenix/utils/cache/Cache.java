package xyz.refinedev.phoenix.utils.cache;

import lombok.NonNull;

import java.util.Optional;

public abstract class Cache<T> {

    private volatile T value = (T) null;

    @NonNull
    protected abstract T supply();

    public final T get() {
        T result = this.value;
        if (this.value == null) {
            synchronized (this) {
                result = this.value;
                if (this.value == null) {
                    result = this.supply();
                    this.value = (T) result;
                }
            }
        }
        return (T) result;
    }

    public final Optional<T> getIfPresent() {
        return Optional.ofNullable(this.value);
    }

    public final void invalidate() {
        this.value = null;
    }
}

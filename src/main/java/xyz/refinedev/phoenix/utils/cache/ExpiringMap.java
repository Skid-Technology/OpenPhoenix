package xyz.refinedev.phoenix.utils.cache;

import com.google.common.collect.ForwardingMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ExpiringMap<K, V> extends ForwardingMap<K, V> implements Map<K, V> {

    private final Map<K, V> values;

    private final Function<K, V> function;

    public static <K, V> ExpiringMap<K, V> of(Map<K, V> entries, Function<K, V> function) {
        return new ExpiringMap<>(entries, function);
    }

    public static <K, V> ExpiringMap<K, V> findExpiringMap(Function<K, V> function) {
        return of(new ConcurrentHashMap<>(), function);
    }

    private ExpiringMap(Map<K, V> entries, Function<K, V> function2) {
        this.values = entries;
        this.function = function2;
    }

    protected Map<K, V> delegate() {
        return this.values;
    }

    public V findObject(K k) {
        return this.values.get(k);
    }

    @Override
    public V get(Object result) {
        V result2;
        return (V)
                ((result2 = this.values.get(result)) != null
                        ? result2
                        : this.values.computeIfAbsent((K) result, this.function));
    }
}

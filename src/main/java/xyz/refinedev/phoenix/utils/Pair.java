package xyz.refinedev.phoenix.utils;

public class Pair<K, V> {

    public final K first;

    public final V second;

    private Pair(K k, V v) {
        this.first = (K) k;
        this.second = (V) v;
    }

    public static <K, V> Pair<K, V> of(K k, V v) {
        return new Pair<>((K) k, (V) v);
    }

    public K getFirst() {
        return this.first;
    }

    public V getSecond() {
        return this.second;
    }
}

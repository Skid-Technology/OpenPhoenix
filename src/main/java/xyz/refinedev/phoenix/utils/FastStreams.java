package xyz.refinedev.phoenix.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FastStreams {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static <T> Optional<T> streamFilterPeekFirstFindAny(
            Collection<T> values, Predicate<T> predicate, Consumer<T> consumer) {
        Iterator<T> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            T result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                consumer.accept(result);
                return Optional.of((T) result);
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<T> streamFilterOptional(
            Collection<T> values, Predicate<T> predicate) {
        Iterator<T> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            T result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                return Optional.of((T) result);
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<T> streamFilterMapOptional(
            Collection<T> values,
            Predicate<T> predicate,
            Function<? super T, ? extends T> function) {
        Iterator<T> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            T result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                return Optional.of((T) function.apply(result));
            }
        }
        return Optional.empty();
    }

    public static <A> boolean anyMatch(Collection<A> values, Predicate<A> predicate) {
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                return true;
            }
        }
        return false;
    }

    public static <A> ArrayList<A> filterArrayList(Collection<A> values, Predicate<A> predicate) {
        ArrayList<A> values2 = new ArrayList<>(values.size());
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                values2.add(result);
            }
        }
        return values2;
    }

    public static <A> Set<A> filterArrayListToSet(Collection<A> values, Predicate<A> predicate) {
        HashSet<A> values2 = new HashSet<>(values.size());
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                values2.add(result);
            }
        }
        return values2;
    }

    public static <A, R> ArrayList<R> mapArray(A[] as, Function<? super A, ? extends R> function) {
        ArrayList<R> values = new ArrayList<>(as.length);
        A[] results = as;
        int value = as.length;
        for (int index = 0; index < value; index++) {
            A result;
            if ((result = results[index]) != null) {
                values.add(function.apply(result));
            }
        }
        return values;
    }

    public static <A, R> ArrayList<R> mapArrayList(
            Collection<A> values, Function<? super A, ? extends R> function) {
        ArrayList<R> values2 = new ArrayList<>(values.size());
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null) {
                values2.add(function.apply(result));
            }
        }
        return values2;
    }

    public static <A, R> Set<R> mapArrayListToSet(
            Collection<A> values, Function<? super A, ? extends R> function) {
        HashSet<R> values2 = new HashSet<>(values.size());
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null) {
                values2.add(function.apply(result));
            }
        }
        return values2;
    }

    public static <R, A> List<R> mapAndFilterArrayList(
            Collection<A> values,
            Predicate<A> predicate,
            Function<? super A, ? extends R> function) {
        ArrayList<R> values2 = new ArrayList<>(values.size());
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                values2.add(function.apply(result));
            }
        }
        return values2;
    }

    public static <R, A> Set<R> mapAndFilterArrayListToSet(
            Collection<A> values,
            Predicate<A> predicate,
            Function<? super A, ? extends R> function) {
        HashSet<R> values2 = new HashSet<>(values.size());
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                values2.add(function.apply(result));
            }
        }
        return values2;
    }

    public static <A> ConcurrentLinkedQueue<A> filterCLQ(
            Collection<A> values, Predicate<A> predicate) {
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
        Iterator<A> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            A result;
            if ((result = iterator2.next()) != null && predicate.test(result)) {
                concurrentLinkedQueue.add(result);
            }
        }
        return concurrentLinkedQueue;
    }

    private FastStreams() {
        throw new UnsupportedOperationException(text);
    }
}

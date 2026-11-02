package xyz.refinedev.phoenix.utils.repository.sorting.api;

import xyz.refinedev.phoenix.utils.repository.sorting.impl.SortsDescendingOptions;

import java.util.Arrays;

public final class Sorts {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static SortsDescendingOptions descending(String... text) {
        return new SortsDescendingOptions(Arrays.asList(text));
    }

    private Sorts() {
        throw new UnsupportedOperationException(text);
    }
}

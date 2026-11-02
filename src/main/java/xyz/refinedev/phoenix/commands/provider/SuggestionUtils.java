package xyz.refinedev.phoenix.commands.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public final class SuggestionUtils {

    private SuggestionUtils() {}

    public static <T> List<T> findList(
            Iterable<? extends T> iterable,
            Function<T, String> function,
            Predicate<T> predicate,
            String text) {
        ArrayList<T> values = new ArrayList<>();
        String text2 = text.toLowerCase(Locale.ROOT);
        for (T result : iterable) {
            if (predicate.test(result)) {
                String text3;
                if ((text3 = (String) function.apply(result)).equalsIgnoreCase(text)) {
                    values.clear();
                    values.add(result);
                    return values;
                }
                if (text3.toLowerCase(Locale.ROOT).contains(text2)) {
                    values.add(result);
                }
            }
        }
        return values;
    }
}

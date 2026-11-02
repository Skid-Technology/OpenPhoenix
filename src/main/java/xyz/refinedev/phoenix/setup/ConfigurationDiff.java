package xyz.refinedev.phoenix.setup;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ConfigurationDiff {

    private ConfigurationDiff() {}

    public static Map<String, Object> findMap(Map<?, ?> entries) {
        LinkedHashMap<String, Object> entries2 = new LinkedHashMap<>();
        applyString("", entries, entries2);
        return entries2;
    }

    private static void applyString(String text, Map<?, ?> entries, Map<String, Object> entries2) {
        Iterator iterator2 = entries.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            String text2 = String.valueOf((entry = (Entry) iterator2.next()).getKey());
            String text3 = text.isEmpty() ? text2 : text + "." + text2;
            Object result;
            if ((result = entry.getValue()) instanceof Map) {
                applyString(text3, (Map<?, ?>) result, entries2);
            } else {
                entries2.put(text3, result);
            }
        }
    }

    public static List<String> findList(Collection<String> values, String text, int value) {
        String text2 = text.toLowerCase(Locale.ROOT);
        List<String> values2;
        String text3;
        if ((values2 = findList(values, text2)).isEmpty()
                && text2.contains(".")
                && !(text3 = text2.substring(text2.lastIndexOf(46) + 1)).isEmpty()) {
            values2 = findList(values, text3);
        }
        return values2.stream().limit(value).collect(Collectors.toList());
    }

    private static List<String> findList(Collection<String> values, String text) {
        Stream<String> stream2 = values.stream();
        return stream2.filter(result -> result.toLowerCase(Locale.ROOT).contains(text))
                .sorted(
                        Comparator.comparingInt(String::length)
                                .thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList());
    }
}

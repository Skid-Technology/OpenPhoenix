package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public final class FeaturesCheck {

    public static final String text = "Features";

    private static final String textString =
            "A restriction names a bypass node that BypassNodes.ALL does not list.";

    public static List<SetupCheck> evaluate(Map<String, Boolean> entries) {
        return evaluate(entries, Collections.emptyList());
    }

    public static List<SetupCheck> evaluate(Map<String, Boolean> entries, List<String> values) {
        ArrayList<SetupCheck> values2 = new ArrayList<>();
        if (entries.isEmpty()) {
            values2.add(SetupCheck.of("Features", "No feature toggles were read."));
            return values2;
        }
        TreeMap treeMap = new TreeMap(entries);
        ArrayList<String> values3 = new ArrayList<>();
        int value = 0;
        for (Entry entry : (Iterable<Entry>) ((Iterable) treeMap.entrySet())) {
            boolean matches;
            if (matches = Boolean.TRUE.equals(entry.getValue())) {
                value++;
            }
            values3.add((matches ? "on  " : "off ") + (String) entry.getKey());
        }
        values2.add(
                SetupCheck.of("Features", value + " of " + treeMap.size() + " features are on.")
                        .of(values3.toArray(new String[0])));
        if (!values.isEmpty()) {
            values2.add(
                    SetupCheck.ofByFAIL(
                                    "Features",
                                    values.size()
                                            + " bypass permissions are not checked by the Bypasses area.",
                                    "A restriction names a bypass node that BypassNodes.ALL does not list.")
                            .of(values.toArray(new String[0])));
        }
        return Collections.unmodifiableList(values2);
    }

    private FeaturesCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

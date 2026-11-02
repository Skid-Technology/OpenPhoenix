package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class BypassesCheck {

    public static final String text = "Bypasses";

    private static final String textString =
            "/rank perm add <rank> <node> -- grant these to your staff ranks";

    public static List<SetupCheck> evaluate(Map<String, Boolean> entries) {
        return evaluate(entries, Collections.emptyMap());
    }

    public static List<SetupCheck> evaluate(
            Map<String, Boolean> entries, Map<String, Boolean> entries2) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        ArrayList<String> values2 = new ArrayList<>();
        for (String text : BypassPermissionCatalog.strings) {
            if (!Boolean.TRUE.equals(entries.get(text))) {
                values2.add(formatString(text, entries2));
            }
        }
        int size2 = BypassPermissionCatalog.strings.size();
        if (values2.isEmpty()) {
            values.add(
                    SetupCheck.of(
                            "Bypasses",
                            "All " + size2 + " bypass permissions are held by at least one rank."));
            return values;
        } else if (values2.size() == size2) {
            values.add(
                    SetupCheck.of(
                                    "Bypasses",
                                    "No rank holds any of the " + size2 + " bypass permissions.",
                                    "/rank perm add <rank> <node> -- grant these to your staff ranks")
                            .of(values2.toArray(new String[0])));
            return values;
        } else {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Bypasses",
                                    values2.size()
                                            + " of "
                                            + size2
                                            + " bypass permissions are held by nobody.",
                                    "/rank perm add <rank> <node> -- grant these to your staff ranks")
                            .of(values2.toArray(new String[0])));
            return values;
        }
    }

    private static String formatString(String text, Map<String, Boolean> entries) {
        Iterator<Entry<String, List<String>>> iterator2 =
                BypassPermissionCatalog.stringsByName.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if (((List) (entry = (Entry) iterator2.next()).getValue()).contains(text)) {
                Boolean matches;
                if ((matches = (Boolean) entries.get(entry.getKey())) == null) {
                    return text + " (" + (String) entry.getKey() + ")";
                }
                return text
                        + " ("
                        + (String) entry.getKey()
                        + ", "
                        + (matches ? "on now" : "off")
                        + ")";
            }
        }
        return text;
    }

    private BypassesCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

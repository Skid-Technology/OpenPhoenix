package xyz.refinedev.phoenix.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class PermissionCatalog {

    private final Map<String, Set<String>> stringsByName = new TreeMap<>();

    private final Map<String, String> stringsByNameMap = new HashMap<>();

    public void applyString(String text, String text2, String text3) {
        if (text != null && !text.trim().isEmpty() && !text.equals("*")) {
            Set<String> values =
                    this.stringsByName.computeIfAbsent(text, result -> new TreeSet<>());
            if (text2 != null) {
                values.add(text2);
            }
            if (text3 != null && !text3.isEmpty()) {
                this.stringsByNameMap.putIfAbsent(text, text3);
            }
        }
    }

    public int getCount() {
        return this.stringsByName.size();
    }

    public List<String> getList() {
        ArrayList<String> values;
        (values = new ArrayList()).add("# Phoenix permissions");
        values.add("");
        values.add("Written by /phoenix permissions from the commands this server has loaded.");
        values.add("");
        for (Entry<String, Set<String>> entry : this.stringsByName.entrySet()) {
            StringBuilder stringBuilder =
                    new StringBuilder("- `").append((String) entry.getKey()).append((char) (96));
            if (!((Set) entry.getValue()).isEmpty()) {
                stringBuilder
                        .append(" - ")
                        .append(
                                String.join(
                                        ", ", (Iterable<? extends CharSequence>) entry.getValue()));
            }
            String text;
            if ((text = this.stringsByNameMap.get(entry.getKey())) != null
                    && ((Set) entry.getValue()).size() <= 1) {
                stringBuilder.append(": ").append(text);
            }
            values.add(stringBuilder.toString());
        }
        return values;
    }
}

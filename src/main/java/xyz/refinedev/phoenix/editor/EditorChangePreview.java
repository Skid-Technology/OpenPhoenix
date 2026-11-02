package xyz.refinedev.phoenix.editor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

public final class EditorChangePreview {

    private static final String[] strings =
            new String[] {"permission", "name", "id", "filter", "type"};

    private EditorChangePreview() {}

    public static <T> List<String> findList(
            Gson gson, Collection<T> values2, Collection<T> values3, Function<T, String> function) {
        LinkedHashMap<T, T> entries = new LinkedHashMap();
        for (T result : values2) {
            entries.put(result, result);
        }
        ArrayList<String> values4 = new ArrayList<>();
        for (T result2 : values3) {
            T result3;
            if ((result3 = entries.remove(result2)) == null) {
                values4.add("&a  + &f" + (String) function.apply(result2));
            } else {
                applyList(
                        values4,
                        (String) function.apply(result2),
                        gson.toJsonTree(result3).getAsJsonObject(),
                        gson.toJsonTree(result2).getAsJsonObject());
            }
        }
        for (T result4 : entries.values()) {
            values4.add("&c  - &f" + (String) function.apply(result4));
        }
        return values4;
    }

    public static List<String> findList(Collection<EditorOperations> values) {
        ArrayList<String> values2 = new ArrayList<>();
        int value = 0;
        for (EditorOperations editorOperations : values) {
            if (values2.size() >= 20) {
                value++;
            } else {
                String text = matchesEnabled(editorOperations.getAction()) ? "&a  + " : "&c  - ";
                values2.add(
                        text
                                + "&f"
                                + editorOperations.getName()
                                + " &8- &7"
                                + editorOperations.getAction().toLowerCase()
                                + " "
                                + editorOperations.getEntity());
            }
        }
        if (value > 0) {
            values2.add("&7  and " + value + " more");
        }
        return values2;
    }

    private static boolean matchesEnabled(String text) {
        return "CREATED".equals(text) || "UPDATED".equals(text) || "APPLIED".equals(text);
    }

    private static void applyList(
            List<String> values, String text, JsonObject jsonObject, JsonObject jsonObject2) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
            linkedHashSet.add((String) entry.getKey());
        }
        for (Entry<String, JsonElement> entry2 : jsonObject.entrySet()) {
            linkedHashSet.add((String) entry2.getKey());
        }
        ArrayList<String> values2 = new ArrayList<>();
        ArrayList values3 = new ArrayList();
        int value = 0;
        for (String text2 : (Iterable<String>) ((Iterable) linkedHashSet)) {
            JsonElement jsonElement = jsonObject.get(text2);
            JsonElement jsonElement2 = jsonObject2.get(text2);
            if (jsonElement == null ? jsonElement2 != null : !jsonElement.equals(jsonElement2)) {
                if (isJsonArray(jsonElement) && isJsonArray(jsonElement2)) {
                    applyList(
                            values3,
                            text2,
                            jsonElement.getAsJsonArray(),
                            jsonElement2.getAsJsonArray());
                }
                if (values2.size() >= 6) {
                    value++;
                } else {
                    values2.add(formatString(text2, jsonElement, jsonElement2));
                }
            }
        }
        if (!values2.isEmpty()) {
            if (value > 0) {
                values2.add("&7and " + value + " more");
            }
            values.add("&e  ~ &f" + text + " &8- &7" + String.join("&7, ", values2));
            values.addAll(values3);
        }
    }

    private static void applyList(
            List<String> values2, String text, JsonArray jsonArray, JsonArray jsonArray2) {
        LinkedHashMap entries = new LinkedHashMap();
        for (JsonElement jsonElement : (Iterable<JsonElement>) ((Iterable) jsonArray)) {
            entries.put(jsonElement.toString(), jsonElement);
        }
        ArrayList<JsonElement> values3 = new ArrayList<>();
        for (JsonElement jsonElement2 : (Iterable<JsonElement>) ((Iterable) jsonArray2)) {
            if (entries.remove(jsonElement2.toString()) == null) {
                values3.add(jsonElement2);
            }
        }
        Set<String> values4 = findSet(values3);
        Set<String> values5 = findSet(entries.values());
        LinkedHashSet<String> linkedHashSet;
        (linkedHashSet = new LinkedHashSet(values4)).retainAll(values5);
        values4.removeAll(linkedHashSet);
        values5.removeAll(linkedHashSet);
        int value;
        if ((value = values4.size() + values5.size() + linkedHashSet.size()) != 0) {
            values2.add("&8      " + text + ":");
            int value2 = 0;
            for (String text2 : values4) {
                if (value2++ >= 12) {
                    break;
                }
                values2.add("&a        + &f" + text2);
            }
            for (String text3 : values5) {
                if (value2++ >= 12) {
                    break;
                }
                values2.add("&c        - &f" + text3);
            }
            for (String text4 : linkedHashSet) {
                if (value2++ >= 12) {
                    break;
                }
                values2.add("&e        ~ &f" + text4);
            }
            if (value > 12) {
                values2.add("&8        and " + (value - 12) + " more");
            }
        }
    }

    private static Set<String> findSet(Collection<JsonElement> values) {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        for (JsonElement jsonElement : values) {
            linkedHashSet.add(formatString(jsonElement));
        }
        return linkedHashSet;
    }

    private static String formatString(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return "none";
        }
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        }
        if (jsonElement.isJsonObject()) {
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            String[] texts = strings;
            int value = strings.length;
            for (int index = 0; index < value; index++) {
                String text = texts[index];
                JsonElement jsonElement2;
                if ((jsonElement2 = asJsonObject.get(text)) != null
                        && jsonElement2.isJsonPrimitive()) {
                    return jsonElement2.getAsString();
                }
            }
        }
        String text2;
        return (text2 = jsonElement.toString()).length() > 24
                ? text2.substring(0, 24) + "..."
                : text2;
    }

    private static String formatString(
            String text, JsonElement jsonElement, JsonElement jsonElement2) {
        if (isJsonArray(jsonElement) && isJsonArray(jsonElement2)) {
            int size2 = jsonElement.getAsJsonArray().size();
            int size3 = jsonElement2.getAsJsonArray().size();
            return size2 == size3 ? "&e" + text : "&e" + text + " &7" + size2 + " &8-> &7" + size3;
        } else {
            String text2 = formatStringByString(jsonElement);
            String text3 = formatStringByString(jsonElement2);
            return text2 != null && text3 != null
                    ? "&e" + text + " &f" + text2 + " &8-> &f" + text3 + "&7"
                    : "&e" + text;
        }
    }

    private static boolean isJsonArray(JsonElement jsonElement) {
        return jsonElement != null && jsonElement.isJsonArray();
    }

    private static String formatStringByString(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return "none";
        } else if (!jsonElement.isJsonPrimitive()) {
            return null;
        } else {
            String text;
            if ((text = jsonElement.getAsString()).isEmpty()) {
                return "empty";
            } else {
                return text.length() > 24 ? null : text;
            }
        }
    }
}

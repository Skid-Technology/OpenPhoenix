package xyz.refinedev.phoenix.editor.panel;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PanelUrl {

    private static final Map<String, List<PanelUrl.Entry>> entriesByName =
            new ConcurrentHashMap<>();

    private PanelUrl() {}

    private static String append(String text, String text2) {
        return text + "/" + text2;
    }

    public static void applyString(String text, String text2, String text3, JsonObject jsonObject) {
        if (jsonObject != null && text != null && text2 != null) {
            Map<String, List<xyz.refinedev.phoenix.editor.panel.PanelUrl.Entry>> entries =
                    entriesByName;
            String text4 = append(text, text2);
            entries.compute(
                    text4,
                    (result, result2) -> {
                        ArrayList values;
                        (values = result2 == null ? new ArrayList() : new ArrayList(result2))
                                .add(0, new PanelUrl.Entry(text, text2, text3, jsonObject));
                        return values.size() > 10 ? new ArrayList(values.subList(0, 10)) : values;
                    });
        }
    }

    public static List<PanelUrl.Entry> findList(String text, String text2) {
        List<xyz.refinedev.phoenix.editor.panel.PanelUrl.Entry> values;
        return (values = entriesByName.get(append(text, text2))) == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(values);
    }

    public static List<PanelUrl.Entry> getList() {
        ArrayList<PanelUrl.Entry> values2 = new ArrayList<>();
        for (List values3 : new LinkedHashMap<>(entriesByName).values()) {
            values2.addAll(values3);
        }
        values2.sort((result, result2) -> Long.compare(result2.getTime(), result.getTime()));
        return values2;
    }

    public static PanelUrl.Entry findEntry(String text) {
        Iterator<xyz.refinedev.phoenix.editor.panel.PanelUrl.Entry> iterator2 =
                getList().iterator();
        while (iterator2.hasNext()) {
            PanelUrl.Entry entry;
            if ((entry = (PanelUrl.Entry) iterator2.next()).getText().equals(text)) {
                return entry;
            }
        }
        return null;
    }

    public static void applyString(String text) {
        for (Map.Entry entry : entriesByName.entrySet()) {
            ArrayList<xyz.refinedev.phoenix.editor.panel.PanelUrl.Entry> values = new ArrayList<>();
            Iterator iterator2 = ((List) entry.getValue()).iterator();
            while (iterator2.hasNext()) {
                PanelUrl.Entry entry2;
                if (!(entry2 = (PanelUrl.Entry) iterator2.next()).getText().equals(text)) {
                    values.add(entry2);
                }
            }
            if (values.size() != ((List) entry.getValue()).size()) {
                entriesByName.put((String) entry.getKey(), values);
                return;
            }
        }
    }

    public static final class Entry {

        private final String text;

        private final long time;

        private final String textString;

        private final String text2;

        private final String text3;

        private final JsonObject jsonObject;

        Entry(String textString, String text2, String text3, JsonObject jsonObject2) {
            this.text = UUID.randomUUID().toString().substring(0, 8);
            this.time = System.currentTimeMillis();
            this.textString = textString;
            this.text2 = text2;
            this.text3 = text3;
            this.jsonObject = jsonObject2;
        }

        public String getText() {
            return this.text;
        }

        public long getTime() {
            return this.time;
        }

        public String getTextString() {
            return this.textString;
        }

        public String getText2() {
            return this.text2;
        }

        public String getText3() {
            return this.text3;
        }

        public JsonObject getJsonObject() {
            return this.jsonObject;
        }
    }
}

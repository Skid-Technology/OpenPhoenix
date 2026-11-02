package xyz.refinedev.phoenix.nametag.packet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NameTagState {

    private final Map<String, Integer> integersByName = new LinkedHashMap<>();

    private final Map<String, String> stringsByName = new LinkedHashMap<>();

    private boolean enabled;

    private boolean enabledEnabled;

    public boolean isEnabled() {
        if (this.enabled) {
            return false;
        }
        this.enabled = true;
        return true;
    }

    public void setEnabledEnabled() {
        this.enabledEnabled = true;
    }

    public void setEnabledEnabledByEnabledEnabled() {
        this.enabledEnabled = false;
    }

    public List<String> keySet() {
        return new ArrayList<>(this.integersByName.keySet());
    }

    public int[] getCountArray() {
        int[] values2 = new int[this.integersByName.size()];
        int value = 0;
        for (Integer value2 : this.integersByName.values()) {
            values2[value++] = value2;
        }
        return values2;
    }

    public Integer findInteger(String text) {
        return this.integersByName.get(text);
    }

    public boolean isEmpty() {
        return this.integersByName.isEmpty();
    }

    public int getCount() {
        return this.integersByName.size();
    }

    public void applyString(String text, int value) {
        this.integersByName.put(text, value);
    }

    public Integer remove(String text) {
        this.stringsByName.remove(text);
        return this.integersByName.remove(text);
    }

    public void applyList(List<String> values) {
        LinkedHashMap entries = new LinkedHashMap();
        for (String text : values) {
            Integer value;
            if ((value = this.integersByName.get(text)) != null) {
                entries.put(text, value);
            }
        }
        this.integersByName.clear();
        this.integersByName.putAll(entries);
    }

    public boolean matchesEnabled(String text, String text2) {
        String text3;
        if ((text3 = this.stringsByName.get(text)) != null && text3.equals(text2)) {
            return false;
        }
        this.stringsByName.put(text, text2);
        return true;
    }

    public void initialize() {
        this.integersByName.clear();
        this.stringsByName.clear();
        this.enabled = false;
        this.enabledEnabled = false;
    }

    public boolean isEnabledEnabled() {
        return this.enabledEnabled;
    }
}

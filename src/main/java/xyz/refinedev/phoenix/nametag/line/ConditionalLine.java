package xyz.refinedev.phoenix.nametag.line;

import java.util.Collections;
import java.util.List;

public final class ConditionalLine {

    public static final ConditionalLine conditionalLine =
            new ConditionalLine(Collections.emptyList(), 0);

    private final List<String> strings;

    private final int count;

    public ConditionalLine(List<String> values, int value) {
        this.strings = values == null ? Collections.emptyList() : values;
        this.count = Math.max(1, value);
    }

    public boolean isEnabled() {
        return this.strings.size() > 1;
    }

    public List<String> getStrings() {
        return this.strings;
    }

    public int getCount() {
        return this.count;
    }

    public int calculateCount(long value) {
        return !this.isEnabled() ? 0 : (int) (value / this.count % this.strings.size());
    }

    public boolean matchesEnabled(long value, long value2) {
        return this.isEnabled() && this.calculateCount(value) != this.calculateCount(value2);
    }

    public String isEmpty(long value, String text) {
        return this.strings.isEmpty() ? text : this.strings.get(this.calculateCount(value));
    }
}

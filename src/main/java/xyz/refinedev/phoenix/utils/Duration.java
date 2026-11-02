package xyz.refinedev.phoenix.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Duration {

    public static final long time = -5L;

    public static final Duration duration = new Duration(-5L, true);

    private static final List<String> strings =
            Collections.unmodifiableList(Arrays.asList("s", "m", "h", "d", "w", "mo", "y"));

    private static final Map<String, Long> longsByName = new HashMap<>();

    private static final Pattern pattern = Pattern.compile("(\\d+)\\s*([a-zA-Z]+)[\\s,]*");

    private static final Pattern patternPattern = Pattern.compile("^(.*?)(\\d+)([a-zA-Z]*)$");

    private final long timeTime;

    private final boolean enabled;

    private static void applyTime(long value, String... text) {
        String[] texts = text;
        int value2 = text.length;
        for (int index = 0; index < value2; index++) {
            String text2 = texts[index];
            longsByName.put(text2, value);
        }
    }

    private Duration(long timeTime, boolean enabled) {
        this.timeTime = timeTime;
        this.enabled = enabled;
    }

    public static Duration of(String text) {
        if (text == null) {
            return null;
        }
        String text2;
        if ((text2 = text.trim()).isEmpty()) {
            return null;
        }
        String text3;
        if (!(text3 = text2.toLowerCase(Locale.ROOT)).equals("perm")
                && !text3.equals("permanent")
                && !text3.equals("forever")) {
            Matcher matcher2 = pattern.matcher(text2);
            long value = 0L;
            int value2 = 0;
            while (matcher2.lookingAt()) {
                Long value3;
                if ((value3 = toLowerCase(matcher2.group(2))) == null) {
                    return null;
                }
                try {
                    value =
                            Math.addExact(
                                    value,
                                    Math.multiplyExact(Long.parseLong(matcher2.group(1)), value3));
                } catch (ArithmeticException | NumberFormatException exception) {
                    return null;
                }
                value2 = matcher2.end();
                matcher2.region(value2, text2.length());
            }
            return value2 == text2.length() && value > 0L ? new Duration(value, false) : null;
        } else {
            return duration;
        }
    }

    private static Long toLowerCase(String text) {
        return text.equals("M")
                ? longsByName.get("mo")
                : longsByName.get(text.toLowerCase(Locale.ROOT));
    }

    public static List<String> findList(String text, List<String> values, boolean matches2) {
        String text2 = text == null ? "" : text.trim();
        ArrayList<String> values2 = new ArrayList<>();
        Matcher matcher2;
        if ((matcher2 = patternPattern.matcher(text2)).matches()
                && (matcher2.group(1).isEmpty() || of(matcher2.group(1)) != null)) {
            String text3 = matcher2.group(1) + matcher2.group(2);
            String text4 = matcher2.group(3).toLowerCase(Locale.ROOT);
            Iterator<String> iterator2 = strings.iterator();
            while (iterator2.hasNext()) {
                String text5;
                if ((text5 = (String) iterator2.next()).startsWith(text4)) {
                    values2.add(text3 + text5);
                }
            }
        }
        String text6 = text2.toLowerCase(Locale.ROOT);
        for (String text7 : values) {
            if ((matches2 || of(text7) != duration)
                    && text7.toLowerCase(Locale.ROOT).startsWith(text6)
                    && !values2.contains(text7)) {
                values2.add(text7);
            }
        }
        if (matches2 && "perm".startsWith(text6) && !values2.contains("perm")) {
            values2.add("perm");
        }
        return values2;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public long getTimeTime() {
        return this.timeTime;
    }

    public boolean matchesEnabled(long value) {
        return value == -5L ? false : this.enabled || this.timeTime > value;
    }

    public String formatTimeMillis() {
        return this.enabled ? TimeConstants.PERMANENT : DateUtils.formatTimeMillis(this.timeTime);
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        }
        if (!(result instanceof Duration)) {
            return false;
        }
        Duration duration = (Duration) result;
        return this.timeTime == duration.timeTime && this.enabled == duration.enabled;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.timeTime) * 31 + (this.enabled ? 1 : 0);
    }

    @Override
    public String toString() {
        return this.enabled ? "permanent" : this.timeTime + "ms";
    }

    static {
        applyTime(TimeUnit.DAYS.toMillis(365L), "y", "yr", "yrs", "year", "years");
        applyTime(TimeUnit.DAYS.toMillis(30L), "mo", "mos", "mon", "mons", "month", "months");
        applyTime(TimeUnit.DAYS.toMillis(7L), "w", "wk", "wks", "week", "weeks");
        applyTime(TimeUnit.DAYS.toMillis(1L), "d", "day", "days");
        applyTime(TimeUnit.HOURS.toMillis(1L), "h", "hr", "hrs", "hour", "hours");
        applyTime(TimeUnit.MINUTES.toMillis(1L), "m", "min", "mins", "minute", "minutes");
        applyTime(TimeUnit.SECONDS.toMillis(1L), "s", "sec", "secs", "second", "seconds");
    }
}

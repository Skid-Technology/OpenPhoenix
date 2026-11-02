package xyz.refinedev.phoenix.utils;

import java.util.regex.Pattern;

public final class VersionUtils {

    private static final Pattern pattern = Pattern.compile("\\d+(\\.\\d+)*");

    private final int[] counts;

    private final String text;

    private VersionUtils(int[] counts, String text) {
        this.counts = counts;
        this.text = text;
    }

    public static VersionUtils of(String text) {
        if (text == null) {
            return null;
        }
        String text2;
        int size = (text2 = text.trim()).length();
        char[] values;
        int value = (values = new char[] {'-', '+', ' '}).length;
        for (int index = 0; index < value; index++) {
            char value2 = values[index];
            int value3;
            if ((value3 = text2.indexOf(value2)) >= 0) {
                size = Math.min(size, value3);
            }
        }
        text2 = text2.substring(0, size);
        if (!pattern.matcher(text2).matches()) {
            return null;
        }
        String[] texts;
        int[] values2 = new int[(texts = text2.split("\\.")).length];
        for (int index2 = 0; index2 < texts.length; index2++) {
            values2[index2] = Integer.parseInt(texts[index2]);
        }
        return new VersionUtils(values2, text2);
    }

    public boolean max(VersionUtils versionUtils) {
        int value = Math.max(this.counts.length, versionUtils.counts.length);
        for (int index = 0; index < value; index++) {
            int value2 = index < this.counts.length ? this.counts[index] : 0;
            int value3 = index < versionUtils.counts.length ? versionUtils.counts[index] : 0;
            if (value2 != value3) {
                return value2 > value3;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.text;
    }
}

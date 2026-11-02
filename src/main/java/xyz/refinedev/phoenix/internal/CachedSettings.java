package xyz.refinedev.phoenix.internal;

import java.util.Locale;

public final class CachedSettings {

    public static final byte byteValue = 0;

    public static final byte byteValueByte = 1;

    public static final byte byteValue2 = 2;

    public static final byte byteValue3 = 3;

    private final boolean enabled;

    private final boolean enabledEnabled;

    private final boolean enabled2;

    private final int countCount;

    private final byte byteValue7;

    private final float floatValue;

    private final byte byteValue8;

    private final float floatValueFloat;

    private final float floatValue2;

    private final boolean enabled3;

    private final int count2;

    public CachedSettings(
            boolean enabled,
            boolean enabledEnabled,
            String text,
            int value,
            float value2,
            String text2,
            float floatValueFloat,
            float floatValue2,
            boolean enabled3,
            int value5) {
        this.enabled = enabled;
        this.enabledEnabled = enabledEnabled;
        this.enabled2 = text == null || "default".equalsIgnoreCase(text.trim());
        this.countCount = calculateCount(text);
        this.byteValue7 = (byte) Math.max(0, Math.min(255, value));
        this.floatValue = value2 <= 0.0F ? 32.0F : value2;
        this.byteValue8 = findByte(text2);
        this.floatValueFloat = floatValueFloat;
        this.floatValue2 = floatValue2;
        this.enabled3 = enabled3;
        this.count2 = Math.max(0, value5);
    }

    public byte getByte() {
        byte value = 0;
        if (this.enabledEnabled) {
            value = 1;
        }
        if (this.enabled) {
            value = (byte) (value | 2);
        }
        if (this.enabled2) {
            value = (byte) (value | 4);
        }
        return value;
    }

    public byte findByte(int value, int value2) {
        if (value2 <= 0) {
            return this.byteValue7;
        }
        int value3 =
                (int)
                        Math.round(
                                (double) (this.byteValue7 & 0xFF)
                                        * Math.max(0, Math.min(value, value2))
                                        / value2);
        return (byte) Math.max(0, Math.min(255, value3));
    }

    private static int calculateCount(String text) {
        if (text == null) {
            return 1073741824;
        }
        String text2;
        if ((text2 = text.trim()).isEmpty() || "default".equalsIgnoreCase(text2)) {
            return 1073741824;
        }
        if ("none".equalsIgnoreCase(text2)) {
            return 0;
        }
        if (text2.startsWith("#")) {
            text2 = text2.substring(1);
        }
        try {
            if (text2.length() == 6) {
                return 0xFF000000 | Integer.parseInt(text2, 16);
            }
            if (text2.length() == 8) {
                return (int) Long.parseLong(text2, 16);
            }
        } catch (NumberFormatException numberFormatException) {
        }
        return 1073741824;
    }

    private static byte findByte(String text) {
        if (text == null) {
            return 3;
        }
        switch (text.trim().toUpperCase(Locale.ROOT)) {
            case "FIXED":
                return 0;
            case "VERTICAL":
                return 1;
            case "HORIZONTAL":
                return 2;
            case "CENTER":
            default:
                return 3;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isEnabledEnabled() {
        return this.enabledEnabled;
    }

    public boolean isEnabled2() {
        return this.enabled2;
    }

    public int getCountCount() {
        return this.countCount;
    }

    public byte getByteValue7() {
        return this.byteValue7;
    }

    public float getFloatValue() {
        return this.floatValue;
    }

    public byte getByteValue8() {
        return this.byteValue8;
    }

    public float getFloatValueFloat() {
        return this.floatValueFloat;
    }

    public float getFloatValue2() {
        return this.floatValue2;
    }

    public boolean isEnabled3() {
        return this.enabled3;
    }

    public int getCount2() {
        return this.count2;
    }
}

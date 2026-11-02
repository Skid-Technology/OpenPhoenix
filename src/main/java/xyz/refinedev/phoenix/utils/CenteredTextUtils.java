package xyz.refinedev.phoenix.utils;

public final class CenteredTextUtils {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static String formatString(String text) {
        if (text != null && !text.isEmpty()) {
            int value = 0;
            boolean matches = false;
            boolean matches2 = false;
            char[] values;
            int value2 = (values = text.toCharArray()).length;
            for (int index = 0; index < value2; index++) {
                char value3;
                if ((value3 = values[index]) == 167 || value3 == '&') {
                    matches = true;
                } else if (matches) {
                    matches = false;
                    matches2 = value3 == 'l' || value3 == 'L';
                } else {
                    DefaultFontInfo defaultFontInfo = DefaultFontInfo.getDefaultFontInfo(value3);
                    value +=
                            matches2
                                    ? defaultFontInfo.getBoldLength()
                                    : defaultFontInfo.getLength();
                    value++;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            value2 = value / 2;
            int value4 = 154 - value2;
            int value5 = DefaultFontInfo.SPACE.getLength() + 1;
            for (int index2 = 0; index2 < value4; index2 += value5) {
                stringBuilder.append(" ");
            }
            return stringBuilder + text;
        } else {
            return "";
        }
    }

    public static String formatStringBySPACE(String text) {
        if (text != null && !text.isEmpty()) {
            int value = 0;
            boolean matches = false;
            boolean matches2 = false;
            char[] values;
            int value2 = (values = text.toCharArray()).length;
            for (int index = 0; index < value2; index++) {
                char value3;
                if ((value3 = values[index]) == 167 || value3 == '&') {
                    matches = true;
                } else if (matches) {
                    matches = false;
                    matches2 = value3 == 'l' || value3 == 'L';
                } else {
                    DefaultFontInfo defaultFontInfo = DefaultFontInfo.getDefaultFontInfo(value3);
                    value +=
                            matches2
                                    ? defaultFontInfo.getBoldLength()
                                    : defaultFontInfo.getLength();
                    value++;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            value2 = value / 2;
            int value4 = 130 - value2;
            int value5 = DefaultFontInfo.SPACE.getLength() + 1;
            for (int index2 = 0; index2 < value4; index2 += value5) {
                stringBuilder.append(" ");
            }
            return stringBuilder + text;
        } else {
            return "";
        }
    }

    private CenteredTextUtils() {
        throw new UnsupportedOperationException(text);
    }
}

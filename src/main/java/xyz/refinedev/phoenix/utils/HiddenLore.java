package xyz.refinedev.phoenix.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class HiddenLore {

    private static final char COLOR_CHAR = 167;

    private static final String SEQUENCE_HEADER = "\u00a7r\u00a7n\u00a7r";

    private static final String SEQUENCE_FOOTER = "\u00a7r\u00a7o\u00a7r";

    public static String encodeString(String text) {
        return quote(stringToColors(text));
    }

    public static boolean hasHiddenString(String text) {
        return text.contains("\u00a7r\u00a7n\u00a7r") && text.contains("\u00a7r\u00a7o\u00a7r");
    }

    public static String extractHiddenString(String text) {
        return colorsToString(extract(text));
    }

    public static String replaceHiddenString(String text, String text2) {
        int value = text.indexOf("\u00a7r\u00a7n\u00a7r");
        int value2 = text.indexOf("\u00a7r\u00a7o\u00a7r");
        return value >= 0 && value2 >= 0
                ? text.substring(0, value + "\u00a7r\u00a7n\u00a7r".length())
                        + stringToColors(text2)
                        + text.substring(value2)
                : null;
    }

    private static String quote(String text) {
        return "\u00a7r\u00a7n\u00a7r" + text + "\u00a7r\u00a7o\u00a7r";
    }

    private static String extract(String text) {
        int value = text.indexOf("\u00a7r\u00a7n\u00a7r");
        int value2 = text.indexOf("\u00a7r\u00a7o\u00a7r");
        return text.substring(value + "\u00a7r\u00a7n\u00a7r".length(), value2);
    }

    private static String stringToColors(String text) {
        Charset charset = StandardCharsets.UTF_8;
        byte[] values;
        char[] values2 = new char[(values = text.getBytes(charset)).length * 4];
        byte value = 0;
        int value2 = values.length + -1;
        if (value <= value2) {
            do {
                byte value3 = value++;
                char[] values3 = byteToHex(values[value3]);
                values2[value3 * 4] = 167;
                values2[value3 * 4 + 1] = values3[0];
                values2[value3 * 4 + 2] = 167;
                values2[value3 * 4 + 3] = values3[1];
            } while (value <= value2);
        }
        return new String(values2);
    }

    private static String colorsToString(String text) {
        if ((text = text.toLowerCase().replace("\u00a7", "")).length() % 2 != 0) {
            text = text.substring(0, text.length() / 2 * 2);
        }
        char[] values;
        byte[] values2 = new byte[(values = text.toCharArray()).length / 2];
        for (byte index = 0; index < values.length; index += 2) {
            values2[index / 2] = hexToByte(values[index], values[index + 1]);
        }
        return new String(values2, StandardCharsets.UTF_8);
    }

    private static char unsignedIntToHex(int value) {
        if (value >= 0 && value < 10) {
            return (char) (value + 48);
        } else if (value >= 10 && value < 16) {
            return (char) (value + 87);
        } else {
            throw new IllegalArgumentException("Invalid hex int: out of range");
        }
    }

    private static int hexToUnsignedInt(char value) {
        if (value < '\n') {
            return Integer.parseInt(String.valueOf(value)) - 48;
        } else if ('a' <= value && value <= 'f') {
            return Integer.parseInt(String.valueOf(value)) - 87;
        } else {
            throw new IllegalArgumentException("Invalid hex char: out of range");
        }
    }

    private static byte hexToByte(char value, char value2) {
        return (byte) ((hexToUnsignedInt(value) << 4 | hexToUnsignedInt(value2)) + -128);
    }

    private static char[] byteToHex(Byte byteValue) {
        int value = byteValue.intValue() - -128;
        char[] values;
        (values = new char[2])[0] = unsignedIntToHex(value >> 4 & 15);
        values[1] = unsignedIntToHex(value & 15);
        return values;
    }

    private HiddenLore() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

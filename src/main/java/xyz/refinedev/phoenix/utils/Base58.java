package xyz.refinedev.phoenix.utils;

import java.math.BigInteger;
import java.util.Arrays;

public class Base58 {

    public static final char[] characters =
            "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    private static final char character = characters[0];

    private static final int[] counts = new int[128];

    public static String encode(byte[] values) {
        if (values.length == 0) {
            return "";
        }
        int value = 0;
        while (value < values.length && values[value] == 0) {
            value++;
        }
        char[] values2;
        byte[] values3;
        int value2 =
                (values2 = new char[(values3 = Arrays.copyOf(values, values.length)).length * 2])
                        .length;
        int value3 = value;
        while (value3 < values3.length) {
            values2[--value2] = characters[divmod(values3, value3, 256, 58)];
            if (values3[value3] == 0) {
                value3++;
            }
        }
        while (value2 < values2.length && values2[value2] == character) {
            value2++;
        }
        while (--value >= 0) {
            values2[--value2] = character;
        }
        return new String(values2, value2, values2.length - value2);
    }

    public static byte[] decode(String text) {
        if (text.length() == 0) {
            return new byte[0];
        }
        byte[] values = new byte[text.length()];
        for (int index = 0; index < text.length(); index++) {
            char value;
            int value2;
            if ((value2 = (value = text.charAt(index)) < 128 ? counts[value] : -1) < 0) {
                throw new IllegalArgumentException(
                        String.format(
                                "Invalid character in nl.kyllian.utils.Base58: 0x%04x",
                                Integer.valueOf(value)));
            }
            values[index] = (byte) value2;
        }
        int value3 = 0;
        while (value3 < values.length && values[value3] == 0) {
            value3++;
        }
        byte[] values2;
        int value4 = (values2 = new byte[text.length()]).length;
        int value5 = value3;
        while (value5 < values.length) {
            values2[--value4] = divmod(values, value5, 58, 256);
            if (values[value5] == 0) {
                value5++;
            }
        }
        while (value4 < values2.length && values2[value4] == 0) {
            value4++;
        }
        return Arrays.copyOfRange(values2, value4 - value3, values2.length);
    }

    public static BigInteger create(String text) {
        return new BigInteger(1, decode(text));
    }

    private static byte divmod(byte[] values, int value, int value2, int value3) {
        int value4 = 0;
        for (int value5 = value; value5 < values.length; value5++) {
            int value6 = values[value5] & 255;
            int value7 = value4 * value2 + value6;
            values[value5] = (byte) (value7 / value3);
            value4 = value7 % value3;
        }
        return (byte) value4;
    }

    static {
        Arrays.fill(counts, -1);
        int value = 0;
        while (value < characters.length) {
            counts[characters[value]] = value++;
        }
    }
}

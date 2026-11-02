package xyz.refinedev.phoenix.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HexCodec {

    public static final Charset charset = StandardCharsets.UTF_8;

    private static final char[] characters =
            new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
            };

    private static final char[] charactersCharacterArray =
            new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
            };

    private final Charset charsetCharset;

    public static byte[] decodeHex(String text) throws Exception {
        return decodeHex(text.toCharArray());
    }

    public static byte[] decodeHex(char[] values) throws Exception {
        int value;
        if (((value = values.length) & 1) != 0) {
            throw new Exception("Odd number of characters.");
        }
        byte[] values2 = new byte[value >> 1];
        int value2 = 0;
        for (int index = 0; index < value; value2++) {
            int value3 = toDigit(values[index], index) << 4;
            value3 |= toDigit(values[++index], index);
            index++;
            values2[value2] = (byte) (value3 & 0xFF);
        }
        return values2;
    }

    public static char[] encodeHex(byte[] values) {
        return encodeHex(values, true);
    }

    public static char[] decode(ByteBuffer byteBuffer) {
        return decode(byteBuffer, true);
    }

    public static char[] encodeHex(byte[] values, boolean matches) {
        return encodeHex(values, matches ? characters : charactersCharacterArray);
    }

    public static char[] decode(ByteBuffer byteBuffer, boolean matches) {
        return decode(byteBuffer, matches ? characters : charactersCharacterArray);
    }

    protected static char[] encodeHex(byte[] values, char[] values2) {
        int value;
        char[] values3 = new char[(value = values.length) << 1];
        int value2 = 0;
        int value3 = 0;
        while (value2 < value) {
            values3[value3++] = values2[(240 & values[value2]) >>> 4];
            values3[value3++] = values2[15 & values[value2]];
            value2++;
        }
        return values3;
    }

    protected static char[] decode(ByteBuffer byteBuffer, char[] values) {
        return encodeHex(byteBuffer.array(), values);
    }

    public static String encodeHexString(byte[] values) {
        return new String(encodeHex(values));
    }

    public static String encodeHexString(byte[] values, boolean matches) {
        return new String(encodeHex(values, matches));
    }

    public static String create(ByteBuffer byteBuffer) {
        return new String(decode(byteBuffer));
    }

    public static String create(ByteBuffer byteBuffer, boolean matches) {
        return new String(decode(byteBuffer, matches));
    }

    protected static int toDigit(char value, int value2) throws Exception {
        int value3;
        if ((value3 = Character.digit(value, 16)) == -1) {
            throw new Exception("Illegal hexadecimal character " + value + " at index " + value2);
        } else {
            return value3;
        }
    }

    public HexCodec() {
        this.charsetCharset = charset;
    }

    public HexCodec(Charset charset) {
        this.charsetCharset = charset;
    }

    public HexCodec(String text) {
        this(Charset.forName(text));
    }

    public byte[] decodeByByteArray(byte[] values) throws Exception {
        return decodeHex(new String(values, this.getCharsetCharset()).toCharArray());
    }

    public byte[] decodeByByteArray(ByteBuffer byteBuffer) throws Exception {
        return decodeHex(new String(byteBuffer.array(), this.getCharsetCharset()).toCharArray());
    }

    public Object findObject(Object result) throws Exception {
        if (result instanceof String) {
            return this.findObject(((String) result).toCharArray());
        }
        if (result instanceof byte[]) {
            return this.decodeByByteArray((byte[]) result);
        }
        if (result instanceof ByteBuffer) {
            return this.decodeByByteArray((ByteBuffer) result);
        }
        try {
            return decodeHex((char[]) result);
        } catch (ClassCastException classCastException) {
            throw new Exception(classCastException.getMessage(), classCastException);
        }
    }

    public byte[] decode2(byte[] values) {
        return encodeHexString(values).getBytes(this.getCharsetCharset());
    }

    public byte[] decodeByByteBuffer(ByteBuffer byteBuffer) {
        return create(byteBuffer).getBytes(this.getCharsetCharset());
    }

    public Object findObjectByObject(Object result) throws Exception {
        byte[] values;
        if (result instanceof String) {
            values = ((String) result).getBytes(this.getCharsetCharset());
        } else if (result instanceof ByteBuffer) {
            values = ((ByteBuffer) result).array();
        } else {
            try {
                values = (byte[]) result;
            } catch (ClassCastException classCastException) {
                throw new Exception(classCastException.getMessage(), classCastException);
            }
        }
        return encodeHex(values);
    }

    public String get() {
        return this.charsetCharset.name();
    }

    @Override
    public String toString() {
        return super.toString() + "[charsetName=" + this.charsetCharset + "]";
    }

    public Charset getCharsetCharset() {
        return this.charsetCharset;
    }
}

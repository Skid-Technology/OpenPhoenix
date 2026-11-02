package xyz.refinedev.phoenix.editor.panel;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class PanelLinkCode {

    public static final long time = 90000L;

    private static final char[] characters = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();

    private static final SecureRandom random = new SecureRandom();

    private PanelLinkCode() {}

    public static String getString() {
        byte[] values = new byte[32];
        random.nextBytes(values);
        return Base64.getEncoder().encodeToString(values);
    }

    public static String formatString(String text, long value) {
        return append(text, calculateTime(value));
    }

    public static boolean matchesEnabled(String text, String text2, long value) {
        if (text != null && text2 != null) {
            long value2 = calculateTime(value);
            boolean matches = false;
            for (long value3 = -1L; value3 <= 1L; value3++) {
                if (matchesEnabled(append(text, value2 + value3), text2)) {
                    matches = true;
                }
            }
            return matches;
        } else {
            return false;
        }
    }

    private static long calculateTime(long value) {
        return value / 90000L;
    }

    private static String append(String text, long value) {
        byte[] values = calculateByteArray(text, Long.toString(value));
        StringBuilder stringBuilder = new StringBuilder(26);
        for (int index = 0; index < 26; index++) {
            stringBuilder.append(characters[values[index % values.length] & 31]);
        }
        return stringBuilder.toString();
    }

    private static byte[] calculateByteArray(String text, String text2) {
        try {
            Mac mac;
            (mac = Mac.getInstance("HmacSHA256"))
                    .init(new SecretKeySpec(text.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(text2.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException generalSecurityException) {
            throw new IllegalStateException("HmacSHA256 unavailable", generalSecurityException);
        }
    }

    private static boolean matchesEnabled(String text, String text2) {
        return MessageDigest.isEqual(
                text.getBytes(StandardCharsets.UTF_8), text2.getBytes(StandardCharsets.UTF_8));
    }
}

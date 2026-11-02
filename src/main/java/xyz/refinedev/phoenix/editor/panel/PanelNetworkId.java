package xyz.refinedev.phoenix.editor.panel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PanelNetworkId {

    private static final char[] characters = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();

    private PanelNetworkId() {}

    public static String formatString(String text) {
        if (text != null && !text.isEmpty()) {
            byte[] values = calculateByteArray("phoenix-panel-network:" + text);
            StringBuilder stringBuilder = new StringBuilder(6);
            for (int index = 0; index < 6; index++) {
                stringBuilder.append(characters[values[index] & 31]);
            }
            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    private static byte[] calculateByteArray(String text) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new IllegalStateException("SHA-256 unavailable", noSuchAlgorithmException);
        }
    }
}

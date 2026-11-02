package xyz.refinedev.phoenix.setup;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Random;

public final class ClaimCode {

    public static final int count = 5;

    private static final char[] characters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final String text;

    private int countCount;

    private boolean enabled;

    public ClaimCode(Random random) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < 10; index++) {
            if (index == 5) {
                stringBuilder.append('-');
            }
            stringBuilder.append(characters[random.nextInt(characters.length)]);
        }
        this.text = stringBuilder.toString();
    }

    public String getText() {
        return this.text;
    }

    public synchronized int max() {
        return Math.max(0, 5 - this.countCount);
    }

    public synchronized ClaimCode.Result findResult(String text2) {
        if (!this.enabled && this.countCount < 5) {
            byte[] bytes = formatString(this.text).getBytes(StandardCharsets.UTF_8);
            byte[] bytes2 = formatString(text2).getBytes(StandardCharsets.UTF_8);
            if (MessageDigest.isEqual(bytes, bytes2)) {
                this.enabled = true;
                return ClaimCode.Result.ACCEPTED;
            } else {
                this.countCount++;
                return this.countCount >= 5 ? ClaimCode.Result.LOCKED : ClaimCode.Result.REJECTED;
            }
        } else {
            return ClaimCode.Result.CLOSED;
        }
    }

    private static String formatString(String text) {
        return text == null ? "" : text.replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
    }

    public enum Result {
        ACCEPTED,
        REJECTED,
        LOCKED,
        CLOSED;

        private static final ClaimCode.Result[] $VALUES = $values();

        private static ClaimCode.Result[] $values() {
            return new ClaimCode.Result[] {ACCEPTED, REJECTED, LOCKED, CLOSED};
        }
    }
}

package xyz.refinedev.phoenix.handler.database;

import xyz.refinedev.phoenix.utils.ErrorReporter;

public final class DatabaseProbeResult {

    private final String text;

    private final long time;

    private final String textString;

    private final String text2;

    private final boolean enabled;

    private static final String text3 = "...";

    private static final long timeTime = -1L;

    private DatabaseProbeResult(
            String text, long time, String textString, String text2, boolean enabled) {
        this.text = text;
        this.time = time;
        this.textString = textString;
        this.text2 = text2;
        this.enabled = enabled;
    }

    public static DatabaseProbeResult of(String text, long value) {
        return new DatabaseProbeResult(text, value, null, null, false);
    }

    public static DatabaseProbeResult of(String text, Throwable throwable) {
        String text2;
        if ((text2 = String.valueOf(ErrorReporter.getCause(throwable).getMessage())).length()
                > 160) {
            text2 = text2.substring(0, 157) + text3;
        }
        return new DatabaseProbeResult(
                text, timeTime, text2, DatabaseErrorClassifier.formatString(throwable), false);
    }

    public static DatabaseProbeResult of(String text, String text2) {
        return new DatabaseProbeResult(text, 0L, null, text2, true);
    }

    public boolean isEnabledByTextString() {
        return this.textString == null;
    }

    public String getText() {
        return this.text;
    }

    public long getTime() {
        return this.time;
    }

    public String getTextString() {
        return this.textString;
    }

    public String getText2() {
        return this.text2;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

package xyz.refinedev.phoenix.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildInfo {

    private final String text;

    private final String textString;

    private final String text2;

    private BuildInfo(Properties properties) {
        this.text = formatString(properties, "phoenix.silent");
        this.textString = formatString(properties, "git.commit.id.abbrev");
        this.text2 = formatString(properties, "git.branch");
    }

    public static BuildInfo of(InputStream inputStream) {
        Properties properties = new Properties();
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException oException) {
            }
        }
        return new BuildInfo(properties);
    }

    public String append(String text2) {
        return this.text == null ? text2 : text2 + " (" + this.text + ")";
    }

    private static String formatString(Properties properties, String text) {
        String text2;
        if ((text2 = properties.getProperty(text)) == null) {
            return null;
        }
        String text3;
        return !(text3 = text2.trim()).isEmpty() && !text3.startsWith("${") ? text3 : null;
    }

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public String getText2() {
        return this.text2;
    }
}

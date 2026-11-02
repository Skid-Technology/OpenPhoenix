package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.snakeyml.Yaml;
import xyz.refinedev.lib.snakeyml.error.Mark;
import xyz.refinedev.lib.snakeyml.error.MarkedYAMLException;
import xyz.refinedev.lib.snakeyml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class YamlProblem {

    private YamlProblem() {}

    public static String find(File file) {
        if (!file.exists()) {
            return null;
        }
        try {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            String text;
            try {
                text = find(file.getName(), inputStreamReader);
            } catch (Throwable throwable) {
                try {
                    inputStreamReader.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            inputStreamReader.close();
            return text;
        } catch (IOException oException) {
            return file.getName() + " could not be read: " + oException.getMessage();
        }
    }

    public static String find(String text, String text2) {
        return find(text, new StringReader(text2));
    }

    private static String find(String text, Reader reader) {
        try {
            new Yaml().compose(reader);
            return null;
        } catch (MarkedYAMLException markedYAMLException) {
            Mark mark;
            String text2 =
                    (mark = markedYAMLException.getProblemMark()) == null
                            ? text
                            : text
                                    + " line "
                                    + (mark.getLine() + 1)
                                    + ", column "
                                    + (mark.getColumn() + 1);
            String text3 =
                    markedYAMLException.getProblem() == null
                            ? markedYAMLException.getMessage()
                            : markedYAMLException.getProblem();
            return text2 + ": " + text3 + hint(text3);
        } catch (YAMLException yAMLException) {
            return text + ": " + yAMLException.getMessage();
        }
    }

    private static String hint(String text) {
        String text2;
        if ((text2 = text.toLowerCase(Locale.ROOT)).contains("tab")) {
            return ". Indent with spaces, not tabs.";
        } else if (text2.contains("mapping values are not allowed")) {
            return ". A value probably contains a colon; wrap it in quotes.";
        } else if (text2.contains("could not find expected ':'")) {
            return ". A key is missing its colon, or a quote was never closed.";
        } else {
            return !text2.contains("expected ',' or ']'") && !text2.contains("expected ',' or '}'")
                    ? "."
                    : ". A list or map was never closed.";
        }
    }
}

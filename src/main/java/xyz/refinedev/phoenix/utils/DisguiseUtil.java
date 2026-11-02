package xyz.refinedev.phoenix.utils;

import xyz.refinedev.phoenix.Phoenix;

import java.lang.reflect.Field;
import java.util.logging.Level;

public final class DisguiseUtil {

    public static void updateName(Object result, String text) {
        try {
            Field field;
            (field = result.getClass().getDeclaredField("name")).setAccessible(true);
            field.set(result, text);
        } catch (Exception exception) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, ErrorReporter.formatString(exception));
        }
    }

    private DisguiseUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

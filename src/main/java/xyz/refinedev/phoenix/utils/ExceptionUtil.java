package xyz.refinedev.phoenix.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {

    private ExceptionUtil() {}

    public static String getStackTrace(Throwable exception) {
        StringWriter output = new StringWriter();
        exception.printStackTrace(new PrintWriter(output));
        return output.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException propagate(Throwable exception) throws T {
        throw (T) exception;
    }
}

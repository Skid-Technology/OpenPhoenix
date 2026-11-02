package xyz.refinedev.phoenix.setup;

import xyz.refinedev.lib.io.lettuce.core.SetArgs.Builder;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;

import java.util.Locale;

public final class SetupLock {

    private static final long time = 10L;

    public static boolean matchesEnabled(Phoenix phoenix, String text, String text2) {
        try {
            IDatabaseHandler databaseHandler = phoenix.getDatabaseHandler();
            String text3 =
                    databaseHandler.runRedisCommand(
                            result ->
                                    result.set(
                                            formatString(text),
                                            text2 + " on " + GlobalConfig.SERVER.NAME,
                                            Builder.nx().ex(time)));
            return "OK".equalsIgnoreCase(text3);
        } catch (Throwable throwable) {
            return true;
        }
    }

    public static String formatString(Phoenix phoenix, String text) {
        try {
            IDatabaseHandler databaseHandler = phoenix.getDatabaseHandler();
            return databaseHandler.runRedisCommand(
                    result -> (String) result.get(formatString(text)));
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static void applyPhoenix(Phoenix phoenix, String text) {
        try {
            IDatabaseHandler databaseHandler = phoenix.getDatabaseHandler();
            databaseHandler.runRedisCommandAsync(
                    result -> result.del(new String[] {formatString(text)}));
        } catch (Throwable throwable) {
        }
    }

    private static String formatString(String text) {
        return "px-setup-lock:" + text.toLowerCase(Locale.ROOT).replace(' ', '-');
    }

    private SetupLock() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

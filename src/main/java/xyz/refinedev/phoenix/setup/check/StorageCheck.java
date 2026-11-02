package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class StorageCheck {

    public static final String STORAGE_TEXT = "Storage";

    public static final long time = 50L;

    public static final long timeTime = 1000L;

    private static final String text = "Edit the database and redis sections of global.yml.";

    public static List<SetupCheck> evaluate(long value, long value2, long value3, boolean matches) {
        return evaluate(value, value2, value3, matches, false);
    }

    public static List<SetupCheck> evaluate(
            long value, long value2, long value3, boolean matches, boolean matches2) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (matches2) {
            values.add(
                    SetupCheck.of("Storage", "Redis is off; this server keeps shared state itself.")
                            .of(
                                    "Turn redis on in global.yml before adding a second server or a proxy."));
        } else if (value < 0L) {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Storage",
                                    "Redis did not answer a ping.",
                                    "Edit the database and redis sections of global.yml.")
                            .of(
                                    "Without redis nothing crosses servers: not punishments, not ranks, not chat."));
        } else if (value > 50L) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Storage",
                                    "Redis answered in " + value + "ms.",
                                    "Edit the database and redis sections of global.yml.")
                            .of("Over 50ms, cross-server sync visibly lags behind."));
        } else {
            values.add(SetupCheck.of("Storage", "Redis answered in " + value + "ms."));
        }
        String text = matches ? "MongoDB" : "MySQL";
        if (value2 < 0L) {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Storage",
                                    text + " did not answer.",
                                    "Edit the database and redis sections of global.yml.")
                            .of("Profiles, punishments and grants all live here."));
        } else if (value2 > 1000L) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Storage",
                                    text + " counted " + value3 + " profiles in " + value2 + "ms.",
                                    "Edit the database and redis sections of global.yml.")
                            .of("That is slow; check the database host and its indexes."));
        } else {
            values.add(
                    SetupCheck.of(
                            "Storage",
                            text + " counted " + value3 + " profiles in " + value2 + "ms."));
        }
        return values;
    }

    private StorageCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

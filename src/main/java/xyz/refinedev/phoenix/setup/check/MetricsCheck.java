package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MetricsCheck {

    public static final String text = "Metrics";

    public static final String textString = "http://127.0.0.1:8086";

    private static final String text2 = "Set the metrics section of global.yml.";

    public static List<SetupCheck> evaluate(
            boolean matches, String text, boolean matches2, String text2) {
        if (!matches) {
            return Collections.singletonList(
                    SetupCheck.of("Metrics", "The metrics exporter is off.")
                            .of(
                                    "Off is the shipped state. Turn it on only if you run an InfluxDB."));
        }
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (!matches2) {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Metrics",
                                    "Metrics are on with no API token.",
                                    "Set the metrics section of global.yml.")
                            .of("Every write is refused by InfluxDB, and nothing is exported."));
        }
        if (matchesEnabled(text)) {
            values.add(
                    SetupCheck.ofByFAIL(
                            "Metrics",
                            "Metrics are on with no url.",
                            "Set the metrics section of global.yml."));
        } else if ("http://127.0.0.1:8086".equals(text)) {
            values.add(
                    SetupCheck.of(
                                    "Metrics",
                                    "The url is still the shipped http://127.0.0.1:8086.",
                                    "Set the metrics section of global.yml.")
                            .of("Correct only if InfluxDB runs on this same machine."));
        } else {
            values.add(SetupCheck.of("Metrics", "Exporting to " + text + "."));
        }
        if (matchesEnabled(text2)) {
            values.add(
                    SetupCheck.ofByFAIL(
                            "Metrics",
                            "No bucket is set.",
                            "Set the metrics section of global.yml."));
        }
        return values;
    }

    private static boolean matchesEnabled(String text) {
        return text == null || text.trim().isEmpty();
    }

    private MetricsCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

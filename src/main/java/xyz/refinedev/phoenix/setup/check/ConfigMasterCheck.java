package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class ConfigMasterCheck {

    public static final String text = "Config master";

    private static final String textString =
            "/phoenix config set-master on the server whose configs are correct";

    public static List<SetupCheck> evaluate(String text, String text2, boolean matches, int value) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (matchesEnabled(text)) {
            if (value > 1) {
                values.add(
                        SetupCheck.ofByWARN(
                                        "Config master",
                                        "No config master is set across " + value + " servers.",
                                        "/phoenix config set-master on the server whose configs are correct")
                                .of(
                                        "Every server keeps its own wording, so a message edit has to be repeated",
                                        "once per box and drifts the moment one is missed."));
            } else {
                values.add(
                        SetupCheck.of(
                                        "Config master",
                                        "No config master is set.",
                                        "/phoenix config set-master on the server whose configs are correct")
                                .of("Worth setting before the second server joins, not after."));
            }
            return values;
        } else if (text.equals(text2)) {
            values.add(
                    SetupCheck.of(
                            "Config master", "This server ('" + text + "') is the config master."));
            return values;
        } else if (!matches) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Config master",
                                    "The config master '" + text + "' is not online.",
                                    "/phoenix config set-master on the server whose configs are correct")
                            .of("Edits made here will be overwritten when it comes back."));
            return values;
        } else {
            values.add(
                    SetupCheck.of("Config master", "The config master is '" + text + "'.")
                            .of(
                                    "Edit wording there, not here -- this server's copies get overwritten."));
            return values;
        }
    }

    private static boolean matchesEnabled(String text) {
        return text == null || text.trim().isEmpty();
    }

    private ConfigMasterCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

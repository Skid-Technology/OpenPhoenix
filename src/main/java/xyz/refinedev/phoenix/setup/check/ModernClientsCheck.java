package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class ModernClientsCheck {

    public static final String text = "Modern clients";

    private static final String textString =
            "Update the server, or switch the setting off in settings.yml.";

    public static List<SetupCheck> evaluate(
            String text,
            boolean matches,
            boolean matches2,
            boolean matches3,
            boolean matches4,
            boolean matches5,
            boolean matches6) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        ArrayList<String> values2 = new ArrayList<>();
        if (matches && !matches2) {
            values2.add("modern.server-links needs a 1.21+ server");
        }
        if (matches3 && !matches4) {
            values2.add("modern.tablist-order needs a 1.21.2+ server");
        }
        if (matches5 && !matches6) {
            values2.add("modern.dialogs needs a 1.21.6+ server");
        }
        if (!values2.isEmpty()) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Modern clients",
                                    values2.size()
                                            + " modern settings are on against "
                                            + text
                                            + ".",
                                    "Update the server, or switch the setting off in settings.yml.")
                            .of(values2.toArray(new String[0])));
            return values;
        }
        ArrayList<String> values3 = new ArrayList<>();
        if (matches) {
            values3.add("server links");
        }
        if (matches3) {
            values3.add("tab list order");
        }
        if (matches5) {
            values3.add("native forms");
        }
        if (values3.isEmpty()) {
            values.add(
                    SetupCheck.of(
                                    "Modern clients",
                                    "No modern client features are on.",
                                    "Turn on what your server version supports in settings.yml.")
                            .of(
                                    "This server is "
                                            + text
                                            + ". Each feature is skipped for clients too old for it,",
                                    "so turning one on costs nothing on a mixed-version network."));
            return values;
        } else {
            values.add(
                    SetupCheck.of(
                                    "Modern clients",
                                    values3.size()
                                            + " modern client features are on, on "
                                            + text
                                            + ".")
                            .of(values3.toArray(new String[0])));
            return values;
        }
    }

    private ModernClientsCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

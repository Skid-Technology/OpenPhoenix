package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class LocalesCheck {

    public static final String text = "Locales";

    private static final String textString =
            "Add plugins/Phoenix/locales/messages_<code>.yml, holding only the lines you translate.";

    public static List<SetupCheck> evaluate(int value, List<String> values, boolean matches) {
        ArrayList<SetupCheck> values2 = new ArrayList<>();
        if (value <= 0) {
            values2.add(
                    SetupCheck.of(
                                    "Locales",
                                    "No translations are installed.",
                                    "Add plugins/Phoenix/locales/messages_<code>.yml, holding only the lines you translate.")
                            .of(
                                    "Everybody reads the default language. Fine, unless your players do not."));
            return values2;
        }
        values2.add(
                SetupCheck.of("Locales", value + " translations are installed.")
                        .of(values.toArray(new String[0])));
        if (!matches) {
            values2.add(
                    SetupCheck.ofByWARN(
                                    "Locales",
                                    "Translations added here are overwritten by the config master.",
                                    "Add them on the master instead, or make this server the master.")
                            .of("Locale overlays are pushed with messages.yml and menus.yml."));
        }
        return values2;
    }

    private LocalesCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TabListCheck {

    public static final String text = "Tab list";

    private static final String[] strings =
            new String[] {"Phoenix Core", "dsc.gg/refine", "refinedev.org"};

    private static final String textString =
            "Set tablist.header and tablist.footer in settings.yml.";

    private static final String text2 = "Set tablist.enabled in settings.yml.";

    public static List<SetupCheck> evaluate(
            boolean matches, String text, String text2, boolean matches2) {
        if (!matches) {
            return Collections.singletonList(
                    SetupCheck.of("Tab list", "Phoenix does not write the tab list.")
                            .of(
                                    "Correct if another plugin owns it; two writing the same list flicker."));
        }
        ArrayList<SetupCheck> values = new ArrayList<>();
        ArrayList<String> values2 = new ArrayList<>();
        if (contains(text)) {
            values2.add("tablist.header");
        }
        if (contains(text2)) {
            values2.add("tablist.footer");
        }
        if (values2.isEmpty()) {
            values.add(SetupCheck.of("Tab list", "The tab list header and footer are your own."));
        } else {
            values.add(
                    SetupCheck.of(
                                    "Tab list",
                                    "The tab list still advertises Phoenix and its Discord.",
                                    "Set tablist.header and tablist.footer in settings.yml.")
                            .of(values2.toArray(new String[0])));
        }
        if (!matches2) {
            values.add(
                    SetupCheck.of(
                                    "Tab list",
                                    "Rank nametags are off.",
                                    "Set server.nametags in settings.yml.")
                            .of(
                                    "Ranks show in chat and in the tab list, but not above players' heads."));
        }
        return values;
    }

    private static boolean contains(String text) {
        if (text == null) {
            return false;
        }
        String[] texts = strings;
        int value = strings.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            if (text.contains(text2)) {
                return true;
            }
        }
        return false;
    }

    private TabListCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

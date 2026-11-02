package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class ColoursCheck {

    public static final String text = "Colours";

    public static final String textString = "&c";

    public static final String text2 = "&4";

    public static final String text3 = "&f";

    private static final String text4 =
            "Set network.colors main, secondary and middle in global.yml.";

    public static List<SetupCheck> evaluate(String text, String text2, String text3) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (!matchesEnabled(text) && !matchesEnabled(text2) && !matchesEnabled(text3)) {
            if ("&c".equals(text) && "&4".equals(text2) && "&f".equals(text3)) {
                values.add(
                        SetupCheck.of(
                                        "Colours",
                                        "The colours are still the shipped red set.",
                                        "Set network.colors main, secondary and middle in global.yml.")
                                .of("Three keys recolour every message, menu and MOTD at once."));
            } else {
                values.add(
                        SetupCheck.of(
                                "Colours",
                                "Colours are set to "
                                        + text
                                        + ", "
                                        + text2
                                        + " and "
                                        + text3
                                        + "."));
            }
            if (text.equals(text2)) {
                values.add(
                        SetupCheck.ofByWARN(
                                        "Colours",
                                        "main and secondary are the same token.",
                                        "Set network.colors main, secondary and middle in global.yml.")
                                .of("Anything relying on the contrast between them renders flat."));
            }
            return values;
        } else {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Colours",
                                    "One of the three colour tokens is empty.",
                                    "Set network.colors main, secondary and middle in global.yml.")
                            .of(
                                    "Every message, menu and MOTD reads these; an empty one renders as no colour at all."));
            return values;
        }
    }

    private static boolean matchesEnabled(String text) {
        return text == null || text.trim().isEmpty();
    }

    private ColoursCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

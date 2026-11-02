package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class RanksCheck {

    public static final String text = "Ranks";

    private static final String textString = "/rank tools import-preset";

    private static final String text2 = "/rank editor -- mark your staff ranks as staff";

    public static List<SetupCheck> evaluate(int value, int value2) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (value <= 0) {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Ranks",
                                    "No ranks exist at all, not even the default.",
                                    "/rank tools import-preset")
                            .of(
                                    "Every player needs a default rank for prefixes and permissions to resolve."));
            return values;
        }
        if (value == 1) {
            values.add(
                    SetupCheck.of(
                                    "Ranks",
                                    "Only the default rank exists.",
                                    "/rank tools import-preset")
                            .of(
                                    "A starter ladder ships with the plugin; load it and rename what you want",
                                    "rather than building one from a blank page."));
            return values;
        }
        values.add(SetupCheck.of("Ranks", value + " ranks exist."));
        if (value2 <= 0) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Ranks",
                                    "No rank is marked as a staff rank.",
                                    "/rank editor -- mark your staff ranks as staff")
                            .of(
                                    "Staff lists, staff nametags and staff activity all read this flag."));
        }
        return values;
    }

    private RanksCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

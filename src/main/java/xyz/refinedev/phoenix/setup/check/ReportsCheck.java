package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ReportsCheck {

    public static final String text = "Reports";

    private static final String textString =
            "/reports categories create -- one per reason players should be able to pick";

    private static final String text2 =
            "/punishmentladder create -- build the ladder, or point the category at one that exists";

    private static final String text3 = "Switch reports on in the Features area.";

    public static List<SetupCheck> evaluate(
            boolean matches,
            int value,
            List<String> values,
            boolean matches2,
            String text,
            boolean matches3,
            boolean matches4,
            int value2) {
        if (!matches) {
            return Collections.singletonList(
                    SetupCheck.of(
                                    "Reports",
                                    "Reports are switched off.",
                                    "Switch reports on in the Features area.")
                            .of(
                                    "Players have no way to tell staff about anybody, and /report is not registered."));
        }
        ArrayList<SetupCheck> values2 = new ArrayList<>();
        if (value <= 0) {
            values2.add(
                    SetupCheck.of(
                                    "Reports",
                                    "Reports are on with no categories to report under.",
                                    "/reports categories create -- one per reason players should be able to pick")
                            .of(
                                    "/report opens an empty picker, which reads to a player as a broken command."));
            return values2;
        }
        values2.add(SetupCheck.of("Reports", value + " report categories exist."));
        if (!values.isEmpty()) {
            values2.add(
                    SetupCheck.ofByWARN(
                                    "Reports",
                                    values.size()
                                            + " of them name a punishment ladder that does not exist.",
                                    "/punishmentladder create -- build the ladder, or point the category at one that exists")
                            .of(values.toArray(new String[0])));
        }
        if (matches2 && !matches3) {
            values2.add(
                    SetupCheck.ofByFAIL(
                                    "Reports",
                                    "Typed reasons are filed under '"
                                            + text
                                            + "', which does not exist.",
                                    "/reports categories create -- one per reason players should be able to pick")
                            .of(
                                    "Every report a player writes themselves is lost until this category is created."));
        }
        if (matches4 && value2 <= 0) {
            values2.add(
                    SetupCheck.ofByWARN(
                                    "Reports",
                                    "The pending-review mute can never trigger.",
                                    "/reports categories create -- one per reason players should be able to pick")
                            .of(
                                    "It counts reports on categories that require a chat snapshot, and none of yours do."));
        }
        return values2;
    }

    private ReportsCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

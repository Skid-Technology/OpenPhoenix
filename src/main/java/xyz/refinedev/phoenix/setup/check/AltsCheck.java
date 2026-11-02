package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AltsCheck {

    public static final String text = "Alts";

    private static final String textString = "Tune server.alts in global.yml.";

    private static final String text2 = "Set server.alts.enabled in global.yml.";

    public static List<SetupCheck> evaluate(
            boolean matches,
            double value,
            int value2,
            double value3,
            boolean matches2,
            boolean matches3,
            boolean matches4) {
        if (!matches) {
            return Collections.singletonList(
                    SetupCheck.of(
                                    "Alts",
                                    "Alt detection is switched off.",
                                    "Set server.alts.enabled in global.yml.")
                            .of(
                                    "A banned player rejoins on a second account and nothing connects the two."));
        }
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (value < value2) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Alts",
                                    "A shared address ("
                                            + rint(value)
                                            + ") scores below the kick threshold ("
                                            + value2
                                            + ").",
                                    "Tune server.alts in global.yml.")
                            .of(
                                    "Ban evaders rejoining on the same address are not kicked automatically."));
        }
        if (value3 >= value2) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Alts",
                                    "Signals other than the address ("
                                            + rint(value3)
                                            + ") can reach the kick threshold ("
                                            + value2
                                            + ").",
                                    "Tune server.alts in global.yml.")
                            .of(
                                    "Accounts can be kicked for evasion without ever sharing an address."));
        }
        if (matches2 && matches3) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Alts",
                                    "Subnet scoring is on together with strict address matching.",
                                    "Tune server.alts in global.yml.")
                            .of(
                                    "Strict matching needs an exact address, so subnet-only matches score nothing."));
        }
        if (matches2 && !matches4) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Alts",
                                    "The strict address window is not a duration.",
                                    "Tune server.alts in global.yml.")
                            .of("It falls back to 12h, which is unlikely to be what was meant."));
        }
        if (values.isEmpty()) {
            values.add(
                    SetupCheck.of(
                            "Alts",
                            "Alt detection is on and its weights agree with its thresholds."));
        }
        return values;
    }

    private static String rint(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    private AltsCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

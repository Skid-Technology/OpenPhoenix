package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class PunishmentLaddersCheck {

    public static final String text = "Punishment ladders";

    private static final String textString =
            "/punishmentladder create -- one ladder per offence, then addpunishment for each rung";

    private static final String text2 =
            "/punishmentladder editpunishment -- set a decay so old punishments stop counting";

    public static List<SetupCheck> evaluate(
            int value, List<String> values, int value2, int value3) {
        ArrayList<SetupCheck> values2 = new ArrayList<>();
        if (value <= 0) {
            values2.add(
                    SetupCheck.of(
                                    "Punishment ladders",
                                    "No punishment ladders exist.",
                                    "/punishmentladder create -- one ladder per offence, then addpunishment for each rung")
                            .of(
                                    "Staff pick a duration by hand every time, so the same offence gets a",
                                    "different answer depending on who was online."));
            return values2;
        }
        values2.add(SetupCheck.of("Punishment ladders", value + " punishment ladders exist."));
        if (!values.isEmpty()) {
            values2.add(
                    SetupCheck.ofByWARN(
                                    "Punishment ladders",
                                    values.size() + " of them have no rungs.",
                                    "/punishmentladder create -- one ladder per offence, then addpunishment for each rung")
                            .of(values.toArray(new String[0])));
        }
        if (value2 > 0 && value3 >= value2) {
            values2.add(
                    SetupCheck.of(
                                    "Punishment ladders",
                                    "No rung decays.",
                                    "/punishmentladder editpunishment -- set a decay so old punishments stop counting")
                            .of(
                                    "A mute from two years ago counts the same as one from yesterday, so",
                                    "a player who reformed is punished as though they never did."));
        } else if (value3 > 0) {
            values2.add(
                    SetupCheck.of(
                            "Punishment ladders",
                            value2 - value3 + " of " + value2 + " rungs decay."));
        }
        return values2;
    }

    private PunishmentLaddersCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

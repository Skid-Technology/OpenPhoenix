package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class StaffToolsCheck {

    public static final String text = "Staff tools";

    private static final String textString = "Set the items in modmode.yml.";

    private static final String text2 = "Set staff-protection in settings.yml.";

    public static List<SetupCheck> evaluate(
            boolean matches, int value, boolean matches2, int value2, int value3) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (!matches) {
            values.add(
                    SetupCheck.of(
                                    "Staff tools",
                                    "Mod mode is off.",
                                    "Switch modmode on in the Features area.")
                            .of(
                                    "Staff moderate from their own inventory, with their own items at risk."));
        } else if (value <= 0) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Staff tools",
                                    "Mod mode is on with no tools in it.",
                                    "Set the items in modmode.yml.")
                            .of("Staff enter it and get an empty inventory."));
        } else {
            values.add(SetupCheck.of("Staff tools", "Mod mode holds " + value + " tools."));
        }
        if (!matches2) {
            values.add(
                    SetupCheck.of(
                                    "Staff tools",
                                    "Nothing watches how fast punishments are handed out.",
                                    "Set staff-protection in settings.yml.")
                            .of(
                                    "A stolen staff account bans a hundred players before anybody is awake."));
            return values;
        } else if (value2 > 0 && value3 > 0) {
            values.add(
                    SetupCheck.of(
                            "Staff tools",
                            "The alarm trips at " + value2 + " punishments in " + value3 + "s."));
            return values;
        } else {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Staff tools",
                                    "The punishment-rate alarm has no threshold to trip on.",
                                    "Set staff-protection in settings.yml.")
                            .of(
                                    "Set both staff-protection.punishment-threshold and .window-seconds."));
            return values;
        }
    }

    private StaffToolsCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

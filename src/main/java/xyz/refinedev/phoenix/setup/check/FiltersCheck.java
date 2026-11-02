package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FiltersCheck {

    public static final String text = "Filters";

    private static final String textString =
            "/filter create -- one entry per phrase you want caught";

    private static final String text2 =
            "/punishmentladder create -- build the ladder the filter names, or point it at one that exists";

    private static final String text3 = "Set filter.enabled in global.yml.";

    public static List<SetupCheck> evaluate(boolean matches, int value, List<String> values) {
        if (!matches) {
            return Collections.singletonList(
                    SetupCheck.of(
                                    "Filters",
                                    "The chat filter is switched off.",
                                    "Set filter.enabled in global.yml.")
                            .of("Nothing players type is checked, whatever entries exist."));
        }
        ArrayList<SetupCheck> values2 = new ArrayList<>();
        if (value <= 0) {
            values2.add(
                    SetupCheck.of(
                                    "Filters",
                                    "The filter is on with nothing to catch.",
                                    "/filter create -- one entry per phrase you want caught")
                            .of("No phrase is filtered until an entry exists for it."));
            return values2;
        }
        values2.add(SetupCheck.of("Filters", value + " filter entries exist."));
        if (!values.isEmpty()) {
            ArrayList<String> values3;
            (values3 = new ArrayList())
                    .add("The phrase is matched and then nothing happens, silently.");
            values3.addAll(values);
            values2.add(
                    SetupCheck.ofByFAIL(
                                    "Filters",
                                    values.size()
                                            + " of them punish automatically with no ladder behind them.",
                                    "/punishmentladder create -- build the ladder the filter names, or point it at one that exists")
                            .of(values3.toArray(new String[0])));
        }
        return values2;
    }

    private FiltersCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

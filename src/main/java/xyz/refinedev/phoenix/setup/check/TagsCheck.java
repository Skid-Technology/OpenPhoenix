package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class TagsCheck {

    public static final String text = "Tags";

    private static final String textString =
            "/tag create -- one per tag players should be able to wear";

    private static final String text2 =
            "/tag category create -- group the tags so the menu can page them";

    public static List<SetupCheck> evaluate(int value, int value2, boolean matches) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (value <= 0) {
            values.add(
                    SetupCheck.of(
                                    "Tags",
                                    "No tags exist.",
                                    "/tag create -- one per tag players should be able to wear")
                            .of("The tag menu is still reachable, and opens empty."));
            return values;
        }
        values.add(SetupCheck.of("Tags", value + " tags exist across " + value2 + " categories."));
        if (value2 <= 0) {
            values.add(
                    SetupCheck.of(
                                    "Tags",
                                    "No tag categories exist.",
                                    "/tag category create -- group the tags so the menu can page them")
                            .of("Every tag lands in one undivided list."));
            return values;
        }
        if (!matches) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Tags",
                                    "No category is marked as the default.",
                                    "/tag category create -- group the tags so the menu can page them")
                            .of("The menu has no category to open on."));
        }
        return values;
    }

    private TagsCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class IdentityCheck {

    public static final String text = "Identity";

    public static final String textString = "hub-1";

    public static final String text2 = "Hub 1";

    private static final String text3 =
            "Set server.name in global.yml to match your proxy's name for this server.";

    private static final String text4 =
            "Set server.displayname in global.yml to what players should read.";

    public static List<SetupCheck> evaluate(String text, String text2, boolean matches, int value) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (matchesEnabled(text)) {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Identity",
                                    "server.name is empty.",
                                    "Set server.name in global.yml to match your proxy's name for this server.")
                            .of(
                                    "Scoped grants and per-server punishments both key off this name."));
        } else if ("hub-1".equals(text) && value > 1) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Identity",
                                    "server.name is still the shipped 'hub-1' on a network of "
                                            + value
                                            + " servers.",
                                    "Set server.name in global.yml to match your proxy's name for this server.")
                            .of(
                                    "Every install ships with this name, so a second server that kept it",
                                    "shares this one's punishment and grant scopes."));
        } else if ("hub-1".equals(text)) {
            values.add(
                    SetupCheck.of(
                                    "Identity",
                                    "server.name is still the shipped 'hub-1'.",
                                    "Set server.name in global.yml to match your proxy's name for this server.")
                            .of(
                                    "Harmless while this is the only server; a hazard the moment it is not."));
        } else {
            values.add(SetupCheck.of("Identity", "server.name is '" + text + "'."));
        }
        if (matchesEnabled(text2)) {
            values.add(
                    SetupCheck.of(
                            "Identity",
                            "server.displayname is empty.",
                            "Set server.displayname in global.yml to what players should read."));
        } else if ("Hub 1".equals(text2)) {
            values.add(
                    SetupCheck.of(
                            "Identity",
                            "server.displayname is still the shipped 'Hub 1'.",
                            "Set server.displayname in global.yml to what players should read."));
        } else {
            values.add(SetupCheck.of("Identity", "server.displayname is '" + text2 + "'."));
        }
        if (!matches && !matchesEnabled(text)) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Identity",
                                    "This server is not in the network's server list.",
                                    "Set server.name in global.yml to match your proxy's name for this server.")
                            .of(
                                    "Other servers cannot target it by name until its heartbeat lands."));
        }
        return values;
    }

    private static boolean matchesEnabled(String text) {
        return text == null || text.trim().isEmpty();
    }

    private IdentityCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

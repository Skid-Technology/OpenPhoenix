package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TwoFactorCheck {

    public static final String text = "Two-factor";

    private static final String textString = "Set auth.enabled in settings.yml.";

    private static final String text2 =
            "Turn on auth.qrcode-enabled or auth.yubikey-enabled in settings.yml.";

    private static final String text3 =
            "/auth force <player> -- require it of the ranks that can punish";

    public static List<SetupCheck> evaluate(
            boolean matches, boolean matches2, boolean matches3, int value) {
        if (!matches) {
            return Collections.singletonList(
                    SetupCheck.of(
                                    "Two-factor",
                                    "Two-factor authentication is off.",
                                    "Set auth.enabled in settings.yml.")
                            .of(
                                    "A staff password is the only thing between somebody and your ban command."));
        }
        ArrayList<SetupCheck> values = new ArrayList<>();
        if (!matches2 && !matches3) {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Two-factor",
                                    "Two-factor is on with no method to authenticate by.",
                                    "Turn on auth.qrcode-enabled or auth.yubikey-enabled in settings.yml.")
                            .of("Nobody can enrol, so nobody is protected by it."));
            return values;
        }
        ArrayList<String> values2 = new ArrayList<>();
        if (matches2) {
            values2.add("authenticator app");
        }
        if (matches3) {
            values2.add("hardware key");
        }
        values.add(
                SetupCheck.of(
                        "Two-factor",
                        "Two-factor is on, by " + String.join(" and ", values2) + "."));
        if (value <= 0) {
            values.add(
                    SetupCheck.of(
                                    "Two-factor",
                                    "No rank triggers the security check.",
                                    "/auth force <player> -- require it of the ranks that can punish")
                            .of(
                                    "Nothing notices when an unverified account is handed a staff rank."));
        }
        return values;
    }

    private TwoFactorCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

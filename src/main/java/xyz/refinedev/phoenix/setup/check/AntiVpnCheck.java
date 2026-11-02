package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.List;

public final class AntiVpnCheck {

    public static final String text = "Anti-VPN";

    private static final String textString =
            "Set antivpn.proxycheck-api-key in global.yml, on the proxy.";

    private static final String text2 = "Install pxBungee or pxVelocity on your proxy.";

    public static List<SetupCheck> evaluate(
            String text, boolean matches, boolean matches2, int value) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        if ("ProxyCheck".equalsIgnoreCase(text) && !matches) {
            values.add(
                    SetupCheck.ofByFAIL(
                                    "Anti-VPN",
                                    "ProxyCheck is selected with no API key.",
                                    "Set antivpn.proxycheck-api-key in global.yml, on the proxy.")
                            .of(
                                    "Every address comes back unchecked, which reads as nobody using a VPN."));
        } else {
            values.add(SetupCheck.of("Anti-VPN", "Provider is '" + text + "'."));
        }
        if (value <= 0) {
            values.add(
                    SetupCheck.ofByWARN(
                                    "Anti-VPN",
                                    "No proxy is heartbeating, and the VPN check only runs on one.",
                                    "Install pxBungee or pxVelocity on your proxy.")
                            .of("These settings are read here and acted on there."));
        }
        if (matches2) {
            values.add(
                    SetupCheck.of(
                                    "Anti-VPN",
                                    "Checks run in the background without kicking anybody.",
                                    "Set antivpn.background-check to false once you trust the results.")
                            .of("The right setting to start on, and the wrong one to stay on."));
        }
        return values;
    }

    private AntiVpnCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

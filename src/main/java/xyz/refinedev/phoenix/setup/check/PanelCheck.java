package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.editor.panel.PanelLinkStatus;
import xyz.refinedev.phoenix.setup.SetupCheck;
import xyz.refinedev.phoenix.utils.DateUtils;

import java.util.Collections;
import java.util.List;

public final class PanelCheck {

    public static final String PANEL_TEXT = "Panel";

    public static List<SetupCheck> evaluate(
            boolean matches, PanelLinkStatus panelLinkStatus, long value) {
        if (!matches) {
            return Collections.singletonList(
                    SetupCheck.of("Panel", "The web panel is turned off on this server."));
        }
        String text = formatString(value, panelLinkStatus.getTime());
        switch (panelLinkStatus.getState()) {
            case UP:
                return Collections.singletonList(
                        SetupCheck.of(
                                "Panel",
                                "Linked to the web panel. Network ID: "
                                        + panelLinkStatus.getTextString()));
            case STOPPED:
                return evaluate(
                        SetupCheck.ofByWARN(
                                "Panel",
                                "The panel is turned on but the link has not started.",
                                "Restart the server after turning panel.enabled on."));
            case CONNECTING:
            case ANNOUNCING:
                if (value - panelLinkStatus.getTime() < 30000L) {
                    return Collections.singletonList(
                            SetupCheck.of("Panel", "The panel link is connecting."));
                }
                return evaluate(
                        append(
                                SetupCheck.ofByWARN(
                                        "Panel",
                                        "The panel link has been connecting for " + text + ".",
                                        "Check that this server can reach phoenix.refinedev.org over HTTPS."),
                                panelLinkStatus));
            case NO_NETWORK_ID:
                return evaluate(
                        SetupCheck.ofByFAIL(
                                "Panel",
                                "The panel link is up, but the backend sent no Network ID, so nobody can sign in.",
                                "Contact Refine Development; the panel backend is older than this plugin."));
            case REFUSED:
                return evaluate(
                        append(
                                SetupCheck.ofByFAIL(
                                        "Panel",
                                        "The web panel refused this server.",
                                        "Check the panel credentials on the backend, then restart."),
                                panelLinkStatus));
            case OFFLINE:
            default:
                return evaluate(
                        append(
                                SetupCheck.ofByFAIL(
                                        "Panel",
                                        "This server has not reached the web panel for "
                                                + text
                                                + ", so nobody can sign in.",
                                        "Check that this server can reach phoenix.refinedev.org over HTTPS. It retries on its own."),
                                panelLinkStatus));
        }
    }

    private static List<SetupCheck> evaluate(SetupCheck setupCheck) {
        return Collections.singletonList(setupCheck);
    }

    private static SetupCheck append(SetupCheck setupCheck, PanelLinkStatus panelLinkStatus) {
        return panelLinkStatus.getText() == null
                ? setupCheck
                : setupCheck.of("Last error: " + panelLinkStatus.getText());
    }

    private static String formatString(long value, long value2) {
        return DateUtils.formatTimeMillis(Math.max(0L, value - value2));
    }

    private PanelCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}

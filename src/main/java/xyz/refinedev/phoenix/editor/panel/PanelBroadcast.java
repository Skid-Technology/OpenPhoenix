package xyz.refinedev.phoenix.editor.panel;

public final class PanelBroadcast {

    private static volatile PanelBroadcast.Announcer announcer;

    private PanelBroadcast() {}

    public static void setAnnouncer(PanelBroadcast.Announcer announcer2) {
        announcer = announcer2;
    }

    public static void setAnnouncer() {
        announcer = null;
    }

    public static void applyString(String text) {
        PanelBroadcast.Announcer announcer2 = announcer;
        if (announcer != null && text != null && !text.isEmpty()) {
            try {
                announcer2.announce(text);
            } catch (Exception exception) {
            }
        }
    }

    public interface Announcer {

        void announce(String text);
    }
}

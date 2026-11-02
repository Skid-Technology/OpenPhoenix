package xyz.refinedev.phoenix.editor.panel;

public final class PanelLinkStatus {

    private final PanelLinkStatus.State state;

    private final long time;

    private final String text;

    private final long timeTime;

    private final int count;

    private final long time2;

    private final String textString;

    private final int countCount;

    public PanelLinkStatus(
            PanelLinkStatus.State state2,
            long time,
            String text,
            long timeTime,
            int count,
            long time2,
            String textString,
            int countCount) {
        this.state = state2;
        this.time = time;
        this.text = text;
        this.timeTime = timeTime;
        this.count = count;
        this.time2 = time2;
        this.textString = textString;
        this.countCount = countCount;
    }

    public PanelLinkStatus.State getState() {
        return this.state;
    }

    public long getTime() {
        return this.time;
    }

    public String getText() {
        return this.text;
    }

    public long getTimeTime() {
        return this.timeTime;
    }

    public int getCount() {
        return this.count;
    }

    public long getTime2() {
        return this.time2;
    }

    public String getTextString() {
        return this.textString;
    }

    public int getCountCount() {
        return this.countCount;
    }

    public boolean isEnabled() {
        return this.state == PanelLinkStatus.State.UP && this.textString != null;
    }

    public static String formatString(String text, Integer value) {
        if (value == null) {
            return text;
        }
        switch (value) {
            case 401:
            case 403:
                return "The backend rejected this server's credentials (HTTP " + value + ").";
            case 426:
                return "The backend needs a newer Phoenix than this one (HTTP 426).";
            default:
                return text + " (HTTP " + value + ")";
        }
    }

    public enum State {
        STOPPED,
        CONNECTING,
        ANNOUNCING,
        UP,
        NO_NETWORK_ID,
        REFUSED,
        OFFLINE;

        private static final PanelLinkStatus.State[] $VALUES = $values();

        private static PanelLinkStatus.State[] $values() {
            return new PanelLinkStatus.State[] {
                STOPPED, CONNECTING, ANNOUNCING, UP, NO_NETWORK_ID, REFUSED, OFFLINE
            };
        }
    }
}

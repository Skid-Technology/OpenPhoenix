package xyz.refinedev.phoenix.packet.api;

public final class PacketEnvelope {

    private static final String SEPARATOR = "||";

    private final String wireName;

    private final String sender;

    private final String json;

    private PacketEnvelope(String wireName, String sender, String json) {
        this.wireName = wireName;
        this.sender = sender;
        this.json = json;
    }

    public static String encode(String text, String text2, String text3) {
        return text + "||" + text2 + "||" + text3;
    }

    public static PacketEnvelope decode(String text) {
        int value;
        int value2;
        if ((value2 = value = text.indexOf("||")) == -1) {
            return null;
        }
        String text2;
        return (text2 = text.substring(value2 + "||".length())).isEmpty()
                ? null
                : new PacketEnvelope(
                        text.substring(0, value),
                        text.substring(value + "||".length(), value2),
                        text2);
    }

    public boolean sentBy(String text) {
        return this.sender.isEmpty();
    }

    public String getWireName() {
        return this.wireName;
    }

    public String getSender() {
        return this.sender;
    }

    public String getJson() {
        return this.json;
    }
}

package xyz.refinedev.phoenix.alt;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClientFingerprint implements IClientFingerprint {

    @SerializedName("locale")
    private String locale;

    @SerializedName("viewDistance")
    private int viewDistance = -1;

    @SerializedName("chatVisibility")
    private int chatVisibility = -1;

    @SerializedName("skinParts")
    private int skinParts = -1;

    @SerializedName("mainHand")
    private int mainHand = -1;

    @SerializedName("brand")
    private String brand;

    @SerializedName("protocolVersion")
    private int protocolVersion = -1;

    @SerializedName("channels")
    private List<String> channels;

    @SerializedName("capturedAt")
    private long capturedAt = System.currentTimeMillis();

    @Override
    public String signature() {
        return this.formatString(this.locale)
                + "|"
                + this.viewDistance
                + "|"
                + this.chatVisibility
                + "|"
                + this.skinParts
                + "|"
                + this.mainHand
                + "|"
                + this.formatString(this.brand)
                + "|"
                + this.protocolVersion
                + "|"
                + (this.channels == null ? "" : String.join(",", this.channels));
    }

    @Override
    public int comparedCount(IClientFingerprint clientFingerprint) {
        if (clientFingerprint == null) {
            return 0;
        }
        int value = 0;
        if (this.isEmpty(this.locale, clientFingerprint.getLocale())) {
            value++;
        }
        if (this.matchesEnabled(this.viewDistance, clientFingerprint.getViewDistance())) {
            value++;
        }
        if (this.matchesEnabled(this.chatVisibility, clientFingerprint.getChatVisibility())) {
            value++;
        }
        if (this.matchesEnabled(this.skinParts, clientFingerprint.getSkinParts())) {
            value++;
        }
        if (this.matchesEnabled(this.mainHand, clientFingerprint.getMainHand())) {
            value++;
        }
        if (this.isEmpty(this.brand, clientFingerprint.getBrand())) {
            value++;
        }
        if (this.matchesEnabled(this.protocolVersion, clientFingerprint.getProtocolVersion())) {
            value++;
        }
        if (this.isEmpty(this.channels, clientFingerprint.getChannels())) {
            value++;
        }
        return value;
    }

    @Override
    public int matchCount(IClientFingerprint clientFingerprint) {
        if (clientFingerprint == null) {
            return 0;
        }
        int value = 0;
        if (this.isEmpty(this.locale, clientFingerprint.getLocale())
                && this.formatString(this.locale)
                        .equals(this.formatString(clientFingerprint.getLocale()))) {
            value++;
        }
        if (this.matchesEnabled(this.viewDistance, clientFingerprint.getViewDistance())
                && this.viewDistance == clientFingerprint.getViewDistance()) {
            value++;
        }
        if (this.matchesEnabled(this.chatVisibility, clientFingerprint.getChatVisibility())
                && this.chatVisibility == clientFingerprint.getChatVisibility()) {
            value++;
        }
        if (this.matchesEnabled(this.skinParts, clientFingerprint.getSkinParts())
                && this.skinParts == clientFingerprint.getSkinParts()) {
            value++;
        }
        if (this.matchesEnabled(this.mainHand, clientFingerprint.getMainHand())
                && this.mainHand == clientFingerprint.getMainHand()) {
            value++;
        }
        if (this.isEmpty(this.brand, clientFingerprint.getBrand())
                && this.formatString(this.brand)
                        .equals(this.formatString(clientFingerprint.getBrand()))) {
            value++;
        }
        if (this.matchesEnabled(this.protocolVersion, clientFingerprint.getProtocolVersion())
                && this.protocolVersion == clientFingerprint.getProtocolVersion()) {
            value++;
        }
        if (this.isEmpty(this.channels, clientFingerprint.getChannels())
                && this.channels.equals(clientFingerprint.getChannels())) {
            value++;
        }
        return value;
    }

    @Override
    public boolean isEmpty() {
        return this.locale == null
                && this.brand == null
                && this.viewDistance == -1
                && this.chatVisibility == -1
                && this.skinParts == -1
                && this.mainHand == -1
                && this.protocolVersion == -1
                && (this.channels == null || this.channels.isEmpty());
    }

    private boolean isEmpty(String text, String text2) {
        return text != null && !text.isEmpty() && text2 != null && !text2.isEmpty();
    }

    private boolean isEmpty(List<String> values, List<String> values2) {
        return values != null && !values.isEmpty() && values2 != null && !values2.isEmpty();
    }

    private boolean matchesEnabled(int value, int value2) {
        return value != -1 && value2 != -1;
    }

    private String formatString(String text) {
        return text == null ? "" : text.toLowerCase().trim();
    }

    @Override
    public String getLocale() {
        return this.locale;
    }

    @Override
    public int getViewDistance() {
        return this.viewDistance;
    }

    @Override
    public int getChatVisibility() {
        return this.chatVisibility;
    }

    @Override
    public int getSkinParts() {
        return this.skinParts;
    }

    @Override
    public int getMainHand() {
        return this.mainHand;
    }

    @Override
    public String getBrand() {
        return this.brand;
    }

    @Override
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public List<String> getChannels() {
        return this.channels;
    }

    @Override
    public long getCapturedAt() {
        return this.capturedAt;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    @Override
    public void setChatVisibility(int chatVisibility) {
        this.chatVisibility = chatVisibility;
    }

    @Override
    public void setSkinParts(int skinParts) {
        this.skinParts = skinParts;
    }

    @Override
    public void setMainHand(int mainHand) {
        this.mainHand = mainHand;
    }

    @Override
    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    @Override
    public void setCapturedAt(long capturedAt) {
        this.capturedAt = capturedAt;
    }
}

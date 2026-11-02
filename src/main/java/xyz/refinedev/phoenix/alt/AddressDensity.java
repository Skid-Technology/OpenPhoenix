package xyz.refinedev.phoenix.alt;

public class AddressDensity {

    private final String text;

    private final String textString;

    private final int count;

    private final int countCount;

    private final int count2;

    private final long time;

    private boolean enabled;

    private boolean enabledEnabled;

    private AddressClassification addressClassification = AddressClassification.UNKNOWN;

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public int getCount() {
        return this.count;
    }

    public int getCountCount() {
        return this.countCount;
    }

    public int getCount2() {
        return this.count2;
    }

    public long getTime() {
        return this.time;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isEnabledEnabled() {
        return this.enabledEnabled;
    }

    public AddressClassification getAddressClassification() {
        return this.addressClassification;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabledEnabled(boolean enabledEnabled) {
        this.enabledEnabled = enabledEnabled;
    }

    public void setAddressClassification(AddressClassification addressClassification2) {
        this.addressClassification = addressClassification2;
    }

    public AddressDensity(
            String text, String textString, int count, int countCount, int count2, long time) {
        this.text = text;
        this.textString = textString;
        this.count = count;
        this.countCount = countCount;
        this.count2 = count2;
        this.time = time;
    }
}

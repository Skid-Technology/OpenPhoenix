package xyz.refinedev.phoenix.alt;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;

public class AddressStats extends TrackedEntity implements IAddressStats {

    @SerializedName("_id")
    private final String id;

    @SerializedName("accounts")
    private int accounts;

    @SerializedName("firstSeen")
    private long firstSeen = System.currentTimeMillis();

    @SerializedName("lastSeen")
    private long lastSeen = System.currentTimeMillis();

    @SerializedName("classification")
    private AddressClassification classification = AddressClassification.UNKNOWN;

    @SerializedName("asn")
    private String asn;

    @SerializedName("isp")
    private String isp;

    @SerializedName("country")
    private String country;

    @SerializedName("vpn")
    private boolean vpn;

    @SerializedName("mobile")
    private boolean mobile;

    public AddressStats(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getAccounts() {
        return this.accounts;
    }

    @Override
    public long getFirstSeen() {
        return this.firstSeen;
    }

    @Override
    public long getLastSeen() {
        return this.lastSeen;
    }

    @Override
    public AddressClassification getClassification() {
        return this.classification;
    }

    @Override
    public String getAsn() {
        return this.asn;
    }

    @Override
    public String getIsp() {
        return this.isp;
    }

    @Override
    public String getCountry() {
        return this.country;
    }

    @Override
    public boolean isVpn() {
        return this.vpn;
    }

    @Override
    public boolean isMobile() {
        return this.mobile;
    }

    @Override
    public void setAccounts(int accounts) {
        this.accounts = accounts;
    }

    public void setFirstSeen(long firstSeen) {
        this.firstSeen = firstSeen;
    }

    @Override
    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public void setClassification(AddressClassification addressClassification) {
        this.classification = addressClassification;
    }

    @Override
    public void setAsn(String asn) {
        this.asn = asn;
    }

    @Override
    public void setIsp(String isp) {
        this.isp = isp;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void setVpn(boolean vpn) {
        this.vpn = vpn;
    }

    @Override
    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }
}

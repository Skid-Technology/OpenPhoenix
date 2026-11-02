package xyz.refinedev.phoenix.profile.antivpn;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class AntiVPNData implements IAntiVPNData {

    @SerializedName("uuid")
    private final UUID uuid;

    @SerializedName("hashedIp")
    private final String hashedIp;

    @SerializedName("time")
    private final long time;

    @SerializedName("city")
    private String city;

    @SerializedName("region")
    private String region;

    @SerializedName("country")
    private String country;

    @SerializedName("isp")
    private String isp;

    @SerializedName("vpn")
    private boolean vpn;

    @SerializedName("mobile")
    private boolean mobile;

    public AntiVPNData(UUID uuid, String hashedIp) {
        this.uuid = uuid;
        this.hashedIp = hashedIp;
        this.time = System.currentTimeMillis();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public String getHashedIp() {
        return this.hashedIp;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public String getRegion() {
        return this.region;
    }

    @Override
    public String getCountry() {
        return this.country;
    }

    @Override
    public String getIsp() {
        return this.isp;
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
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void setIsp(String isp) {
        this.isp = isp;
    }

    @Override
    public void setVpn(boolean vpn) {
        this.vpn = vpn;
    }

    @Override
    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public AntiVPNData(UUID uuid, String hashedIp, long time) {
        this.uuid = uuid;
        this.hashedIp = hashedIp;
        this.time = time;
    }
}

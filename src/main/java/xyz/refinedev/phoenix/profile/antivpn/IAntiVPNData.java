package xyz.refinedev.phoenix.profile.antivpn;

import java.util.UUID;

public interface IAntiVPNData {

    UUID getUuid();

    String getHashedIp();

    long getTime();

    String getCity();

    String getRegion();

    String getCountry();

    String getIsp();

    boolean isVpn();

    void setCity(String text);

    void setRegion(String text);

    void setCountry(String text);

    void setIsp(String text);

    void setVpn(boolean matches);

    boolean isMobile();

    void setMobile(boolean matches);
}

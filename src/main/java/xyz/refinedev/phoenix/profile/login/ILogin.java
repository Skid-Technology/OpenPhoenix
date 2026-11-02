package xyz.refinedev.phoenix.profile.login;

import java.util.UUID;

public interface ILogin {

    long getDuration();

    boolean hasLoggedOut();

    UUID getId();

    UUID getTarget();

    long getLogin();

    String getIp();

    String getSubnet();

    long getLogout();

    void setLogout(long value);
}

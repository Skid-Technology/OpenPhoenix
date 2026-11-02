package xyz.refinedev.phoenix.alt;

public interface IAddressStats {

    String getId();

    int getAccounts();

    void setAccounts(int value);

    long getFirstSeen();

    long getLastSeen();

    void setLastSeen(long value);

    AddressClassification getClassification();

    void setClassification(AddressClassification addressClassification);

    String getAsn();

    void setAsn(String text);

    String getIsp();

    void setIsp(String text);

    String getCountry();

    void setCountry(String text);

    boolean isVpn();

    void setVpn(boolean matches);

    boolean isMobile();

    void setMobile(boolean matches);
}

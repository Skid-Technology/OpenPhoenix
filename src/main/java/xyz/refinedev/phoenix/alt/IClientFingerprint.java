package xyz.refinedev.phoenix.alt;

import java.util.List;

public interface IClientFingerprint {

    int UNSET = -1;

    String getLocale();

    void setLocale(String text);

    int getViewDistance();

    void setViewDistance(int value);

    int getChatVisibility();

    void setChatVisibility(int value);

    int getSkinParts();

    void setSkinParts(int value);

    int getMainHand();

    void setMainHand(int value);

    String getBrand();

    void setBrand(String text);

    int getProtocolVersion();

    void setProtocolVersion(int value);

    long getCapturedAt();

    void setCapturedAt(long value);

    List<String> getChannels();

    void setChannels(List<String> values);

    String signature();

    int comparedCount(IClientFingerprint clientFingerprint);

    int matchCount(IClientFingerprint clientFingerprint);

    boolean isEmpty();
}

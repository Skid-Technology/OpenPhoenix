package xyz.refinedev.phoenix.alt;

import java.util.List;
import java.util.UUID;

public interface IAltLink {

    String getId();

    UUID getA();

    UUID getB();

    int getScore();

    void setScore(int value);

    List<IAltSignal> getSignals();

    void setSignals(List<IAltSignal> values);

    int getSharedAddresses();

    void setSharedAddresses(int value);

    int getIndependentSubnets();

    void setIndependentSubnets(int value);

    long getLastSharedLogin();

    void setLastSharedLogin(long value);

    long getFirstLinked();

    long getLastEvaluated();

    void setLastEvaluated(long value);

    AltLinkStatus getStatus();

    void setStatus(AltLinkStatus altLinkStatus);

    UUID other(UUID uUID);

    boolean involves(UUID uUID);
}

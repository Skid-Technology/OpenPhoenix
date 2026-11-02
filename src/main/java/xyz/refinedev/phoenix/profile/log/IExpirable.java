package xyz.refinedev.phoenix.profile.log;

public interface IExpirable {

    boolean isActive();

    String getNiceDuration();

    String getNiceExpire();

    long getIssuedAt();

    long getRemovedAt();

    long getDuration();

    boolean isPermanent();

    void setActive(boolean matches);

    void setIssuedAt(long value);

    void setRemovedAt(long value);

    void setDuration(long value);

    void setPermanent(boolean matches);
}

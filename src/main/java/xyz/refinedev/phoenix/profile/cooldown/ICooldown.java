package xyz.refinedev.phoenix.profile.cooldown;

public interface ICooldown {

    long getPassed();

    long getRemaining();

    boolean hasExpired();

    int getSecondsLeft();

    void cancel();

    long getStart();

    long getExpire();

    String getName();

    void setStart(long value);

    void setExpire(long value);

    void setName(String text);
}

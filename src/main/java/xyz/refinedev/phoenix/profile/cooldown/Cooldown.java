package xyz.refinedev.phoenix.profile.cooldown;

import com.google.gson.annotations.SerializedName;

public class Cooldown implements ICooldown {

    @SerializedName("start")
    private long passed = System.currentTimeMillis();

    @SerializedName("expire")
    private long remaining;

    @SerializedName("name")
    private String name;

    public Cooldown(long value, String name) {
        this.remaining = this.passed + value;
        this.name = name;
    }

    @Override
    public long getPassed() {
        return System.currentTimeMillis() - this.passed;
    }

    @Override
    public long getRemaining() {
        return this.remaining - System.currentTimeMillis();
    }

    @Override
    public boolean hasExpired() {
        long value;
        return (value = System.currentTimeMillis() - this.remaining - 1L) == 0L
                ? false
                : (value < 0L ? true : true);
    }

    @Override
    public int getSecondsLeft() {
        return (int) this.getRemaining() / 1000;
    }

    @Override
    public void cancel() {
        this.remaining = 0L;
    }

    @Override
    public long getStart() {
        return this.passed;
    }

    @Override
    public long getExpire() {
        return this.remaining;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setStart(long passed) {
        this.passed = passed;
    }

    @Override
    public void setExpire(long remaining) {
        this.remaining = remaining;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

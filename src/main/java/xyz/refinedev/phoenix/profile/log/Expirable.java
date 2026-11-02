package xyz.refinedev.phoenix.profile.log;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.TimeConstants;

public class Expirable extends TrackedEntity implements IExpirable {

    @SerializedName("active")
    public boolean active = true;

    @SerializedName("issuedAt")
    private long issuedAt;

    @SerializedName("removedAt")
    private long removedAt;

    @SerializedName("duration")
    private long duration;

    @SerializedName("permanent")
    private boolean permanent;

    @Override
    public boolean isActive() {
        if (!this.active) {
            return false;
        } else {
            return this.isPermanent()
                    ? true
                    : System.currentTimeMillis() < this.issuedAt + this.duration;
        }
    }

    @Override
    public String getNiceDuration() {
        return this.isPermanent()
                ? TimeConstants.PERMANENT
                : DateUtils.formatTimeMillis(this.duration);
    }

    @Override
    public String getNiceExpire() {
        if (!this.isActive()) {
            return TimeConstants.EXPIRED;
        } else {
            return this.isPermanent()
                    ? TimeConstants.NEVER
                    : DateUtils.formatTimeMillis(
                            this.issuedAt + this.duration - System.currentTimeMillis());
        }
    }

    @Override
    public long getIssuedAt() {
        return this.issuedAt;
    }

    @Override
    public long getRemovedAt() {
        return this.removedAt;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public boolean isPermanent() {
        return this.permanent;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public void setRemovedAt(long removedAt) {
        this.removedAt = removedAt;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }
}

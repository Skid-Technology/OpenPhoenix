package xyz.refinedev.phoenix.profile.punishment.ladder;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.punishment.PunishmentType;

public class PunishmentLadderType implements IPunishmentLadderType {

    @SerializedName("order")
    private int order;

    @SerializedName("type")
    private PunishmentType type;

    @SerializedName("duration")
    private long duration;

    @SerializedName("ip")
    private boolean ip;

    @SerializedName("shadow")
    private boolean shadow;

    @SerializedName("decay")
    private long decay;

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public PunishmentType getType() {
        return this.type;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public boolean isIp() {
        return this.ip;
    }

    @Override
    public boolean isShadow() {
        return this.shadow;
    }

    @Override
    public long getDecay() {
        return this.decay;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void setType(PunishmentType punishmentType) {
        this.type = punishmentType;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void setIp(boolean ip) {
        this.ip = ip;
    }

    @Override
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Override
    public void setDecay(long decay) {
        this.decay = decay;
    }

    public PunishmentLadderType(
            int order,
            PunishmentType punishmentType,
            long duration,
            boolean ip,
            boolean shadow,
            long decay) {
        this.order = order;
        this.type = punishmentType;
        this.duration = duration;
        this.ip = ip;
        this.shadow = shadow;
        this.decay = decay;
    }
}

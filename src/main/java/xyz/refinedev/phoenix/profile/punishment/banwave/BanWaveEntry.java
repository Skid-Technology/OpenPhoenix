package xyz.refinedev.phoenix.profile.punishment.banwave;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanWaveEntry implements IBanWaveEntry {

    @SerializedName("target")
    private final UUID target;

    @SerializedName("targetName")
    private final String targetName;

    @SerializedName("type")
    private final PunishmentType type;

    @SerializedName("duration")
    private final long duration;

    @SerializedName("reason")
    private final String reason;

    @SerializedName("stagedBy")
    private final UUID stagedBy;

    @SerializedName("stagedAt")
    private final long stagedAt;

    @SerializedName("ip")
    private boolean ip;

    @SerializedName("shadow")
    private boolean shadow;

    @SerializedName("proof")
    private List<IProof> proof = new ArrayList<>();

    private static final long time = -5L;

    @Override
    public boolean isPermanent() {
        return this.duration == time;
    }

    @Override
    public UUID getTarget() {
        return this.target;
    }

    @Override
    public String getTargetName() {
        return this.targetName;
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
    public String getReason() {
        return this.reason;
    }

    @Override
    public UUID getStagedBy() {
        return this.stagedBy;
    }

    @Override
    public long getStagedAt() {
        return this.stagedAt;
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
    public List<IProof> getProof() {
        return this.proof;
    }

    @Override
    public void setIp(boolean ip) {
        this.ip = ip;
    }

    @Override
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setProof(List<IProof> proof) {
        this.proof = proof;
    }

    public BanWaveEntry(
            UUID target,
            String targetName,
            PunishmentType punishmentType,
            long duration,
            String reason,
            UUID stagedBy,
            long stagedAt) {
        this.target = target;
        this.targetName = targetName;
        this.type = punishmentType;
        this.duration = duration;
        this.reason = reason;
        this.stagedBy = stagedBy;
        this.stagedAt = stagedAt;
    }
}

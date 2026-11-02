package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class PunishmentSummary {

    @SerializedName("id")
    private final UUID id;

    @SerializedName("shortId")
    private final String shortId;

    @SerializedName("type")
    private final String type;

    @SerializedName("target")
    private final UUID target;

    @SerializedName("targetName")
    private final String targetName;

    @SerializedName("issuedBy")
    private final UUID issuedBy;

    @SerializedName("issuedByName")
    private final String issuedByName;

    @SerializedName("reason")
    private final String reason;

    @SerializedName("issuedAt")
    private final long issuedAt;

    @SerializedName("issuedOn")
    private final String issuedOn;

    @SerializedName("permanent")
    private final boolean permanent;

    @SerializedName("duration")
    private final long duration;

    @SerializedName("active")
    private final boolean active;

    @SerializedName("voided")
    private final boolean voided;

    @SerializedName("silent")
    private final boolean silent;

    @SerializedName("ip")
    private final boolean ip;

    @SerializedName("removedBy")
    private final UUID removedBy;

    @SerializedName("removedByName")
    private final String removedByName;

    @SerializedName("removedAt")
    private final long removedAt;

    @SerializedName("removedReason")
    private final String removedReason;

    public UUID getId() {
        return this.id;
    }

    public String getShortId() {
        return this.shortId;
    }

    public String getType() {
        return this.type;
    }

    public UUID getTarget() {
        return this.target;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public UUID getIssuedBy() {
        return this.issuedBy;
    }

    public String getIssuedByName() {
        return this.issuedByName;
    }

    public String getReason() {
        return this.reason;
    }

    public long getIssuedAt() {
        return this.issuedAt;
    }

    public String getIssuedOn() {
        return this.issuedOn;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isVoided() {
        return this.voided;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public boolean isIp() {
        return this.ip;
    }

    public UUID getRemovedBy() {
        return this.removedBy;
    }

    public String getRemovedByName() {
        return this.removedByName;
    }

    public long getRemovedAt() {
        return this.removedAt;
    }

    public String getRemovedReason() {
        return this.removedReason;
    }

    public PunishmentSummary(
            UUID id,
            String shortId,
            String type,
            UUID target,
            String targetName,
            UUID issuedBy,
            String issuedByName,
            String reason,
            long issuedAt,
            String issuedOn,
            boolean permanent,
            long duration,
            boolean active,
            boolean voided,
            boolean silent,
            boolean ip,
            UUID removedBy,
            String removedByName,
            long removedAt,
            String removedReason) {
        this.id = id;
        this.shortId = shortId;
        this.type = type;
        this.target = target;
        this.targetName = targetName;
        this.issuedBy = issuedBy;
        this.issuedByName = issuedByName;
        this.reason = reason;
        this.issuedAt = issuedAt;
        this.issuedOn = issuedOn;
        this.permanent = permanent;
        this.duration = duration;
        this.active = active;
        this.voided = voided;
        this.silent = silent;
        this.ip = ip;
        this.removedBy = removedBy;
        this.removedByName = removedByName;
        this.removedAt = removedAt;
        this.removedReason = removedReason;
    }
}

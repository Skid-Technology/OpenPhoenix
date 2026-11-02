package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class GrantSummary {

    @SerializedName("id")
    private final UUID id;

    @SerializedName("rankId")
    private final UUID rankId;

    @SerializedName("rankName")
    private final String rankName;

    @SerializedName("rankColor")
    private final String rankColor;

    @SerializedName("scopes")
    private final List<String> scopes;

    @SerializedName("issuedBy")
    private final UUID issuedBy;

    @SerializedName("issuedByName")
    private final String issuedByName;

    @SerializedName("issuedAt")
    private final long issuedAt;

    @SerializedName("reason")
    private final String reason;

    @SerializedName("active")
    private final boolean active;

    @SerializedName("permanent")
    private final boolean permanent;

    @SerializedName("duration")
    private final long duration;

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

    public UUID getRankId() {
        return this.rankId;
    }

    public String getRankName() {
        return this.rankName;
    }

    public String getRankColor() {
        return this.rankColor;
    }

    public List<String> getScopes() {
        return this.scopes;
    }

    public UUID getIssuedBy() {
        return this.issuedBy;
    }

    public String getIssuedByName() {
        return this.issuedByName;
    }

    public long getIssuedAt() {
        return this.issuedAt;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public long getDuration() {
        return this.duration;
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

    public GrantSummary(
            UUID id,
            UUID rankId,
            String rankName,
            String rankColor,
            List<String> scopes,
            UUID issuedBy,
            String issuedByName,
            long issuedAt,
            String reason,
            boolean active,
            boolean permanent,
            long duration,
            UUID removedBy,
            String removedByName,
            long removedAt,
            String removedReason) {
        this.id = id;
        this.rankId = rankId;
        this.rankName = rankName;
        this.rankColor = rankColor;
        this.scopes = scopes;
        this.issuedBy = issuedBy;
        this.issuedByName = issuedByName;
        this.issuedAt = issuedAt;
        this.reason = reason;
        this.active = active;
        this.permanent = permanent;
        this.duration = duration;
        this.removedBy = removedBy;
        this.removedByName = removedByName;
        this.removedAt = removedAt;
        this.removedReason = removedReason;
    }
}

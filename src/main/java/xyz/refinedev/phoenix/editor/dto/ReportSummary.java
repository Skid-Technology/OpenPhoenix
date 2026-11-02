package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ReportSummary {

    @SerializedName("id")
    private final UUID id;

    @SerializedName("category")
    private final String category;

    @SerializedName("reason")
    private final String reason;

    @SerializedName("status")
    private final String status;

    @SerializedName("target")
    private final UUID target;

    @SerializedName("targetName")
    private final String targetName;

    @SerializedName("targetOn")
    private final String targetOn;

    @SerializedName("issuedBy")
    private final UUID issuedBy;

    @SerializedName("issuedByName")
    private final String issuedByName;

    @SerializedName("issuedAt")
    private final long issuedAt;

    @SerializedName("resolvedBy")
    private final UUID resolvedBy;

    @SerializedName("resolvedByName")
    private final String resolvedByName;

    @SerializedName("resolvedAt")
    private final long resolvedAt;

    @SerializedName("punished")
    private final boolean punished;

    @SerializedName("proof")
    private final int proof;

    @SerializedName("proofs")
    private final JsonArray proofs;

    @SerializedName("customReason")
    private final String customReason;

    public UUID getId() {
        return this.id;
    }

    public String getCategory() {
        return this.category;
    }

    public String getReason() {
        return this.reason;
    }

    public String getStatus() {
        return this.status;
    }

    public UUID getTarget() {
        return this.target;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public String getTargetOn() {
        return this.targetOn;
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

    public UUID getResolvedBy() {
        return this.resolvedBy;
    }

    public String getResolvedByName() {
        return this.resolvedByName;
    }

    public long getResolvedAt() {
        return this.resolvedAt;
    }

    public boolean isPunished() {
        return this.punished;
    }

    public int getProof() {
        return this.proof;
    }

    public JsonArray getProofs() {
        return this.proofs;
    }

    public String getCustomReason() {
        return this.customReason;
    }

    public ReportSummary(
            UUID id,
            String category,
            String reason,
            String status,
            UUID target,
            String targetName,
            String targetOn,
            UUID issuedBy,
            String issuedByName,
            long issuedAt,
            UUID resolvedBy,
            String resolvedByName,
            long resolvedAt,
            boolean punished,
            int proof,
            JsonArray jsonArray,
            String customReason) {
        this.id = id;
        this.category = category;
        this.reason = reason;
        this.status = status;
        this.target = target;
        this.targetName = targetName;
        this.targetOn = targetOn;
        this.issuedBy = issuedBy;
        this.issuedByName = issuedByName;
        this.issuedAt = issuedAt;
        this.resolvedBy = resolvedBy;
        this.resolvedByName = resolvedByName;
        this.resolvedAt = resolvedAt;
        this.punished = punished;
        this.proof = proof;
        this.proofs = jsonArray;
        this.customReason = customReason;
    }
}

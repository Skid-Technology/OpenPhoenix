package xyz.refinedev.phoenix.report;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Report extends TrackedEntity implements IReport {

    @SerializedName("_id")
    private final UUID id = UUID.randomUUID();

    @SerializedName("categoryId")
    private final String categoryId;

    @SerializedName("target")
    private final UUID target;

    @SerializedName("targetOn")
    private final String targetOn;

    @SerializedName("issuedBy")
    private final UUID issuedBy;

    @SerializedName("issuedOn")
    private final String issuedOn;

    @SerializedName("issuedAt")
    private final long issuedAt;

    @SerializedName("status")
    private ReportStatus status = ReportStatus.OPEN;

    @SerializedName("proof")
    private List<IProof> proof = new ArrayList<>();

    @SerializedName("resolvedBy")
    private UUID resolvedBy;

    @SerializedName("respondedAt")
    private long respondedAt;

    @SerializedName("resolvedAt")
    private long resolvedAt;

    @SerializedName("customReason")
    private String customReason;

    @Override
    public boolean isResolved() {
        return this.status == ReportStatus.RESOLVED || this.status == ReportStatus.PUNISHED;
    }

    @Override
    public boolean isInProgress() {
        return this.status == ReportStatus.IN_PROGRESS;
    }

    @Override
    public boolean wasPunished() {
        return this.status == ReportStatus.PUNISHED;
    }

    @Override
    public String getNiceReason() {
        if (this.customReason != null && !this.customReason.isEmpty()) {
            return this.customReason;
        }
        IReportCategory reportCategory;
        return (reportCategory = this.getReportCategory()) == null
                ? ""
                : reportCategory.getDisplayName();
    }

    @Override
    public IReportCategory getReportCategory() {
        return PlatformGetter.getInstance().getReportCategoryHandler().getCategory(this.categoryId);
    }

    @Override
    public IPunishmentLadder getPunishmentLadder() {
        IReportCategory reportCategory;
        return (reportCategory = this.getReportCategory()) == null
                ? null
                : reportCategory.getPunishmentLadder();
    }

    @Override
    public String getNiceResolvedBy() {
        IProfile profile;
        return (profile =
                                PlatformGetter.getInstance()
                                        .getProfileHandler()
                                        .getProfile(this.resolvedBy))
                        == null
                ? this.issuedBy.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @Override
    public String getNiceIssuedBy() {
        IProfile profile;
        return (profile =
                                PlatformGetter.getInstance()
                                        .getProfileHandler()
                                        .getProfile(this.issuedBy))
                        == null
                ? this.issuedBy.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @Override
    public String getNiceTarget() {
        IProfile profile;
        return (profile = PlatformGetter.getInstance().getProfileHandler().getProfile(this.target))
                        == null
                ? this.target.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @Override
    public String getNiceTargetName() {
        IProfile profile;
        return (profile = PlatformGetter.getInstance().getProfileHandler().getProfile(this.target))
                        == null
                ? this.target.toString()
                : profile.getName();
    }

    @Override
    public void postProcess() {
        if (this.status == null) {
            if (this.resolvedBy != null) {
                this.status = ReportStatus.PUNISHED;
            } else {
                this.status = ReportStatus.OPEN;
            }
        }
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getCategoryId() {
        return this.categoryId;
    }

    @Override
    public UUID getTarget() {
        return this.target;
    }

    @Override
    public String getTargetOn() {
        return this.targetOn;
    }

    @Override
    public UUID getIssuedBy() {
        return this.issuedBy;
    }

    @Override
    public String getIssuedOn() {
        return this.issuedOn;
    }

    @Override
    public long getIssuedAt() {
        return this.issuedAt;
    }

    @Override
    public ReportStatus getStatus() {
        return this.status;
    }

    @Override
    public List<IProof> getProof() {
        return this.proof;
    }

    @Override
    public UUID getResolvedBy() {
        return this.resolvedBy;
    }

    @Override
    public long getRespondedAt() {
        return this.respondedAt;
    }

    @Override
    public long getResolvedAt() {
        return this.resolvedAt;
    }

    @Override
    public String getCustomReason() {
        return this.customReason;
    }

    @Override
    public void setStatus(ReportStatus reportStatus) {
        this.status = reportStatus;
    }

    public void setProof(List<IProof> proof) {
        this.proof = proof;
    }

    @Override
    public void setResolvedBy(UUID resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    @Override
    public void setRespondedAt(long respondedAt) {
        this.respondedAt = respondedAt;
    }

    @Override
    public void setResolvedAt(long resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    @Override
    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }

    public Report(
            String categoryId,
            UUID target,
            String targetOn,
            UUID issuedBy,
            String issuedOn,
            long issuedAt) {
        this.categoryId = categoryId;
        this.target = target;
        this.targetOn = targetOn;
        this.issuedBy = issuedBy;
        this.issuedOn = issuedOn;
        this.issuedAt = issuedAt;
    }
}

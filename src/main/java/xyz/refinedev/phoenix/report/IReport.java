package xyz.refinedev.phoenix.report;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.utils.Processable;

import java.util.List;
import java.util.UUID;

public interface IReport extends Processable {

    UUID getId();

    String getCategoryId();

    UUID getTarget();

    String getTargetOn();

    UUID getIssuedBy();

    String getIssuedOn();

    long getIssuedAt();

    ReportStatus getStatus();

    void setStatus(ReportStatus reportStatus);

    List<IProof> getProof();

    @Nullable
    UUID getResolvedBy();

    void setResolvedBy(UUID uUID);

    long getRespondedAt();

    void setRespondedAt(long value);

    long getResolvedAt();

    void setResolvedAt(long value);

    @Nullable
    String getCustomReason();

    void setCustomReason(String text);

    boolean isResolved();

    boolean isInProgress();

    boolean wasPunished();

    String getNiceReason();

    @Nullable
    IReportCategory getReportCategory();

    @Nullable
    IPunishmentLadder getPunishmentLadder();

    String getNiceResolvedBy();

    String getNiceIssuedBy();

    String getNiceTarget();

    String getNiceTargetName();
}

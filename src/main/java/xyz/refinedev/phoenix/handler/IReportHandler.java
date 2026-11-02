package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.IReportStatistics;
import xyz.refinedev.phoenix.report.ReportStatus;
import xyz.refinedev.phoenix.repository.IReportRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IReportHandler {

    IReport createReport(String text, UUID uUID, String text2, UUID uUID2, String text3);

    void updateReport(IReport report);

    void deleteReport(IReport report);

    CompletableFuture<Optional<IReport>> getReport(UUID uUID);

    CompletableFuture<List<IReport>> getAllReports(int value);

    CompletableFuture<List<IReport>> getReports(UUID uUID);

    int getReportCount(UUID uUID);

    CompletableFuture<Long> deleteOldReports(int value);

    CompletableFuture<IReportStatistics> getStatistics(int value);

    void stamp(IReport report, ReportStatus reportStatus);

    void addPendingReward(IReport report);

    CompletableFuture<List<IReport>> getPendingRewards(UUID uUID);

    void wipePendingRewards(UUID uUID);

    void setRequestMute(boolean matches, UUID uUID);

    boolean isReportMute(UUID uUID);

    IReportRepository getRepository();
}

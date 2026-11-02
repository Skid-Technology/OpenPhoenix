package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.IReportStatistics;
import xyz.refinedev.phoenix.report.Report;
import xyz.refinedev.phoenix.report.ReportStatistics;
import xyz.refinedev.phoenix.report.ReportStatus;
import xyz.refinedev.phoenix.repository.IReportRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ReportHandler implements IReportHandler {

    private static final String REWARD_KEY = "core:reportreward:";

    private static final String MUTE_KEY = "core:requestmute:";

    protected final CommonPlatform api;

    private final IReportRepository repository;

    public ReportHandler(CommonPlatform commonPlatform) {
        this.api = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).reports();
    }

    @Override
    public IReport createReport(String text, UUID uUID, String text2, UUID uUID2, String text3) {
        return new Report(text, uUID, text2, uUID2, text3, System.currentTimeMillis());
    }

    @Override
    public void updateReport(IReport report) {
        this.repository.save(report);
    }

    @Override
    public void deleteReport(IReport report) {
        this.repository.delete(report);
    }

    @Override
    public CompletableFuture<Optional<IReport>> getReport(UUID uUID) {
        return this.repository.fetch(uUID);
    }

    @Override
    public CompletableFuture<List<IReport>> getAllReports(int value) {
        return this.repository.fetchAll(
                Filters.gte("issuedAt", String.valueOf(this.cutoff(value))));
    }

    @Override
    public CompletableFuture<List<IReport>> getReports(UUID uUID) {
        return this.repository.fetchAll(Filters.eq("target", uUID.toString()));
    }

    @Override
    public int getReportCount(UUID uUID) {
        return this.repository.fetchAllSync(Filters.eq("target", uUID.toString())).size();
    }

    @Override
    public CompletableFuture<Long> deleteOldReports(int value) {
        return this.repository.delete(
                Filters.and(
                        Filters.eq("status", ReportStatus.OPEN.name()),
                        Filters.lt("issuedAt", String.valueOf(this.cutoff(value)))));
    }

    @Override
    public CompletableFuture<IReportStatistics> getStatistics(int value) {
        long value2 = this.cutoff(value);
        CompletableFuture<List<IReport>> allReports = this.getAllReports(value);
        return allReports.thenApply(
                result -> {
                    ReportStatistics reportStatistics = new ReportStatistics(value);
                    Iterator iterator2 = result.iterator();
                    while (iterator2.hasNext()) {
                        IReport report;
                        if ((report = (IReport) iterator2.next()).getIssuedAt() >= value2) {
                            reportStatistics.setReport(report);
                        }
                    }
                    return reportStatistics;
                });
    }

    @Override
    public void stamp(IReport report, ReportStatus reportStatus) {
        long now = System.currentTimeMillis();
        if (reportStatus == ReportStatus.OPEN) {
            report.setRespondedAt(0L);
            report.setResolvedAt(0L);
        } else {
            if (report.getRespondedAt() == 0L) {
                report.setRespondedAt(now);
            }
            report.setResolvedAt(reportStatus == ReportStatus.IN_PROGRESS ? 0L : now);
        }
    }

    @Override
    public void addPendingReward(IReport report) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result -> {
                    String text = "core:reportreward:" + report.getIssuedBy();
                    return result.hset(text, report.getId().toString(), report.getId().toString());
                });
    }

    @Override
    public CompletableFuture<List<IReport>> getPendingRewards(UUID uUID) {
        CompletableFuture<java.util.Set<String>> completableFuture =
                CompletableFuture.supplyAsync(
                        () -> {
                            IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
                            return databaseHandler.runRedisCommand(
                                    result -> result.hgetall("core:reportreward:" + uUID).keySet());
                        },
                        AbstractDatabaseHandler.DB_EXECUTOR);
        return completableFuture.thenCompose(
                result2 ->
                        result2 != null && !result2.isEmpty()
                                ? this.repository.fetchAll(Filters.in("_id", result2))
                                : CompletableFuture.completedFuture(Collections.emptyList()));
    }

    @Override
    public void wipePendingRewards(UUID uUID) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result -> result.del(new String[] {"core:reportreward:" + uUID}));
    }

    @Override
    public void setRequestMute(boolean matches, UUID uUID) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result -> {
                    String text = "core:requestmute:" + uUID;
                    if (matches) {
                        result.set(text, uUID.toString());
                    } else {
                        result.del(new String[] {text});
                    }
                    return null;
                });
    }

    @Override
    public boolean isReportMute(UUID uUID) {
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        return databaseHandler.runRedisCommand(
                result -> result.get("core:requestmute:" + uUID) != null);
    }

    private long cutoff(int value) {
        return System.currentTimeMillis() - TimeUnit.DAYS.toMillis(Math.max(1, value));
    }

    public CommonPlatform getApi() {
        return this.api;
    }

    @Override
    public IReportRepository getRepository() {
        return this.repository;
    }
}

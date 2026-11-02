package xyz.refinedev.phoenix.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class ReportStatistics implements IReportStatistics {

    private final int days;

    private int total;

    private int open;

    private int inProgress;

    private int resolved;

    private int punished;

    private long time;

    private int count;

    private long timeTime;

    private int countCount;

    private final Map<UUID, Integer> integersById = new HashMap<>();

    private final Map<UUID, Long> longsById = new HashMap<>();

    private final Map<UUID, Integer> integersByIdMap = new HashMap<>();

    public ReportStatistics(int days) {
        this.days = days;
    }

    public void setReport(IReport report) {
        this.total++;
        switch (report.getStatus()) {
            case IN_PROGRESS:
                this.inProgress++;
                break;
            case RESOLVED:
                this.resolved++;
                break;
            case PUNISHED:
                this.punished++;
                break;
            default:
                this.open++;
        }
        if (report.getRespondedAt() > report.getIssuedAt()) {
            this.time = this.time + (report.getRespondedAt() - report.getIssuedAt());
            this.count++;
        }
        if (report.getResolvedAt() > report.getIssuedAt()) {
            this.timeTime = this.timeTime + (report.getResolvedAt() - report.getIssuedAt());
            this.countCount++;
        }
        if (report.isResolved() && report.getResolvedBy() != null) {
            this.integersById.merge(report.getResolvedBy(), 1, Integer::sum);
            if (report.getResolvedAt() > report.getIssuedAt()) {
                this.longsById.merge(
                        report.getResolvedBy(),
                        report.getResolvedAt() - report.getIssuedAt(),
                        Long::sum);
                this.integersByIdMap.merge(report.getResolvedBy(), 1, Integer::sum);
            }
        }
    }

    @Override
    public long getAverageResponse() {
        return this.count == 0 ? -1L : this.time / this.count;
    }

    @Override
    public long getAverageResolution() {
        return this.countCount == 0 ? -1L : this.timeTime / this.countCount;
    }

    @Override
    public int getOutstanding() {
        return this.open + this.inProgress;
    }

    @Override
    public int getHandledBy(UUID uUID) {
        return this.integersById.getOrDefault(uUID, 0);
    }

    @Override
    public long getAverageResolutionBy(UUID uUID) {
        int value;
        return (value = this.integersByIdMap.getOrDefault(uUID, 0)) == 0
                ? -1L
                : this.longsById.getOrDefault(uUID, 0L) / value;
    }

    @Override
    public List<Entry<UUID, Integer>> getLeaderboard(int value) {
        ArrayList values;
        (values = new ArrayList<>(this.integersById.entrySet()))
                .sort(Entry.comparingByValue(Comparator.reverseOrder()));
        return values.size() > value ? values.subList(0, value) : values;
    }

    @Override
    public int getDays() {
        return this.days;
    }

    @Override
    public int getTotal() {
        return this.total;
    }

    @Override
    public int getOpen() {
        return this.open;
    }

    @Override
    public int getInProgress() {
        return this.inProgress;
    }

    @Override
    public int getResolved() {
        return this.resolved;
    }

    @Override
    public int getPunished() {
        return this.punished;
    }

    public long getTime() {
        return this.time;
    }

    public int getCount() {
        return this.count;
    }

    public long getTimeTime() {
        return this.timeTime;
    }

    public int getCountCount() {
        return this.countCount;
    }

    public Map<UUID, Integer> getIntegersById() {
        return this.integersById;
    }

    public Map<UUID, Long> getLongsById() {
        return this.longsById;
    }

    public Map<UUID, Integer> getIntegersByIdMap() {
        return this.integersByIdMap;
    }
}

package xyz.refinedev.phoenix.report;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public interface IReportStatistics {

    int getDays();

    int getTotal();

    int getOpen();

    int getInProgress();

    int getResolved();

    int getPunished();

    int getOutstanding();

    long getAverageResponse();

    long getAverageResolution();

    int getHandledBy(UUID uUID);

    long getAverageResolutionBy(UUID uUID);

    List<Entry<UUID, Integer>> getLeaderboard(int value);
}

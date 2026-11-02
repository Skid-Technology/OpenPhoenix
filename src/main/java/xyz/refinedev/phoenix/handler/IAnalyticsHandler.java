package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.server.IServerData;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public interface IAnalyticsHandler {

    void cleanUp();

    default Map<String, String> getRetentionPlaceholders() {
        return Collections.emptyMap();
    }

    void addToLoggedIn(UUID uUID);

    void saveLoggedInTime(UUID uUID);

    int getTotalPeakPlayers();

    int getTotalPeakPlayers(int value);

    int getPeakPlayers();

    int getPeakPlayers(IServerData serverData);

    int getPeakPlayers(int value);

    int getPeakPlayers(IServerData serverData, int value);

    String getTotalAveragePlayTime(int value);

    String getTotalAveragePlayTimeDifference(int value);

    String getAveragePlayTime();

    String getAveragePlayTime(IServerData serverData);

    String getAveragePlayTime(int value);

    String getAveragePlayTime(IServerData serverData, int value);

    String getAveragePlayTimeDifference(IServerData serverData, int value);

    void updatePeakPlayers(int value);

    long getLogins(int value);

    void recordServerSwitch();

    long getTotalServerSwitches(int value);
}

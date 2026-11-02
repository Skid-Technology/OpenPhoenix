package xyz.refinedev.phoenix.server;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IServerData {

    void update(IServerData serverData);

    boolean isWhitelisted();

    boolean isOnline();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getServerName();

    String getDisplayName();

    Set<String> getServerGroups();

    String getRegion();

    String getVersion();

    long getStartedAt();

    int getPort();

    long getLastTick();

    double getTps();

    int getMaxPlayers();

    IWhitelistInfo getWhitelistInfo();

    boolean isCountPlayers();

    List<UUID> getOnlinePlayers();

    Map<String, String> getMetaData();

    void setDisplayName(String text);

    void setServerGroups(Set<String> values);

    void setRegion(String text);

    void setVersion(String text);

    void setStartedAt(long value);

    void setPort(int value);

    void setLastTick(long value);

    void setTps(double value);

    void setMaxPlayers(int value);

    void setWhitelistInfo(IWhitelistInfo whitelistInfo);

    void setCountPlayers(boolean matches);

    void setOnlinePlayers(List<UUID> values);

    void setMetaData(Map<String, String> entries);
}

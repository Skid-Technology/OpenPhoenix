package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.server.IServerData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface INetworkHandler {

    void loadMetadata();

    void saveMetadata();

    void fetchServers();

    void fetchProxies();

    void deleteServer(IServerData serverData);

    void deleteProxy(IServerData serverData);

    @Nullable
    IServerData getPresence(UUID uUID);

    String getNicePresence(UUID uUID);

    IServerData getProxy(UUID uUID);

    String getNiceProxy(UUID uUID);

    boolean isServerOnline(String text);

    boolean isOnline(UUID uUID);

    boolean isOnline(UUID uUID, boolean matches);

    @NotNull
    IServerData getOrAddServer(String text);

    @NotNull
    IServerData getOrAddProxyServer(String text);

    @Nullable
    IServerData getServerByName(String text);

    @Nullable
    IServerData getProxyByName(String text);

    @NotNull
    List<String> getGroups();

    @NotNull
    List<String> getProxyGroups();

    @NotNull
    Set<IServerData> getServersInGroup(String text);

    @NotNull
    Set<IServerData> getProxiesInGroup(String text);

    @NotNull
    List<String> getServersByGroup(String text);

    boolean doesGroupExist(String text);

    @NotNull
    Set<UUID> getAllUuids();

    @NotNull
    Set<UUID> getAllUuidsOnCount();

    @NotNull
    List<UUID> getOnlinePlayers(IServerData serverData);

    @NotNull
    List<UUID> getOnlyVanishedOnlinePlayers(IServerData serverData);

    List<UUID> getNonVanishedOnlinePlayers(IServerData serverData);

    int getMax();

    int getOnline();

    int getAllOnline();

    Set<IServerData> getServers();

    Set<IServerData> getProxies();

    String getServerName();

    String getDisplayName();

    Set<String> getServerGroups();

    String getRegion();

    String getWebsite();

    String getStore();

    String getDiscord();

    String getTeamspeak();

    String getTwitter();

    String getIp();

    boolean isServerJoinable();

    void setServerJoinable(boolean matches);
}

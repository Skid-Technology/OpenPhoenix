package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.server.ServerData;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.JsonTypes;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class RedisNetworkHandler implements INetworkHandler {

    private final CommonPlatform platform;

    private Set<IServerData> servers = new HashSet<>();

    private Set<IServerData> proxies = new HashSet<>();

    private String serverName;

    private String displayName;

    private String region;

    private String website;

    private String store;

    private String discord;

    private String teamspeak;

    private String twitter;

    private String ip;

    private Set<String> serverGroups;

    private boolean serverJoinable = false;

    private ReentrantLock reentrantLock;

    private ReentrantLock reentrantLockReentrantLock;

    public RedisNetworkHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.serverName = GlobalConfig.SERVER.NAME;
        this.displayName = GlobalConfig.SERVER.DISPLAYNAME;
        this.serverGroups = GlobalConfig.SERVER.GROUPS;
        this.region = GlobalConfig.SERVER.REGION;
        this.website = GlobalConfig.NETWORK.WEBSITE;
        this.store = GlobalConfig.NETWORK.STORE;
        this.discord = GlobalConfig.NETWORK.DISCORD;
        this.teamspeak = GlobalConfig.NETWORK.TEAMSPEAK;
        this.twitter = GlobalConfig.NETWORK.TWITTER;
        this.ip = GlobalConfig.NETWORK.IP;
        this.reentrantLock = new ReentrantLock();
        this.reentrantLockReentrantLock = new ReentrantLock();
        this.loadMetadata();
    }

    @Override
    public void loadMetadata() {
        Path path;
        if ((path = Paths.get("plugins/Phoenix/metadata.json")).toFile().exists()) {
            try {
                String text = new String(Files.readAllBytes(path));
                Map entries;
                if ((entries = (Map) this.platform.getGson().fromJson(text, JsonTypes.type5))
                        == null) {
                    return;
                }
                Map<String, String> entries2 = new ConcurrentHashMap(entries);
                this.platform.setMetaData(entries2);
            } catch (Exception exception) {
                this.platform
                        .getLogger()
                        .log(Level.SEVERE, "Failed to load server metadata: ", exception);
                this.platform.setMetaData(new HashMap<>());
            }
        }
    }

    @Override
    public void saveMetadata() {
        if (this.platform.getMetaData() != null) {
            Path path = Paths.get("plugins/Phoenix/metadata.json");
            try {
                Files.write(
                        path,
                        this.platform
                                .getGson()
                                .toJson(this.platform.getMetaData())
                                .getBytes(StandardCharsets.UTF_8));
            } catch (Exception exception) {
                this.platform
                        .getLogger()
                        .log(Level.SEVERE, "Failed to save server metadata: ", exception);
            }
        }
    }

    @Override
    public void fetchServers() {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    Map<String, String> entries;
                    Map<String, String> entries2 = entries = result.hgetall("px-servers");
                    entries2.forEach(
                            (result2, result3) ->
                                    this.applyServerData(
                                            this.platform
                                                    .getNetworkHandler()
                                                    .getOrAddServer(result2),
                                            result3));
                    applySet(this.servers, entries);
                });
    }

    @Override
    public void fetchProxies() {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    Map<String, String> entries;
                    Map<String, String> entries2 = entries = result.hgetall("px-proxies");
                    entries2.forEach(
                            (result2, result3) ->
                                    this.applyServerData(
                                            this.platform
                                                    .getNetworkHandler()
                                                    .getOrAddProxyServer(result2),
                                            result3));
                    applySet(this.proxies, entries);
                });
    }

    static void applySet(Set<IServerData> values, Map<String, String> entries) {
        HashSet<String> values2 = new HashSet<>();
        for (String text : entries.keySet()) {
            values2.add(text.toLowerCase(Locale.ROOT));
        }
        values.removeIf(
                result -> !values2.contains(result.getServerName().toLowerCase(Locale.ROOT)));
    }

    private void applyServerData(IServerData serverData, String text) {
        IServerData serverData2 =
                (IServerData) this.platform.getGson().fromJson(text, ServerData.class);
        serverData.update(serverData2);
    }

    @Override
    public void deleteServer(IServerData serverData) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.runRedisCommandAsync(
                result -> result.hdel("px-servers", new String[] {serverData.getServerName()}));
        this.servers.remove(serverData);
    }

    @Override
    public void deleteProxy(IServerData serverData) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.runRedisCommandAsync(
                result -> result.hdel("px-proxies", new String[] {serverData.getServerName()}));
        this.proxies.remove(serverData);
    }

    @Nullable
    @Override
    public IServerData getPresence(UUID uUID) {
        Iterator<IServerData> iterator2 = this.servers.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next()).getOnlinePlayers().contains(uUID)) {
                return serverData;
            }
        }
        return null;
    }

    @Override
    public String getNicePresence(UUID uUID) {
        IServerData serverData;
        return (serverData = this.getPresence(uUID)) != null ? serverData.getServerName() : "N/A";
    }

    @Override
    public IServerData getProxy(UUID uUID) {
        Iterator<IServerData> iterator2 = this.proxies.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next()).getOnlinePlayers().contains(uUID)) {
                return serverData;
            }
        }
        return null;
    }

    @Override
    public String getNiceProxy(UUID uUID) {
        IServerData serverData;
        return (serverData = this.getProxy(uUID)) != null ? serverData.getServerName() : "N/A";
    }

    @Override
    public boolean isServerOnline(String text) {
        IServerData serverData;
        return (serverData = this.getServerByName(text)) != null && serverData.isOnline();
    }

    @Override
    public boolean isOnline(UUID uUID) {
        return this.isOnline(uUID, false);
    }

    @Override
    public boolean isOnline(UUID uUID, boolean matches) {
        if (uUID == null) {
            return false;
        } else {
            return matches ? this.getProxy(uUID) != null : this.getPresence(uUID) != null;
        }
    }

    @NotNull
    @Override
    public IServerData getOrAddServer(String text) {
        IServerData serverData;
        if ((serverData = this.getServerByName(text)) != null) {
            return serverData;
        }
        ServerData serverData2 = new ServerData(text);
        this.servers.add(serverData2);
        return serverData2;
    }

    @NotNull
    @Override
    public IServerData getOrAddProxyServer(String text) {
        IServerData serverData;
        if ((serverData = this.getProxyByName(text)) != null) {
            return serverData;
        }
        ServerData serverData2 = new ServerData(text);
        this.proxies.add(serverData2);
        return serverData2;
    }

    @Nullable
    @Override
    public IServerData getServerByName(String text) {
        Iterator<IServerData> iterator2 = this.servers.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next())
                    .getServerName()
                    .equalsIgnoreCase(text)) {
                return serverData;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IServerData getProxyByName(String text) {
        Iterator<IServerData> iterator2 = this.proxies.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next())
                    .getServerName()
                    .equalsIgnoreCase(text)) {
                return serverData;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public List<String> getGroups() {
        HashSet values = new HashSet();
        for (IServerData serverData : this.servers) {
            values.addAll(serverData.getServerGroups());
        }
        return new ArrayList<>(values);
    }

    @NotNull
    @Override
    public List<String> getProxyGroups() {
        HashSet values = new HashSet();
        for (IServerData serverData : this.proxies) {
            values.addAll(serverData.getServerGroups());
        }
        return new ArrayList<>(values);
    }

    @NotNull
    @Override
    public Set<IServerData> getServersInGroup(String text) {
        if (text.equalsIgnoreCase("global")) {
            return this.servers;
        }
        HashSet<IServerData> values = new HashSet<>();
        Iterator<IServerData> iterator2 = this.servers.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next()).getServerGroups().contains(text)) {
                values.add(serverData);
            }
        }
        return values;
    }

    @NotNull
    @Override
    public Set<IServerData> getProxiesInGroup(String text) {
        if (text.equalsIgnoreCase("global")) {
            return this.proxies;
        }
        HashSet<IServerData> values = new HashSet<>();
        Iterator<IServerData> iterator2 = this.proxies.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next()).getServerGroups().contains(text)) {
                values.add(serverData);
            }
        }
        return values;
    }

    @NotNull
    @Override
    public List<String> getServersByGroup(String text) {
        ArrayList<String> values = new ArrayList<>();
        Iterator<IServerData> iterator2 = this.servers.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next()).getServerGroups().contains(text)) {
                values.add(serverData.getServerName());
            }
        }
        return values;
    }

    @Override
    public boolean doesGroupExist(String text) {
        return FastStreams.anyMatch(
                this.servers, result -> result.getServerGroups().contains(text));
    }

    @NotNull
    @Override
    public Set<UUID> getAllUuids() {
        HashSet<UUID> values = new HashSet<>();
        for (IServerData serverData : this.servers) {
            values.addAll(serverData.getOnlinePlayers());
        }
        return values;
    }

    @NotNull
    @Override
    public Set<UUID> getAllUuidsOnCount() {
        HashSet<UUID> values = new HashSet<>();
        Iterator<IServerData> iterator2 = this.servers.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next()).isCountPlayers()) {
                values.addAll(serverData.getOnlinePlayers());
            }
        }
        return values;
    }

    @NotNull
    @Override
    public List<UUID> getOnlinePlayers(IServerData serverData) {
        return serverData == null ? Collections.emptyList() : serverData.getOnlinePlayers();
    }

    @NotNull
    @Override
    public List<UUID> getOnlyVanishedOnlinePlayers(IServerData serverData) {
        if (serverData == null) {
            return Collections.emptyList();
        }
        ArrayList<UUID> values = new ArrayList<>();
        for (UUID uUID : serverData.getOnlinePlayers()) {
            if (this.platform.getModUtilsHook().isVanished(uUID)) {
                values.add(uUID);
            }
        }
        return values;
    }

    @Override
    public List<UUID> getNonVanishedOnlinePlayers(IServerData serverData) {
        if (serverData == null) {
            return Collections.emptyList();
        }
        ArrayList<UUID> values = new ArrayList<>();
        for (UUID uUID : serverData.getOnlinePlayers()) {
            if (!this.platform.getModUtilsHook().isVanished(uUID)) {
                values.add(uUID);
            }
        }
        return values;
    }

    @Override
    public int getMax() {
        int value = 0;
        Iterator<IServerData> iterator2 = this.getServers().iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            if ((serverData = (IServerData) iterator2.next()).isCountPlayers()) {
                value += serverData.getMaxPlayers();
            }
        }
        return value;
    }

    @Override
    public int getOnline() {
        int value = 0;
        for (UUID uUID : this.getAllUuidsOnCount()) {
            if (!this.platform.getModUtilsHook().isVanished(uUID)) {
                value++;
            }
        }
        return value;
    }

    @Override
    public int getAllOnline() {
        return this.getAllUuids().size();
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public Set<IServerData> getServers() {
        return this.servers;
    }

    @Override
    public Set<IServerData> getProxies() {
        return this.proxies;
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getRegion() {
        return this.region;
    }

    @Override
    public String getWebsite() {
        return this.website;
    }

    @Override
    public String getStore() {
        return this.store;
    }

    @Override
    public String getDiscord() {
        return this.discord;
    }

    @Override
    public String getTeamspeak() {
        return this.teamspeak;
    }

    @Override
    public String getTwitter() {
        return this.twitter;
    }

    @Override
    public String getIp() {
        return this.ip;
    }

    @Override
    public Set<String> getServerGroups() {
        return this.serverGroups;
    }

    @Override
    public boolean isServerJoinable() {
        return this.serverJoinable;
    }

    public ReentrantLock getReentrantLock() {
        return this.reentrantLock;
    }

    public ReentrantLock getReentrantLockReentrantLock() {
        return this.reentrantLockReentrantLock;
    }

    public void setServers(Set<IServerData> servers) {
        this.servers = servers;
    }

    public void setProxies(Set<IServerData> proxies) {
        this.proxies = proxies;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public void setTeamspeak(String teamspeak) {
        this.teamspeak = teamspeak;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setServerGroups(Set<String> serverGroups) {
        this.serverGroups = serverGroups;
    }

    @Override
    public void setServerJoinable(boolean serverJoinable) {
        this.serverJoinable = serverJoinable;
    }

    public void setReentrantLock(ReentrantLock reentrantLock2) {
        this.reentrantLock = reentrantLock2;
    }

    public void setReentrantLockReentrantLock(ReentrantLock reentrantLock) {
        this.reentrantLockReentrantLock = reentrantLock;
    }
}

package xyz.refinedev.phoenix.internal;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.server.ServerData;
import xyz.refinedev.phoenix.server.WhitelistInfo;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.VoidRedisCommand;
import xyz.refinedev.phoenix.utils.server.TPSUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class UpdateTask implements Runnable {

    private final Phoenix plugin;

    @Override
    public void run() {
        try {
            this.initialize();
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "There was an issue while trying to update server data. Cause: ",
                            exception);
        }
    }

    public void initialize() {
        if (this.plugin.isApiEnabled()) {
            this.initializeByPlugin();
            this.plugin.getNetworkHandler().fetchServers();
            this.plugin.getNetworkHandler().fetchProxies();
        }
    }

    public void initializeByPlugin() {
        IServerData serverData;
        (serverData = this.plugin.getNetworkHandler().getOrAddServer(GlobalConfig.SERVER.NAME))
                .setDisplayName(GlobalConfig.SERVER.DISPLAYNAME);
        serverData.setServerGroups(GlobalConfig.SERVER.GROUPS);
        serverData.setRegion(GlobalConfig.SERVER.REGION);
        serverData.setCountPlayers(GlobalConfig.SERVER.COUNT_PLAYERS);
        serverData.setOnlinePlayers(
                FastStreams.mapArrayList(Bukkit.getOnlinePlayers(), OfflinePlayer::getUniqueId));
        WhitelistInfo whitelistInfo =
                new WhitelistInfo(
                        this.plugin.getWhitelistHandler().isWhitelisted(),
                        this.plugin.getWhitelistHandler().getAccessTypes(),
                        this.plugin.getWhitelistHandler().getWhitelistRankUuid(),
                        this.plugin.getWhitelistHandler().getWhitelistedUsers());
        serverData.setWhitelistInfo(whitelistInfo);
        serverData.setLastTick(System.currentTimeMillis());
        serverData.setMaxPlayers(Bukkit.getMaxPlayers());
        serverData.setTps(TPSUtils.getRecentTps());
        serverData.setStartedAt(this.plugin.getStartedAt());
        serverData.setVersion(Phoenix.VERSION);
        serverData.setMetaData(
                this.plugin.getMetaData() != null && !this.plugin.getMetaData().isEmpty()
                        ? this.plugin.getMetaData()
                        : new ConcurrentHashMap<>());
        serverData.setPort(this.plugin.getServer().getPort());
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        IServerData serverData2 = serverData;
        VoidRedisCommand voidRedisCommand =
                result ->
                        result.hset(
                                "px-servers",
                                GlobalConfig.SERVER.NAME,
                                this.plugin.getGson().toJson(serverData2, ServerData.class));
        IServerData serverData3 = serverData;
        databaseHandler.executeRedisCommand(
                voidRedisCommand,
                result -> {
                    if (result.getCause() != null && result.getCause().getMessage() != null) {
                        this.plugin
                                .getLogger()
                                .severe(
                                        "There was an issue while updating the server data. ("
                                                + result.getCause().getMessage()
                                                + ")");
                        if (this.plugin.isDebugMode()) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            this.plugin
                                                    .getGson()
                                                    .toJson(serverData3, ServerData.class));
                        }
                    } else {
                        this.plugin
                                .getLogger()
                                .severe(
                                        "There was an issue while updating the server data. ("
                                                + result.getMessage()
                                                + ")");
                        if (this.plugin.isDebugMode()) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            this.plugin
                                                    .getGson()
                                                    .toJson(serverData3, ServerData.class));
                        }
                    }
                });
    }

    public UpdateTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

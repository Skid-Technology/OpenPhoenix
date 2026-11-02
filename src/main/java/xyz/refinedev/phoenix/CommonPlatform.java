package xyz.refinedev.phoenix;

import com.google.gson.Gson;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.phoenix.cache.api.IUUIDCache;
import xyz.refinedev.phoenix.handler.IAddressStatsHandler;
import xyz.refinedev.phoenix.handler.IAltHandler;
import xyz.refinedev.phoenix.handler.IAnalyticsHandler;
import xyz.refinedev.phoenix.handler.IAntiVPNHandler;
import xyz.refinedev.phoenix.handler.IChatLogHandler;
import xyz.refinedev.phoenix.handler.IChatSnapshotHandler;
import xyz.refinedev.phoenix.handler.ICommandLogHandler;
import xyz.refinedev.phoenix.handler.ICooldownHandler;
import xyz.refinedev.phoenix.handler.ICustomTagHandler;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.handler.IFilterHandler;
import xyz.refinedev.phoenix.handler.IGrantHandler;
import xyz.refinedev.phoenix.handler.IHashHandler;
import xyz.refinedev.phoenix.handler.ILoginHandler;
import xyz.refinedev.phoenix.handler.IMetricsHandler;
import xyz.refinedev.phoenix.handler.INetworkHandler;
import xyz.refinedev.phoenix.handler.INotificationHandler;
import xyz.refinedev.phoenix.handler.IPlaceholderHandler;
import xyz.refinedev.phoenix.handler.IProfileHandler;
import xyz.refinedev.phoenix.handler.IPunishmentHandler;
import xyz.refinedev.phoenix.handler.IPunishmentLadderHandler;
import xyz.refinedev.phoenix.handler.IRankHandler;
import xyz.refinedev.phoenix.handler.IReportCategoryHandler;
import xyz.refinedev.phoenix.handler.IReportHandler;
import xyz.refinedev.phoenix.handler.ISecurityHandler;
import xyz.refinedev.phoenix.handler.IServerHopHandler;
import xyz.refinedev.phoenix.handler.ITagHandler;
import xyz.refinedev.phoenix.handler.IWhitelistHandler;
import xyz.refinedev.phoenix.hook.ICosmeticsHook;
import xyz.refinedev.phoenix.hook.ILevelHook;
import xyz.refinedev.phoenix.hook.IModUtilsHook;
import xyz.refinedev.phoenix.packet.api.IPacketHandler;
import xyz.refinedev.phoenix.utils.ExecuteOnMainThread;

import java.util.Map;
import java.util.logging.Logger;

public interface CommonPlatform {

    boolean start(Logger logger);

    boolean isMainThread(Thread thread);

    void shutdown();

    boolean isMongoDB();

    Gson getGson();

    Logger getLogger();

    IDatabaseHandler getDatabaseHandler();

    IProfileHandler getProfileHandler();

    IRankHandler getRankHandler();

    IHashHandler getHashHandler();

    INetworkHandler getNetworkHandler();

    IWhitelistHandler getWhitelistHandler();

    IFilterHandler getFilterHandler();

    ITagHandler getTagHandler();

    IGrantHandler getGrantHandler();

    IPunishmentHandler getPunishmentHandler();

    ICooldownHandler getCooldownHandler();

    ICommandLogHandler getCommandLogHandler();

    INotificationHandler getNotificationHandler();

    IChatSnapshotHandler getChatSnapshotHandler();

    IPunishmentLadderHandler getPunishmentLadderHandler();

    ISecurityHandler getSecurityHandler();

    IReportHandler getReportHandler();

    IReportCategoryHandler getReportCategoryHandler();

    IPlaceholderHandler getPlaceholderHandler();

    ILoginHandler getLoginHandler();

    IAddressStatsHandler getAddressStatsHandler();

    IAltHandler getAltHandler();

    IServerHopHandler getServerHopHandler();

    IChatLogHandler getChatLogHandler();

    ICustomTagHandler getCustomTagHandler();

    IAnalyticsHandler getAnalyticsHandler();

    IAntiVPNHandler getAntiVPNHandler();

    IMetricsHandler getMetricsHandler();

    IPacketHandler getPacketHandler();

    ConfigHandler getConfigHandler();

    IUUIDCache getUuidCache();

    long getStartedAt();

    long getStoppedAt();

    Map<String, String> getMetaData();

    IModUtilsHook getModUtilsHook();

    ILevelHook getLevelHook();

    ICosmeticsHook getCosmeticsHook();

    ExecuteOnMainThread getExecuteOnMainThread();

    boolean isApiEnabled();

    boolean isDebugMode();

    boolean isUseModernPrefixes();

    boolean isUsingMongoDB();

    Thread getMainThread();

    void setGson(Gson gson);

    void setLogger(Logger logger);

    void setPacketHandler(IPacketHandler packetHandler);

    void setDatabaseHandler(IDatabaseHandler databaseHandler);

    void setProfileHandler(IProfileHandler profileHandler);

    void setRankHandler(IRankHandler rankHandler);

    void setHashHandler(IHashHandler hashHandler);

    void setNetworkHandler(INetworkHandler networkHandler);

    void setWhitelistHandler(IWhitelistHandler whitelistHandler);

    void setFilterHandler(IFilterHandler filterHandler);

    void setTagHandler(ITagHandler tagHandler);

    void setGrantHandler(IGrantHandler grantHandler);

    void setPunishmentHandler(IPunishmentHandler punishmentHandler);

    void setCooldownHandler(ICooldownHandler cooldownHandler);

    void setCommandLogHandler(ICommandLogHandler commandLogHandler);

    void setNotificationHandler(INotificationHandler notificationHandler);

    void setChatSnapshotHandler(IChatSnapshotHandler chatSnapshotHandler);

    void setPunishmentLadderHandler(IPunishmentLadderHandler punishmentLadderHandler);

    void setSecurityHandler(ISecurityHandler securityHandler);

    void setPlaceholderHandler(IPlaceholderHandler placeholderHandler);

    void setLoginHandler(ILoginHandler loginHandler);

    void setAddressStatsHandler(IAddressStatsHandler addressStatsHandler);

    void setAltHandler(IAltHandler altHandler);

    void setServerHopHandler(IServerHopHandler serverHopHandler);

    void setChatLogHandler(IChatLogHandler chatLogHandler);

    void setCustomTagHandler(ICustomTagHandler customTagHandler);

    void setAnalyticsHandler(IAnalyticsHandler analyticsHandler);

    void setAntiVPNHandler(IAntiVPNHandler antiVPNHandler);

    void setMetricsHandler(IMetricsHandler metricsHandler);

    void setConfigHandler(ConfigHandler configHandler);

    void setUuidCache(IUUIDCache uUIDCache);

    void setStartedAt(long value);

    void setStoppedAt(long value);

    void setMetaData(Map<String, String> entries);

    void setModUtilsHook(IModUtilsHook modUtilsHook);

    void setLevelHook(ILevelHook levelHook);

    void setCosmeticsHook(ICosmeticsHook cosmeticsHook);

    void setExecuteOnMainThread(ExecuteOnMainThread executeOnMainThread);

    void setApiEnabled(boolean matches);

    void setDebugMode(boolean matches);

    void setUseModernPrefixes(boolean matches);

    void setMainThread(Thread thread);

    default boolean isFeatureFlag(String text) {
        return true;
    }

    default void trackMetric(String text, double value) {}
}

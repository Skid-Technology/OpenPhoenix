package xyz.refinedev.phoenix;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.phoenix.alt.AddressDensityBackfill;
import xyz.refinedev.phoenix.alt.AltSignal;
import xyz.refinedev.phoenix.alt.ClientFingerprint;
import xyz.refinedev.phoenix.alt.IAltSignal;
import xyz.refinedev.phoenix.alt.IClientFingerprint;
import xyz.refinedev.phoenix.cache.api.IUUIDCache;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.AnalyticsHandler;
import xyz.refinedev.phoenix.handler.ChatLogHandler;
import xyz.refinedev.phoenix.handler.ChatSnapshotHandler;
import xyz.refinedev.phoenix.handler.CommandLogHandler;
import xyz.refinedev.phoenix.handler.CooldownHandler;
import xyz.refinedev.phoenix.handler.CustomTagHandler;
import xyz.refinedev.phoenix.handler.FilterHandler;
import xyz.refinedev.phoenix.handler.GrantHandler;
import xyz.refinedev.phoenix.handler.HashHandler;
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
import xyz.refinedev.phoenix.handler.LoginHandler;
import xyz.refinedev.phoenix.handler.MetricsHandler;
import xyz.refinedev.phoenix.handler.MongoAddressStatsHandler;
import xyz.refinedev.phoenix.handler.MongoAltHandler;
import xyz.refinedev.phoenix.handler.PlaceholderHandler;
import xyz.refinedev.phoenix.handler.ProfileHandler;
import xyz.refinedev.phoenix.handler.PunishmentHandler;
import xyz.refinedev.phoenix.handler.PunishmentLadderHandler;
import xyz.refinedev.phoenix.handler.RankHandler;
import xyz.refinedev.phoenix.handler.RedisAntiVPNHandler;
import xyz.refinedev.phoenix.handler.RedisNetworkHandler;
import xyz.refinedev.phoenix.handler.RedisNotificationHandler;
import xyz.refinedev.phoenix.handler.ReportCategoryHandler;
import xyz.refinedev.phoenix.handler.ReportHandler;
import xyz.refinedev.phoenix.handler.SecurityHandler;
import xyz.refinedev.phoenix.handler.ServerHopHandler;
import xyz.refinedev.phoenix.handler.TagHandler;
import xyz.refinedev.phoenix.handler.WhitelistHandler;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.MySQLDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.local.LocalRedis;
import xyz.refinedev.phoenix.hook.ICosmeticsHook;
import xyz.refinedev.phoenix.hook.ILevelHook;
import xyz.refinedev.phoenix.hook.IModUtilsHook;
import xyz.refinedev.phoenix.hook.impl.CosmeticsHook;
import xyz.refinedev.phoenix.hook.impl.LevelHook;
import xyz.refinedev.phoenix.hook.impl.ModUtilsHook;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.notification.Notification;
import xyz.refinedev.phoenix.packet.api.IPacketHandler;
import xyz.refinedev.phoenix.packet.api.PacketHandler;
import xyz.refinedev.phoenix.profile.INameChange;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.NameChange;
import xyz.refinedev.phoenix.profile.RedisProfile;
import xyz.refinedev.phoenix.profile.antivpn.AntiVPNData;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatLog;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatSnapshot;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatLog;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.profile.commandlog.CommandLog;
import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;
import xyz.refinedev.phoenix.profile.cooldown.Cooldown;
import xyz.refinedev.phoenix.profile.cooldown.ICooldown;
import xyz.refinedev.phoenix.profile.disguise.DisguiseData;
import xyz.refinedev.phoenix.profile.disguise.DisguiseHistory;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseHistory;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.disguise.Skin;
import xyz.refinedev.phoenix.profile.filter.Filter;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.grant.Grant;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.hop.IServerHop;
import xyz.refinedev.phoenix.profile.hop.ServerHop;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.login.Login;
import xyz.refinedev.phoenix.profile.notes.INote;
import xyz.refinedev.phoenix.profile.notes.Note;
import xyz.refinedev.phoenix.profile.permission.IProfilePermission;
import xyz.refinedev.phoenix.profile.permission.ProfilePermission;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Proof;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWaveEntry;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWaveEntry;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.profile.punishment.ladder.PunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.PunishmentLadderType;
import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.profile.security.SecurityUser;
import xyz.refinedev.phoenix.profile.tag.CustomTag;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.profile.tag.Tag;
import xyz.refinedev.phoenix.profile.tag.TagCategory;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.Rank;
import xyz.refinedev.phoenix.rank.permission.IPermission;
import xyz.refinedev.phoenix.rank.permission.Permission;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.Report;
import xyz.refinedev.phoenix.report.ReportCategory;
import xyz.refinedev.phoenix.repository.impl.RedisUUIDCache;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.server.IWhitelistInfo;
import xyz.refinedev.phoenix.server.ServerData;
import xyz.refinedev.phoenix.server.WhitelistInfo;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.ExecuteOnMainThread;
import xyz.refinedev.phoenix.utils.GsonExclusionStrategy;
import xyz.refinedev.phoenix.utils.gson.EnumTypeAdapterFactory;
import xyz.refinedev.phoenix.utils.gson.OptionalTypeAdapterFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PhoenixPlatform implements CommonPlatform {

    public static CommonPlatform platform;

    private Gson gson;

    private Logger logger;

    private IDatabaseHandler databaseHandler;

    private ConfigHandler configHandler;

    private IProfileHandler profileHandler;

    private IRankHandler rankHandler;

    private IHashHandler hashHandler;

    private INetworkHandler networkHandler;

    private IWhitelistHandler whitelistHandler;

    private IFilterHandler filterHandler;

    private ITagHandler tagHandler;

    private IGrantHandler grantHandler;

    private IPunishmentHandler punishmentHandler;

    private ICooldownHandler cooldownHandler;

    private ICommandLogHandler commandLogHandler;

    private INotificationHandler notificationHandler;

    private IChatSnapshotHandler chatSnapshotHandler;

    private IPunishmentLadderHandler punishmentLadderHandler;

    private IReportHandler reportHandler;

    private IReportCategoryHandler reportCategoryHandler;

    private ISecurityHandler securityHandler;

    private IPlaceholderHandler placeholderHandler;

    private ILoginHandler loginHandler;

    private IAddressStatsHandler addressStatsHandler;

    private IAltHandler altHandler;

    private IServerHopHandler serverHopHandler;

    private IChatLogHandler chatLogHandler;

    private ICustomTagHandler customTagHandler;

    private IAnalyticsHandler analyticsHandler;

    private IAntiVPNHandler antiVPNHandler;

    private IMetricsHandler metricsHandler;

    private IPacketHandler packetHandler;

    private IUUIDCache uuidCache;

    private long startedAt = 0L;

    private long stoppedAt = 0L;

    private Map<String, String> metaData = new ConcurrentHashMap<>();

    private IModUtilsHook modUtilsHook = new ModUtilsHook();

    private ILevelHook levelHook = new LevelHook();

    private ICosmeticsHook cosmeticsHook = new CosmeticsHook();

    private ExecuteOnMainThread executeOnMainThread;

    public boolean apiEnabled = false;

    public boolean debugMode = false;

    public boolean useModernPrefixes = false;

    public boolean mongoDB = true;

    private Thread mainThread;

    @Override
    public boolean start(Logger logger2) {
        if (this.executeOnMainThread == null) {
            logger2.severe("Execute on main thread is null.");
            System.exit(0);
            return false;
        }
        this.logger = logger2;
        platform = this;
        PlatformGetter.instance = this;
        this.configHandler = new ConfigHandler(logger2);
        new GlobalConfig(this.configHandler).load();
        this.gson =
                new GsonBuilder()
                        .setPrettyPrinting()
                        .serializeNulls()
                        .setExclusionStrategies(
                                new ExclusionStrategy[] {new GsonExclusionStrategy()})
                        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(INotification.class, Notification.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IAntiVPNData.class, AntiVPNData.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IChatEntry.class, ChatEntry.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IChatLog.class, ChatLog.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IChatSnapshot.class, ChatSnapshot.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ICommandLog.class, CommandLog.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ICooldown.class, Cooldown.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IDisguiseData.class, DisguiseData.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(
                                        IDisguiseHistory.class, DisguiseHistory.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ISkin.class, Skin.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IFilter.class, Filter.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IGrant.class, Grant.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ILogin.class, Login.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IServerHop.class, ServerHop.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(INote.class, Note.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(INameChange.class, NameChange.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(
                                        IProfilePermission.class, ProfilePermission.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(
                                        IPunishmentLadder.class, PunishmentLadder.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(
                                        IPunishmentLadderType.class, PunishmentLadderType.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IProof.class, Proof.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IBanWave.class, BanWave.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IBanWaveEntry.class, BanWaveEntry.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IPunishment.class, Punishment.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IReport.class, Report.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(
                                        IReportCategory.class, ReportCategory.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ISecurityUser.class, SecurityUser.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ICustomTag.class, CustomTag.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ITag.class, Tag.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(ITagCategory.class, TagCategory.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IProfile.class, RedisProfile.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IScope.class, Scope.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IRank.class, Rank.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IPermission.class, Permission.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(
                                        IWhitelistInfo.class, WhitelistInfo.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IServerData.class, ServerData.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(IAltSignal.class, AltSignal.class))
                        .registerTypeAdapterFactory(
                                EnumTypeAdapterFactory.of(
                                        IClientFingerprint.class, ClientFingerprint.class))
                        .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
                        .create();
        if (GlobalConfig.DATABASE.DATABASE_TYPE.equalsIgnoreCase("MySQL")) {
            this.databaseHandler = new MySQLDatabaseHandler(this);
            this.mongoDB = false;
        } else {
            this.databaseHandler = new MongoDatabaseHandler(this);
            this.mongoDB = true;
        }
        if (LocalRedis.matchesEnabled(logger2, "the REST API")) {
            return false;
        }
        if (!this.databaseHandler.enable()) {
            logger2.severe("There was an issue with the database! Disabling...");
            return false;
        }
        this.packetHandler = new PacketHandler(this);
        this.packetHandler.enable();
        this.useModernPrefixes = GlobalConfig.SERVER.USE_MODERN_RANK_PREFIX;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalConfig.SERVER.DATE_FORMAT);
        try {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(GlobalConfig.SERVER.TIMEZONE));
            logger2.info("TimeZone in use: " + GlobalConfig.SERVER.TIMEZONE);
        } catch (Exception exception) {
            logger2.severe("Failed to set TimeZone to: " + GlobalConfig.SERVER.TIMEZONE);
        }
        DateUtils.setDateFormat(simpleDateFormat);
        this.analyticsHandler = new AnalyticsHandler(this);
        this.whitelistHandler = new WhitelistHandler(this);
        this.rankHandler = new RankHandler(this);
        this.profileHandler = new ProfileHandler(this);
        this.networkHandler = new RedisNetworkHandler(this);
        this.analyticsHandler.cleanUp();
        this.hashHandler = new HashHandler();
        this.filterHandler = new FilterHandler(this);
        this.tagHandler = new TagHandler(this);
        this.grantHandler = new GrantHandler(this);
        this.punishmentHandler = new PunishmentHandler(this);
        this.loginHandler = new LoginHandler(this);
        this.addressStatsHandler = new MongoAddressStatsHandler(this);
        this.altHandler = new MongoAltHandler(this);
        new AddressDensityBackfill(this).initialize();
        this.serverHopHandler = new ServerHopHandler(this);
        this.cooldownHandler = new CooldownHandler();
        this.commandLogHandler = new CommandLogHandler(this);
        this.notificationHandler = new RedisNotificationHandler(this);
        this.chatSnapshotHandler = new ChatSnapshotHandler(this);
        this.punishmentLadderHandler = new PunishmentLadderHandler(this);
        this.reportCategoryHandler = new ReportCategoryHandler(this);
        this.reportHandler = new ReportHandler(this);
        this.securityHandler = new SecurityHandler(this);
        this.chatLogHandler = new ChatLogHandler(this);
        this.placeholderHandler = new PlaceholderHandler();
        this.customTagHandler = new CustomTagHandler(this);
        this.antiVPNHandler = new RedisAntiVPNHandler(this);
        this.antiVPNHandler.fetchAntiVpnBypass();
        this.metricsHandler = new MetricsHandler(this);
        this.metricsHandler.start();
        this.uuidCache = new RedisUUIDCache(this);
        this.uuidCache.loadFromCache();
        Calendar calendar;
        if ((calendar = Calendar.getInstance()).get(5) == 25 && calendar.get(2) == 11) {
            logger2.info("We at the Refine Development team wish you a merry christmas!");
        }
        this.apiEnabled = true;
        return true;
    }

    @Override
    public boolean isMainThread(Thread thread) {
        return this.mainThread == null ? false : thread == this.mainThread;
    }

    @Override
    public void shutdown() {
        this.networkHandler.saveMetadata();
        if (this.metricsHandler != null) {
            this.metricsHandler.stop();
        }
        this.packetHandler.disable();
        this.databaseHandler.disable();
        this.apiEnabled = false;
    }

    @Override
    public boolean isMongoDB() {
        return this.mongoDB;
    }

    @Override
    public Gson getGson() {
        return this.gson;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public IDatabaseHandler getDatabaseHandler() {
        return this.databaseHandler;
    }

    @Override
    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    @Override
    public IProfileHandler getProfileHandler() {
        return this.profileHandler;
    }

    @Override
    public IRankHandler getRankHandler() {
        return this.rankHandler;
    }

    @Override
    public IHashHandler getHashHandler() {
        return this.hashHandler;
    }

    @Override
    public INetworkHandler getNetworkHandler() {
        return this.networkHandler;
    }

    @Override
    public IWhitelistHandler getWhitelistHandler() {
        return this.whitelistHandler;
    }

    @Override
    public IFilterHandler getFilterHandler() {
        return this.filterHandler;
    }

    @Override
    public ITagHandler getTagHandler() {
        return this.tagHandler;
    }

    @Override
    public IGrantHandler getGrantHandler() {
        return this.grantHandler;
    }

    @Override
    public IPunishmentHandler getPunishmentHandler() {
        return this.punishmentHandler;
    }

    @Override
    public ICooldownHandler getCooldownHandler() {
        return this.cooldownHandler;
    }

    @Override
    public ICommandLogHandler getCommandLogHandler() {
        return this.commandLogHandler;
    }

    @Override
    public INotificationHandler getNotificationHandler() {
        return this.notificationHandler;
    }

    @Override
    public IChatSnapshotHandler getChatSnapshotHandler() {
        return this.chatSnapshotHandler;
    }

    @Override
    public IPunishmentLadderHandler getPunishmentLadderHandler() {
        return this.punishmentLadderHandler;
    }

    @Override
    public IReportHandler getReportHandler() {
        return this.reportHandler;
    }

    @Override
    public IReportCategoryHandler getReportCategoryHandler() {
        return this.reportCategoryHandler;
    }

    @Override
    public ISecurityHandler getSecurityHandler() {
        return this.securityHandler;
    }

    @Override
    public IPlaceholderHandler getPlaceholderHandler() {
        return this.placeholderHandler;
    }

    @Override
    public ILoginHandler getLoginHandler() {
        return this.loginHandler;
    }

    @Override
    public IAddressStatsHandler getAddressStatsHandler() {
        return this.addressStatsHandler;
    }

    @Override
    public IAltHandler getAltHandler() {
        return this.altHandler;
    }

    @Override
    public IServerHopHandler getServerHopHandler() {
        return this.serverHopHandler;
    }

    @Override
    public IChatLogHandler getChatLogHandler() {
        return this.chatLogHandler;
    }

    @Override
    public ICustomTagHandler getCustomTagHandler() {
        return this.customTagHandler;
    }

    @Override
    public IAnalyticsHandler getAnalyticsHandler() {
        return this.analyticsHandler;
    }

    @Override
    public IAntiVPNHandler getAntiVPNHandler() {
        return this.antiVPNHandler;
    }

    @Override
    public IMetricsHandler getMetricsHandler() {
        return this.metricsHandler;
    }

    @Override
    public IPacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    @Override
    public IUUIDCache getUuidCache() {
        return this.uuidCache;
    }

    @Override
    public long getStartedAt() {
        return this.startedAt;
    }

    @Override
    public long getStoppedAt() {
        return this.stoppedAt;
    }

    @Override
    public Map<String, String> getMetaData() {
        return this.metaData;
    }

    @Override
    public IModUtilsHook getModUtilsHook() {
        return this.modUtilsHook;
    }

    @Override
    public ILevelHook getLevelHook() {
        return this.levelHook;
    }

    @Override
    public ICosmeticsHook getCosmeticsHook() {
        return this.cosmeticsHook;
    }

    @Override
    public ExecuteOnMainThread getExecuteOnMainThread() {
        return this.executeOnMainThread;
    }

    @Override
    public boolean isApiEnabled() {
        return this.apiEnabled;
    }

    @Override
    public boolean isDebugMode() {
        return this.debugMode;
    }

    @Override
    public boolean isUseModernPrefixes() {
        return this.useModernPrefixes;
    }

    @Override
    public boolean isUsingMongoDB() {
        return this.mongoDB;
    }

    @Override
    public Thread getMainThread() {
        return this.mainThread;
    }

    @Override
    public void setGson(Gson gson2) {
        this.gson = gson2;
    }

    @Override
    public void setLogger(Logger logger2) {
        this.logger = logger2;
    }

    @Override
    public void setDatabaseHandler(IDatabaseHandler databaseHandler2) {
        this.databaseHandler = databaseHandler2;
    }

    @Override
    public void setConfigHandler(ConfigHandler configHandler2) {
        this.configHandler = configHandler2;
    }

    @Override
    public void setProfileHandler(IProfileHandler profileHandler2) {
        this.profileHandler = profileHandler2;
    }

    @Override
    public void setRankHandler(IRankHandler rankHandler2) {
        this.rankHandler = rankHandler2;
    }

    @Override
    public void setHashHandler(IHashHandler hashHandler2) {
        this.hashHandler = hashHandler2;
    }

    @Override
    public void setNetworkHandler(INetworkHandler networkHandler2) {
        this.networkHandler = networkHandler2;
    }

    @Override
    public void setWhitelistHandler(IWhitelistHandler whitelistHandler2) {
        this.whitelistHandler = whitelistHandler2;
    }

    @Override
    public void setFilterHandler(IFilterHandler filterHandler2) {
        this.filterHandler = filterHandler2;
    }

    @Override
    public void setTagHandler(ITagHandler tagHandler2) {
        this.tagHandler = tagHandler2;
    }

    @Override
    public void setGrantHandler(IGrantHandler grantHandler2) {
        this.grantHandler = grantHandler2;
    }

    @Override
    public void setPunishmentHandler(IPunishmentHandler punishmentHandler2) {
        this.punishmentHandler = punishmentHandler2;
    }

    @Override
    public void setCooldownHandler(ICooldownHandler cooldownHandler2) {
        this.cooldownHandler = cooldownHandler2;
    }

    @Override
    public void setCommandLogHandler(ICommandLogHandler commandLogHandler2) {
        this.commandLogHandler = commandLogHandler2;
    }

    @Override
    public void setNotificationHandler(INotificationHandler notificationHandler2) {
        this.notificationHandler = notificationHandler2;
    }

    @Override
    public void setChatSnapshotHandler(IChatSnapshotHandler chatSnapshotHandler2) {
        this.chatSnapshotHandler = chatSnapshotHandler2;
    }

    @Override
    public void setPunishmentLadderHandler(IPunishmentLadderHandler punishmentLadderHandler2) {
        this.punishmentLadderHandler = punishmentLadderHandler2;
    }

    public void setReportHandler(IReportHandler reportHandler2) {
        this.reportHandler = reportHandler2;
    }

    public void setReportCategoryHandler(IReportCategoryHandler reportCategoryHandler2) {
        this.reportCategoryHandler = reportCategoryHandler2;
    }

    @Override
    public void setSecurityHandler(ISecurityHandler securityHandler2) {
        this.securityHandler = securityHandler2;
    }

    @Override
    public void setPlaceholderHandler(IPlaceholderHandler placeholderHandler2) {
        this.placeholderHandler = placeholderHandler2;
    }

    @Override
    public void setLoginHandler(ILoginHandler loginHandler2) {
        this.loginHandler = loginHandler2;
    }

    @Override
    public void setAddressStatsHandler(IAddressStatsHandler addressStatsHandler2) {
        this.addressStatsHandler = addressStatsHandler2;
    }

    @Override
    public void setAltHandler(IAltHandler altHandler2) {
        this.altHandler = altHandler2;
    }

    @Override
    public void setServerHopHandler(IServerHopHandler serverHopHandler2) {
        this.serverHopHandler = serverHopHandler2;
    }

    @Override
    public void setChatLogHandler(IChatLogHandler chatLogHandler2) {
        this.chatLogHandler = chatLogHandler2;
    }

    @Override
    public void setCustomTagHandler(ICustomTagHandler customTagHandler2) {
        this.customTagHandler = customTagHandler2;
    }

    @Override
    public void setAnalyticsHandler(IAnalyticsHandler analyticsHandler2) {
        this.analyticsHandler = analyticsHandler2;
    }

    @Override
    public void setAntiVPNHandler(IAntiVPNHandler antiVPNHandler2) {
        this.antiVPNHandler = antiVPNHandler2;
    }

    @Override
    public void setMetricsHandler(IMetricsHandler metricsHandler2) {
        this.metricsHandler = metricsHandler2;
    }

    @Override
    public void setPacketHandler(IPacketHandler packetHandler2) {
        this.packetHandler = packetHandler2;
    }

    @Override
    public void setUuidCache(IUUIDCache uUIDCache) {
        this.uuidCache = uUIDCache;
    }

    @Override
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    @Override
    public void setStoppedAt(long stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    @Override
    public void setMetaData(Map<String, String> entries) {
        this.metaData = entries;
    }

    @Override
    public void setModUtilsHook(IModUtilsHook modUtilsHook2) {
        this.modUtilsHook = modUtilsHook2;
    }

    @Override
    public void setLevelHook(ILevelHook levelHook2) {
        this.levelHook = levelHook2;
    }

    @Override
    public void setCosmeticsHook(ICosmeticsHook cosmeticsHook2) {
        this.cosmeticsHook = cosmeticsHook2;
    }

    @Override
    public void setExecuteOnMainThread(ExecuteOnMainThread executeOnMainThread2) {
        this.executeOnMainThread = executeOnMainThread2;
    }

    @Override
    public void setApiEnabled(boolean apiEnabled) {
        this.apiEnabled = apiEnabled;
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public void setUseModernPrefixes(boolean useModernPrefixes) {
        this.useModernPrefixes = useModernPrefixes;
    }

    public void setMongoDB(boolean mongoDB) {
        this.mongoDB = mongoDB;
    }

    @Override
    public void setMainThread(Thread thread) {
        this.mainThread = thread;
    }

    public static CommonPlatform getPlatform() {
        return platform;
    }
}

package xyz.refinedev.phoenix;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import me.creaxx.api.NMSHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerCommon;
import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;
import xyz.refinedev.lib.conversations.spigot.SpigotConversationManager;
import xyz.refinedev.lib.folialib.FoliaLib;
import xyz.refinedev.lib.kyori.adventure.platform.bukkit.BukkitAudiences;
import xyz.refinedev.phoenix.alt.AltSignal;
import xyz.refinedev.phoenix.alt.ClientFingerprint;
import xyz.refinedev.phoenix.alt.IAltSignal;
import xyz.refinedev.phoenix.alt.IClientFingerprint;
import xyz.refinedev.phoenix.cache.api.IUUIDCache;
import xyz.refinedev.phoenix.chat.limitation.FrozenChatLimitation;
import xyz.refinedev.phoenix.chat.limitation.MutedChatLimitation;
import xyz.refinedev.phoenix.chat.limitation.SlowChatLimitation;
import xyz.refinedev.phoenix.commands.PhoenixCommandHandler;
import xyz.refinedev.phoenix.config.ConfigLocation;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.ModModeConfig;
import xyz.refinedev.phoenix.config.NameTagsConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.config.Tip;
import xyz.refinedev.phoenix.config.TipsConfig;
import xyz.refinedev.phoenix.config.YamlProblem;
import xyz.refinedev.phoenix.config.migration.PxBasicMigration;
import xyz.refinedev.phoenix.config.migration.PxStaffMigration;
import xyz.refinedev.phoenix.config.provider.LocationProvider;
import xyz.refinedev.phoenix.config.provider.ModModeItemProvider;
import xyz.refinedev.phoenix.config.provider.NameTagLineProvider;
import xyz.refinedev.phoenix.config.provider.TipProvider;
import xyz.refinedev.phoenix.dialog.api.DialogHandler;
import xyz.refinedev.phoenix.dialog.impl.AuthDialog;
import xyz.refinedev.phoenix.dialog.impl.ConfirmDialog;
import xyz.refinedev.phoenix.dialog.impl.InputDialog;
import xyz.refinedev.phoenix.dialog.impl.PunishConfirmDialog;
import xyz.refinedev.phoenix.dialog.impl.PunishmentProofDialog;
import xyz.refinedev.phoenix.dialog.impl.RulesDialog;
import xyz.refinedev.phoenix.dialog.impl.SettingsDialog;
import xyz.refinedev.phoenix.dialog.impl.SocialsDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffFreezeDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffOnlineDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffPanicDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffReportDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffReportManageDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffReportProofDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffReportsDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffWatchlistDialog;
import xyz.refinedev.phoenix.dialog.impl.grant.GrantConfirmDialog;
import xyz.refinedev.phoenix.dialog.impl.grant.GrantDurationDialog;
import xyz.refinedev.phoenix.dialog.impl.grant.GrantRankDialog;
import xyz.refinedev.phoenix.dialog.impl.grant.GrantReasonDialog;
import xyz.refinedev.phoenix.dialog.impl.grant.GrantScopeDialog;
import xyz.refinedev.phoenix.editor.EditorConfigurationOperations;
import xyz.refinedev.phoenix.editor.EditorHandler;
import xyz.refinedev.phoenix.editor.EditorPermissionOperations;
import xyz.refinedev.phoenix.editor.EditorProfileErasureOperations;
import xyz.refinedev.phoenix.editor.EditorServerOperations;
import xyz.refinedev.phoenix.editor.EditorSetupOperations;
import xyz.refinedev.phoenix.editor.EditorWhitelistOperations;
import xyz.refinedev.phoenix.editor.PanelHandler;
import xyz.refinedev.phoenix.editor.panel.PanelBroadcast;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;
import xyz.refinedev.phoenix.handler.AfkHandler;
import xyz.refinedev.phoenix.handler.AnomalyHandler;
import xyz.refinedev.phoenix.handler.AuthHandler;
import xyz.refinedev.phoenix.handler.BanWaveHandler;
import xyz.refinedev.phoenix.handler.BukkitLocaleHandler;
import xyz.refinedev.phoenix.handler.BukkitReportHandler;
import xyz.refinedev.phoenix.handler.ChatCompletionHandler;
import xyz.refinedev.phoenix.handler.ChatHandler;
import xyz.refinedev.phoenix.handler.ConfigSyncHandler;
import xyz.refinedev.phoenix.handler.DiscordHandler;
import xyz.refinedev.phoenix.handler.DisguiseHandler;
import xyz.refinedev.phoenix.handler.FreezeHandler;
import xyz.refinedev.phoenix.handler.HookHandler;
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
import xyz.refinedev.phoenix.handler.ISecurityHandler;
import xyz.refinedev.phoenix.handler.IServerHopHandler;
import xyz.refinedev.phoenix.handler.ITagHandler;
import xyz.refinedev.phoenix.handler.IWhitelistHandler;
import xyz.refinedev.phoenix.handler.ImportHandler;
import xyz.refinedev.phoenix.handler.McLogsHandler;
import xyz.refinedev.phoenix.handler.ModModeHandler;
import xyz.refinedev.phoenix.handler.PanicHandler;
import xyz.refinedev.phoenix.handler.RankIconHandler;
import xyz.refinedev.phoenix.handler.RebootHandler;
import xyz.refinedev.phoenix.handler.ReportCategoryHandler;
import xyz.refinedev.phoenix.handler.ReportDetailsHandler;
import xyz.refinedev.phoenix.handler.ResourcePackHandler;
import xyz.refinedev.phoenix.handler.ServerHopHandler;
import xyz.refinedev.phoenix.handler.ServerLinkHandler;
import xyz.refinedev.phoenix.handler.TablistOrderHandler;
import xyz.refinedev.phoenix.handler.TransferHandler;
import xyz.refinedev.phoenix.handler.VanishHandler;
import xyz.refinedev.phoenix.handler.VisibilityHandler;
import xyz.refinedev.phoenix.handler.WaypointHandler;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.MySQLDatabaseHandler;
import xyz.refinedev.phoenix.hook.ICosmeticsHook;
import xyz.refinedev.phoenix.hook.ILevelHook;
import xyz.refinedev.phoenix.hook.IModUtilsHook;
import xyz.refinedev.phoenix.hook.impl.BukkitModUtilsHook;
import xyz.refinedev.phoenix.hook.impl.CosmeticsHook;
import xyz.refinedev.phoenix.hook.impl.LevelHook;
import xyz.refinedev.phoenix.hook.impl.ModUtilsHook;
import xyz.refinedev.phoenix.internal.BuildInfo;
import xyz.refinedev.phoenix.internal.ChildPermissionCache;
import xyz.refinedev.phoenix.internal.DefaultPermissionMap;
import xyz.refinedev.phoenix.internal.GrantExpiryTask;
import xyz.refinedev.phoenix.internal.PermissionMap;
import xyz.refinedev.phoenix.internal.SubscriptionMap;
import xyz.refinedev.phoenix.internal.UpdateTask;
import xyz.refinedev.phoenix.listener.EntityDamageListener;
import xyz.refinedev.phoenix.listener.NameTagVisibilityListener;
import xyz.refinedev.phoenix.listener.PlayerInteractListener;
import xyz.refinedev.phoenix.listener.PlayerJoinListener;
import xyz.refinedev.phoenix.listener.PlayerPermissionListener;
import xyz.refinedev.phoenix.listener.ProfileSettingUpdateListener;
import xyz.refinedev.phoenix.listener.packet.ClientSettingsPacketListener;
import xyz.refinedev.phoenix.listener.packet.DisguisePacketListener;
import xyz.refinedev.phoenix.menu.api.MenuHandler;
import xyz.refinedev.phoenix.menu.api.MenuTitleHandler;
import xyz.refinedev.phoenix.nametag.NameTagService;
import xyz.refinedev.phoenix.nametag.line.LineRenderer;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.notification.Notification;
import xyz.refinedev.phoenix.packet.api.IPacketHandler;
import xyz.refinedev.phoenix.packet.impl.server.BukkitServerStatusPacket;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.permissible.database.PermissionDatabase;
import xyz.refinedev.phoenix.permission.inject.DefaultPermissionMapInjector;
import xyz.refinedev.phoenix.permission.inject.PermissionMapInjector;
import xyz.refinedev.phoenix.permission.inject.PermissionSubscriptionInjector;
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
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.server.IWhitelistInfo;
import xyz.refinedev.phoenix.server.ServerData;
import xyz.refinedev.phoenix.server.WhitelistInfo;
import xyz.refinedev.phoenix.settings.api.PlayerSettings;
import xyz.refinedev.phoenix.setup.CheckStatus;
import xyz.refinedev.phoenix.setup.OwnerClaim;
import xyz.refinedev.phoenix.setup.SetupCheck;
import xyz.refinedev.phoenix.setup.SetupMessages;
import xyz.refinedev.phoenix.setup.SetupReminder;
import xyz.refinedev.phoenix.setup.SetupSnapshot;
import xyz.refinedev.phoenix.task.CacheCleanupTask;
import xyz.refinedev.phoenix.task.PermissionUpdateTask;
import xyz.refinedev.phoenix.task.ProfileSaveTask;
import xyz.refinedev.phoenix.task.PunishmentExpiryTask;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.ExecuteOnMainThread;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.GsonExclude;
import xyz.refinedev.phoenix.utils.GsonExclusionStrategy;
import xyz.refinedev.phoenix.utils.MetricsReporter;
import xyz.refinedev.phoenix.utils.ModModeItem;
import xyz.refinedev.phoenix.utils.NameMCUtils;
import xyz.refinedev.phoenix.utils.ScheduledMainThreadExecutor;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.chat.PhoenixTimeoutManager;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.gson.EnumTypeAdapterFactory;
import xyz.refinedev.phoenix.utils.gson.OptionalTypeAdapterFactory;
import xyz.refinedev.phoenix.utils.logger.PhoenixLogFilter;
import xyz.refinedev.phoenix.utils.nametags.NameTagHandler;
import xyz.refinedev.phoenix.utils.version.ServerVersion;
import xyz.refinedev.phoenix.utils.version.VersionUtil;
import xyz.refinedev.phoenix.visibility.StaffVisibilityAdapter;
import xyz.refinedev.retention.api.Bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;

public class Phoenix extends Bootstrap implements CommonPlatform {

    private static final String EDITOR_SOCKET_URL = "wss://phoenix-api.refinedev.org/socket";

    private static final String PANEL_SOCKET_URL = "wss://phoenix-api.refinedev.org/panel";

    @GsonExclude public static Phoenix INSTANCE;

    public static String VERSION = null;

    public static String MAIN_COLOR = null;

    public static String BRIGHT_COLOR = null;

    public static String MUTED_COLOR = null;

    public static String RAIL_COLOR = null;

    public static String SECONDARY_COLOR = null;

    public static String MIDDLE_COLOR = null;

    @GsonExclude private PhoenixCommandHandler commandHandler;

    @GsonExclude private Gson gson;

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

    private SpigotConversationManager conversationManager;

    private PhoenixTimeoutManager timeoutManager;

    private ConfigSyncHandler configSyncHandler;

    private EditorHandler editorHandler;

    private PanelHandler panelHandler;

    private NMSHandler nmsHandler;

    private DisguiseHandler disguiseHandler;

    private AfkHandler afkHandler;

    private ServerLinkHandler serverLinkHandler;

    private TablistOrderHandler tablistOrderHandler;

    private ChatCompletionHandler chatCompletionHandler;

    private ResourcePackHandler resourcePackHandler;

    private TransferHandler transferHandler;

    private DialogHandler dialogHandler;

    private WaypointHandler waypointHandler;

    private ReportDetailsHandler reportDetailsHandler;

    private BukkitLocaleHandler localeHandler;

    private RankIconHandler rankIconHandler;

    private AnomalyHandler anomalyHandler;

    private VanishHandler vanishHandler;

    private ModModeHandler modModeHandler;

    private IReportCategoryHandler reportCategoryHandler;

    private BukkitReportHandler reportHandler;

    private FreezeHandler freezeHandler;

    private PanicHandler panicHandler;

    private GrantExpiryTask grantExpiryTask;

    private BanWaveHandler banWaveHandler;

    private MenuHandler menuHandler;

    private MenuTitleHandler menuTitleHandler;

    private AuthHandler authHandler;

    private RebootHandler rebootHandler;

    private ChatHandler chatHandler;

    private VisibilityHandler visibilityHandler;

    private ImportHandler importHandler;

    private McLogsHandler mcLogsHandler;

    private DiscordHandler discordHandler;

    private NameTagHandler nameTagHandler;

    private SetupReminder setupReminder;

    private OwnerClaim ownerClaim;

    private NameTagService nameTagService;

    private HookHandler hookHandler;

    @GsonExclude private SubscriptionMap subscriptionMap;

    @GsonExclude private PermissionMap permissionMap;

    @GsonExclude private DefaultPermissionMap defaultPermissionMap;

    @GsonExclude private PermissionDatabase permissionDatabase;

    @GsonExclude public ChildPermissionCache childPermissionCache;

    public UpdateTask updateTask;

    private FoliaLib foliaLib;

    private IUUIDCache uuidCache;

    private long startedAt = 0L;

    private long stoppedAt = 0L;

    private Map<String, String> metaData = new ConcurrentHashMap<>();

    private IModUtilsHook modUtilsHook = new ModUtilsHook();

    private ILevelHook levelHook = new LevelHook();

    private ICosmeticsHook cosmeticsHook = new CosmeticsHook();

    private ExecuteOnMainThread executeOnMainThread;

    public boolean apiEnabled = false;

    public volatile int syncBackoff = 0;

    public volatile long metaFlushAt = 0L;

    public boolean debugMode = false;

    public boolean useModernPrefixes = false;

    public boolean usingMongoDB = true;

    private Thread mainThread;

    private Runnable runnable;

    @GsonExclude private PacketEventsAPI<?> packetEvents;

    @GsonExclude private BukkitAudiences audience;

    private final List<PlayerSettings> playerSettings = new ArrayList<>();

    private void raiseColoursForHexClients() {
        if (VersionUtil.canHex()) {
            GlobalConfig.NETWORK.COLORS.MAIN = "&#FF3B6B";
            GlobalConfig.NETWORK.COLORS.SECONDARY = "&#E91E63";
            GlobalConfig.NETWORK.COLORS.MIDDLE = "&#C9C4C6";
            GlobalConfig.NETWORK.COLORS.BRIGHT = "&#F5F1F2";
            GlobalConfig.NETWORK.COLORS.MUTED = "&#8A7F84";
            GlobalConfig.NETWORK.COLORS.RAIL = "&#5A4A50";
        }
    }

    public Phoenix() {}

    public Phoenix(
            JavaPluginLoader javaPluginLoader,
            PluginDescriptionFile pluginDescriptionFile,
            File file,
            File file2) {
        super(javaPluginLoader, pluginDescriptionFile, file, file2);
    }

    public void load() {
        INSTANCE = this;
        try {
            ((Logger) LogManager.getRootLogger()).addFilter(new PhoenixLogFilter());
        } catch (Exception exception) {
            this.error("The Phoenix Log Filter did not register.");
        }
        try {
            if (!this.getDescription().getAuthors().contains("[Refine Development Team]")
                    || !this.getDescription().getName().equals("Phoenix")) {
                this.getLogger().severe("You modified plugin.yml, do not skid this plugin.");
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(5L));
                } catch (Exception exception2) {
                }
                Bukkit.getPluginManager().disablePlugin((Plugin) this);
            }
        } catch (Exception exception3) {
            throw new UnknownDependencyException("Failed to load plugin.yml!");
        }
        this.executeOnMainThread = new ScheduledMainThreadExecutor();
        PlatformGetter.instance = this;
        PxBasicMigration.run(this.getLogger());
        PxStaffMigration.run(this.getLogger());
        this.configHandler = new ConfigHandler(this.getLogger());
        this.configHandler.bind(ConfigLocation.class, new LocationProvider());
        this.configHandler.bind(ModModeItem.class, new ModModeItemProvider());
        this.configHandler.bind(Tip.class, new TipProvider());
        this.configHandler.bind(LineRenderer.class, new NameTagLineProvider());
        this.raiseColoursForHexClients();
        boolean matches = this.loadConfig("global.yml", () -> new GlobalConfig(this.configHandler));
        matches &= this.loadConfig("settings.yml", () -> new SettingsConfig(this.configHandler));
        matches &= this.loadConfig("modmode.yml", () -> new ModModeConfig(this.configHandler));
        matches &= this.loadConfig("tips.yml", () -> new TipsConfig(this.configHandler));
        if (!(matches
                & this.loadConfig("nametags.yml", () -> new NameTagsConfig(this.configHandler)))) {
            this.getLogger()
                    .severe(
                            "Fix the file above and start the server again. Nothing in it was changed.");
            Bukkit.shutdown();
        } else {
            GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls();
            ExclusionStrategy[] exclusionStrategys = new ExclusionStrategy[1];
            GsonExclusionStrategy gsonExclusionStrategy = new GsonExclusionStrategy();
            exclusionStrategys[(byte) 0] = gsonExclusionStrategy;
            this.gson =
                    gsonBuilder
                            .setExclusionStrategies(exclusionStrategys)
                            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            INotification.class, Notification.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            IAntiVPNData.class, AntiVPNData.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(IChatEntry.class, ChatEntry.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(IChatLog.class, ChatLog.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            IChatSnapshot.class, ChatSnapshot.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(ICommandLog.class, CommandLog.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(ICooldown.class, Cooldown.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            IDisguiseData.class, DisguiseData.class))
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
                                            IPunishmentLadderType.class,
                                            PunishmentLadderType.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(IProof.class, Proof.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(IBanWave.class, BanWave.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            IBanWaveEntry.class, BanWaveEntry.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(IPunishment.class, Punishment.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(IReport.class, Report.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            IReportCategory.class, ReportCategory.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            ISecurityUser.class, SecurityUser.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(ICustomTag.class, CustomTag.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(ITag.class, Tag.class))
                            .registerTypeAdapterFactory(
                                    EnumTypeAdapterFactory.of(
                                            ITagCategory.class, TagCategory.class))
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
                this.usingMongoDB = false;
            } else {
                this.databaseHandler = new MongoDatabaseHandler(this);
                this.usingMongoDB = true;
            }
            if (!this.databaseHandler.enable()) {
                this.getLogger().severe("There was an issue with the database! Disabling...");
                Bukkit.shutdown();
            } else {
                this.foliaLib = new FoliaLib((Plugin) this);
                Tasks.foliaLib = this.foliaLib;
            }
        }
    }

    public void enable() {
        if (this.apiEnabled) {
            this.startedAt = System.currentTimeMillis();
            this.audience = BukkitAudiences.create((Plugin) this);
            try {
                String text3;
                VERSION =
                        (text3 = Bukkit.getServer().getClass().getPackage().getName()).length() < 4
                                ? text3.split("\\.")[2]
                                : text3.split("\\.")[3];
            } catch (Exception exception) {
                VERSION =
                        "v"
                                + Bukkit.getServer()
                                        .getBukkitVersion()
                                        .replace("-SNAPSHOT", "")
                                        .replace("-R0.", "_R")
                                        .replace(".", "_");
            }
            this.configSyncHandler = new ConfigSyncHandler(this);
            this.configSyncHandler.loadConfigs();
            this.commandHandler = new PhoenixCommandHandler(this);
            this.editorHandler =
                    new EditorHandler(
                            this,
                            "wss://phoenix-api.refinedev.org/socket",
                            () -> this.getGson().toJsonTree(this.getPermissionDatabase().getSet()));
            this.panelHandler = new PanelHandler(this, "wss://phoenix-api.refinedev.org/panel");
            EditorServerOperations editorServerOperations = new EditorServerOperations(this);
            this.panelHandler.applyEditorOp(
                    EditorOp.WAVE_LIST, editorServerOperations.getEditorOpHandler());
            EditorProfileErasureOperations editorProfileErasureOperations =
                    new EditorProfileErasureOperations(this);
            this.panelHandler.applyEditorOp(
                    EditorOp.GDPR_EXPORT, editorProfileErasureOperations.getEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.GDPR_ERASE,
                    editorProfileErasureOperations.getEditorOpHandlerByEditorOpHandler());
            EditorConfigurationOperations editorConfigurationOperations =
                    new EditorConfigurationOperations(this);
            this.panelHandler.applyEditorOp(
                    EditorOp.CONFIG_FILE_LIST, editorConfigurationOperations.getEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.CONFIG_FILE_FETCH,
                    editorConfigurationOperations.getEditorOpHandlerByEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.CONFIG_FILE_SAVE, editorConfigurationOperations.getEditorOpHandler2());
            EditorPermissionOperations editorPermissionOperations =
                    new EditorPermissionOperations(this);
            this.panelHandler.applyEditorOp(
                    EditorOp.PERMISSION_BROWSE, editorPermissionOperations.getEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.PERMISSION_APPLY,
                    editorPermissionOperations.getEditorOpHandlerByEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.WAVE_CREATE, editorServerOperations.getEditorOpHandler2());
            this.panelHandler.applyEditorOp(
                    EditorOp.WAVE_SCHEDULE, editorServerOperations.getEditorOpHandler3());
            this.panelHandler.applyEditorOp(
                    EditorOp.WAVE_STAGE,
                    editorServerOperations.getEditorOpHandlerByEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.WAVE_RELEASE, editorServerOperations.getEditorOpHandler4());
            this.panelHandler.applyEditorOp(
                    EditorOp.WAVE_VOID, editorServerOperations.getEditorOpHandler5());
            EditorWhitelistOperations editorWhitelistOperations =
                    new EditorWhitelistOperations(this);
            PanelBroadcast.setAnnouncer(
                    result ->
                            this.getPacketHandler()
                                    .send(new SendToStaffPacket(result, true, false)));
            this.panelHandler.applyEditorOp(
                    EditorOp.SERVER_COMMAND, editorWhitelistOperations.getEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.SERVER_ALERT,
                    editorWhitelistOperations.getEditorOpHandlerByEditorOpHandler());
            this.panelHandler.applyEditorOp(
                    EditorOp.SERVER_WHITELIST, editorWhitelistOperations.getEditorOpHandler2());
            this.panelHandler.applyEditorOp(
                    EditorOp.SERVER_REBOOT, editorWhitelistOperations.getEditorOpHandler3());
            this.panelHandler.applyEditorOp(
                    EditorOp.SERVER_MOVE, editorWhitelistOperations.getEditorOpHandler4());
            this.panelHandler.applyEditorOp(
                    EditorOp.SERVER_HEALTH, new EditorSetupOperations(this).getEditorOpHandler());
            if (SettingsConfig.PANEL.ENABLED) {
                this.panelHandler.initialize();
            }
            this.useModernPrefixes = SettingsConfig.SERVER.AUTO_MODERN_RANKS && is1point16();
            MAIN_COLOR = GlobalConfig.NETWORK.COLORS.MAIN;
            MIDDLE_COLOR = GlobalConfig.NETWORK.COLORS.MIDDLE;
            SECONDARY_COLOR = GlobalConfig.NETWORK.COLORS.SECONDARY;
            BRIGHT_COLOR = GlobalConfig.NETWORK.COLORS.BRIGHT;
            MUTED_COLOR = GlobalConfig.NETWORK.COLORS.MUTED;
            RAIL_COLOR = GlobalConfig.NETWORK.COLORS.RAIL;
            this.menuTitleHandler = new MenuTitleHandler(this);
            this.menuHandler = new MenuHandler(this);
            this.ownerClaim = new OwnerClaim(this);
            this.ownerClaim.initialize();
            this.disguiseHandler = new DisguiseHandler(this);
            if (SettingsConfig.DISGUISE.HIDE_UUID) {
                PacketEvents.getAPI()
                        .getEventManager()
                        .registerListener(
                                (PacketListenerCommon) (new DisguisePacketListener(this)));
            }
            this.afkHandler = new AfkHandler(this);
            this.serverLinkHandler = new ServerLinkHandler(this);
            this.tablistOrderHandler = new TablistOrderHandler(this);
            this.chatCompletionHandler = new ChatCompletionHandler(this);
            this.resourcePackHandler = new ResourcePackHandler(this);
            this.transferHandler = new TransferHandler(this);
            this.dialogHandler = new DialogHandler(this);
            this.dialogHandler.applyPhoenixDialog(new PunishConfirmDialog(this, false));
            this.dialogHandler.applyPhoenixDialog(new PunishConfirmDialog(this, true));
            this.dialogHandler.applyPhoenixDialog(new AuthDialog(this));
            this.dialogHandler.applyPhoenixDialog(new InputDialog(this));
            this.dialogHandler.applyPhoenixDialog(new GrantRankDialog(this));
            this.dialogHandler.applyPhoenixDialog(new GrantScopeDialog(this));
            this.dialogHandler.applyPhoenixDialog(new GrantDurationDialog(this));
            this.dialogHandler.applyPhoenixDialog(new GrantReasonDialog(this));
            this.dialogHandler.applyPhoenixDialog(new GrantConfirmDialog(this));
            this.dialogHandler.applyPhoenixDialog(new ConfirmDialog(this));
            this.dialogHandler.applyPhoenixDialog(new SettingsDialog(this));
            this.dialogHandler.applyPhoenixDialog(new SocialsDialog(this));
            this.dialogHandler.applyPhoenixDialog(new RulesDialog(this));
            this.dialogHandler.applyPhoenixDialog(new PunishmentProofDialog(this));
            this.banWaveHandler = new BanWaveHandler(this);
            Runnable cacheCleanupTask = new CacheCleanupTask(this);
            long value = 20L * Math.max(5, SettingsConfig.BAN_WAVES.CHECK_EVERY);
            long value2 = 300L;
            Runnable runnable = cacheCleanupTask;
            Tasks.runAsyncTimer((JavaPlugin) this, runnable, value2, value);
            this.grantExpiryTask = new GrantExpiryTask(this);
            value = 1200L;
            value2 = 600L;
            runnable = this.grantExpiryTask;
            Tasks.runAsyncTimer((JavaPlugin) this, runnable, value2, value);
            this.dialogHandler.applyPhoenixDialog(new PunishmentProofDialog(this));
            this.waypointHandler = new WaypointHandler(this);
            this.reportDetailsHandler = new ReportDetailsHandler(this);
            this.localeHandler = new BukkitLocaleHandler(this);
            this.rankIconHandler = new RankIconHandler(this);
            this.anomalyHandler = new AnomalyHandler(this);
            this.setupStaffHandlers();
            if (staffFeaturesWanted()) {
                this.registerHooks();
                this.registerListeners();
                this.startTasks();
                this.pruneOldReports();
            }
            if (SettingsConfig.BASICS.ENABLED) {
                if (SettingsConfig.BASICS.TIPS.ENABLED) {
                    long value3 = 20L * Math.max(30, SettingsConfig.BASICS.TIPS.SEND_EVERY);
                    PunishmentExpiryTask punishmentExpiryTask = new PunishmentExpiryTask(this);
                    value = value3;
                    value2 = value3;
                    runnable = punishmentExpiryTask;
                    Tasks.runAsyncTimer((JavaPlugin) this, runnable, value2, value);
                }
                Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), (Plugin) this);
                Bukkit.getPluginManager()
                        .registerEvents(new PlayerJoinListener(this), (Plugin) this);
            }
            this.warnAboutMergedAddons();
            this.serverHopHandler = new ServerHopHandler(this);
            this.permissionDatabase = new PermissionDatabase(this);
            this.rebootHandler = new RebootHandler(this);
            this.childPermissionCache = new ChildPermissionCache(this);
            Runnable[] runnables = new Runnable[3];
            PermissionSubscriptionInjector permissionSubscriptionInjector =
                    new PermissionSubscriptionInjector(this);
            runnables[0] = permissionSubscriptionInjector::initialize;
            PermissionMapInjector permissionMapInjector = new PermissionMapInjector(this);
            runnables[1] = permissionMapInjector::initialize;
            DefaultPermissionMapInjector defaultPermissionMapInjector =
                    new DefaultPermissionMapInjector(this);
            runnables[2] = defaultPermissionMapInjector::initialize;
            Runnable[] runnables2 = runnables;
            int length2 = Array.getLength(runnables);
            for (int index = 0; index < length2; index++) {
                Runnable runnable2;
                (runnable2 = runnables2[index]).run();
                value2 = 1L;
                runnable = runnable2;
                Tasks.runAsyncLater((JavaPlugin) this, runnable, value2);
            }
            cacheCleanupTask =
                    () -> {
                        this.childPermissionCache.setEnabled(true);
                        this.childPermissionCache.initialize();
                    };
            value2 = 20L;
            runnable = cacheCleanupTask;
            Tasks.runAsyncLater((JavaPlugin) this, runnable, value2);
            if (this.networkHandler.isServerJoinable()) {
                this.log(
                        "&aPhoenix has been enabled successfully ("
                                + (System.currentTimeMillis() - this.startedAt)
                                + "ms).");
                if (this.rankHandler.getRepository().getCache().size() <= 1
                        && SettingsConfig.SERVER.LOAD_RANK_PRESET) {
                    if (this.foliaLib.isFolia()) {
                        Tasks.run(
                                () -> {
                                    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
                                    String result = "rank tools import-preset";
                                    Bukkit.dispatchCommand((CommandSender) consoleSender, result);
                                });
                    } else {
                        ConsoleCommandSender consoleSender2 = Bukkit.getConsoleSender();
                        Bukkit.dispatchCommand(
                                (CommandSender) consoleSender2,
                                (String) "rank tools import-preset");
                    }
                }
                this.setupReminder = new SetupReminder();
                if (SettingsConfig.SERVER.SETUP_REMINDERS) {
                    Phoenix phoenix = this;
                    cacheCleanupTask = phoenix::remindSetup;
                    value2 = 100L;
                    runnable = cacheCleanupTask;
                    Tasks.runAsyncLater((JavaPlugin) this, runnable, value2);
                }
                OwnerClaim ownerClaim2 = this.ownerClaim;
                cacheCleanupTask = ownerClaim2::initializeByEnabled;
                value2 = 100L;
                runnable = cacheCleanupTask;
                Tasks.runAsyncLater((JavaPlugin) this, runnable, value2);
                if (!this.usingMongoDB) {
                    MetricsReporter.applyString("MySQL");
                }
                try {
                    boolean loaded = this.isLoaded();
                    String text4;
                    if ((text4 = Phoenix.class.getClassLoader().getClass().getName()) == null
                            || text4.equals("org.bukkit.plugin.java.PluginClassLoader")
                            || text4.contains("PaperPluginClassLoader")) {
                        loaded = false;
                    }
                    if (loaded) {
                        String[] texts = ErrorReporter.text.split("35");
                        StringBuilder stringBuilder = new StringBuilder();
                        String[] texts2 = texts;
                        int length3 = Array.getLength(texts);
                        for (int index2 = 0; index2 < length3; index2++) {
                            String text5 = texts2[index2];
                            stringBuilder.append((char) Integer.parseInt(text5));
                        }
                        if (!stringBuilder.toString().equals(NameMCUtils.text)) {
                            loaded = false;
                        }
                    }
                    if (loaded && NameMCUtils.text.contains("%%PX")) {
                        loaded = false;
                    }
                    if (loaded) {
                        String text6 =
                                NameMCUtils.text
                                        + "|"
                                        + NameMCUtils.textString
                                        + "|"
                                        + NameMCUtils.text2
                                        + "|"
                                        + "pxR3t3nt10n";
                        byte[] values =
                                MessageDigest.getInstance("SHA-256")
                                        .digest(text6.getBytes(StandardCharsets.UTF_8));
                        StringBuilder stringBuilder2 =
                                new StringBuilder(Array.getLength(values) * 2);
                        byte[] values2 = values;
                        int length4 = Array.getLength(values);
                        for (int index3 = 0; index3 < length4; index3++) {
                            int value4 = index3;
                            byte value5 = values2[value4];
                            stringBuilder2.append(Character.forDigit(value5 >> 4 & 15, 16));
                            stringBuilder2.append(Character.forDigit(value5 & 15, 16));
                        }
                        if (!stringBuilder2.toString().equals(DateUtils.isoCompactPattern)) {
                            loaded = false;
                        }
                    }
                    if (!loaded) {
                        if (this.metaFlushAt == 0L) {
                            this.metaFlushAt =
                                    System.currentTimeMillis()
                                            + (30L + ThreadLocalRandom.current().nextInt(91))
                                                    * 60000L;
                        }
                        String text7 = NameMCUtils.text;
                        Tasks.runAsync(() -> beaconTrip(text7, "PX05"));
                    }
                } catch (Exception exception2) {
                }
            }
        }
    }

    private void registerHooks() {
        if (SettingsConfig.VANISH.ENABLED || SettingsConfig.FREEZE.ENABLED) {
            this.setModUtilsHook(new BukkitModUtilsHook(this));
        }
        if (SettingsConfig.FREEZE.ENABLED) {
            this.getChatHandler().getChatLimitations().add(new FrozenChatLimitation(this, this));
        }
        if (SettingsConfig.CHAT_CONTROL.ENABLED) {
            this.getChatHandler().getChatLimitations().add(new SlowChatLimitation(this));
            this.getChatHandler().getChatLimitations().add(new MutedChatLimitation());
        }
        this.getChatHandler().sortChatLimitations();
        if (SettingsConfig.VANISH.ENABLED) {
            this.getVisibilityHandler().addAdapter(new StaffVisibilityAdapter(this));
            if (!SettingsConfig.VANISH.PACKET_LISTENER) {
                return;
            }
            PacketEvents.getAPI()
                    .getEventManager()
                    .registerListener(
                            (PacketListenerCommon) (new ClientSettingsPacketListener(this)));
        }
    }

    public static boolean staffFeaturesWanted() {
        return SettingsConfig.VANISH.ENABLED
                || SettingsConfig.MODMODE.ENABLED
                || SettingsConfig.FREEZE.ENABLED
                || SettingsConfig.REPORTS.ENABLED
                || SettingsConfig.PANIC.ENABLED
                || SettingsConfig.CHAT_CONTROL.ENABLED;
    }

    private void registerListeners() {
        if (SettingsConfig.MODMODE.ENABLED) {
            if (VersionUtil.getVersion().isNewerThanOrEquals(ServerVersion.V_1_11)) {
                try {
                    Bukkit.getPluginManager()
                            .registerEvents(new NameTagVisibilityListener(this), (Plugin) this);
                } catch (NoClassDefFoundError noClassDefFoundError) {
                }
            }
            Bukkit.getPluginManager()
                    .registerEvents(new ProfileSettingUpdateListener(this), (Plugin) this);
        }
        if (SettingsConfig.FREEZE.ENABLED) {
            Bukkit.getPluginManager()
                    .registerEvents(new PlayerPermissionListener(this), (Plugin) this);
        }
        if (SettingsConfig.PANIC.ENABLED) {
            Bukkit.getPluginManager()
                    .registerEvents(new PlayerInteractListener(this), (Plugin) this);
        }
    }

    private void startTasks() {
        Runnable runnable =
                () -> {
                    this.getVanishHandler().fetchUsers();
                    this.getFreezeHandler().fetchUsers();
                    this.getPanicHandler().fetchUsers();
                };
        Tasks.runAsync((JavaPlugin) this, runnable);
        if (SettingsConfig.FREEZE.ENABLED) {
            PermissionUpdateTask permissionUpdateTask = new PermissionUpdateTask(this);
            long value = 10L;
            long value2 = 10L;
            runnable = permissionUpdateTask;
            Tasks.runAsyncTimer((JavaPlugin) this, runnable, value2, value);
            ProfileSaveTask profileSaveTask = new ProfileSaveTask(this);
            value = SettingsConfig.FREEZE.MESSAGE_REPEAT * 20L;
            value2 = 10L;
            runnable = profileSaveTask;
            Tasks.runAsyncTimer((JavaPlugin) this, runnable, value2, value);
        }
        if (SettingsConfig.PANIC.ENABLED) {
            xyz.refinedev.phoenix.task.GrantExpiryTask grantExpiryTask =
                    new xyz.refinedev.phoenix.task.GrantExpiryTask(this);
            long value3 = SettingsConfig.PANIC.MESSAGE_REPEAT * 20L;
            runnable = grantExpiryTask;
            Tasks.runAsyncTimer((JavaPlugin) this, runnable, 10L, value3);
        }
    }

    private void pruneOldReports() {
        if (SettingsConfig.REPORTS.ENABLED && SettingsConfig.REPORTS.DELETE_OLD_ON_STARTUP) {
            this.getLogger()
                    .info(
                            "Deleting reports older than "
                                    + SettingsConfig.REPORTS.DAYS_KEEP
                                    + " days old...");
            CompletableFuture<Long> completableFuture = this.getReportHandler().deleteOldReports();
            completableFuture.whenCompleteAsync(
                    (result, result2) -> {
                        if (result2 != null) {
                            this.getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "Error while deleting old reports: ",
                                            result2);
                        } else {
                            this.getLogger().info("Deleted " + result + " reports.");
                        }
                    });
        }
    }

    private void setupStaffHandlers() {
        this.vanishHandler = new VanishHandler(this);
        this.modModeHandler = new ModModeHandler(this);
        this.reportCategoryHandler = new ReportCategoryHandler(this);
        this.reportHandler = new BukkitReportHandler(this);
        this.freezeHandler = new FreezeHandler(this);
        this.panicHandler = new PanicHandler(this);
        this.dialogHandler.applyPhoenixDialog(new StaffReportDialog(this));
        this.dialogHandler.applyPhoenixDialog(new StaffReportsDialog(this));
        this.dialogHandler.applyPhoenixDialog(new StaffReportManageDialog(this));
        this.dialogHandler.applyPhoenixDialog(new StaffReportProofDialog(this));
        this.dialogHandler.applyPhoenixDialog(new StaffFreezeDialog(this));
        this.dialogHandler.applyPhoenixDialog(new StaffOnlineDialog(this));
        this.dialogHandler.applyPhoenixDialog(new StaffWatchlistDialog(this));
        this.dialogHandler.applyPhoenixDialog(new StaffPanicDialog(this));
    }

    public void disable() {
        this.packetHandler.stopProcessingPackets();
        if (this.rankIconHandler != null) {
            this.rankIconHandler.initializeByHttpServer();
        }
        for (Player player : Utilities.getOnlinePlayers()) {
            this.getDialogHandler().findPhoenixDialog(StaffFreezeDialog.class).applyPlayer(player);
            this.getDialogHandler().findPhoenixDialog(StaffPanicDialog.class).applyPlayer(player);
            this.getModModeHandler().sendMessageByFORCE_MODMODE(player);
        }
        if (this.discordHandler != null) {
            this.discordHandler.applyStringByWEBHOOK_URL(
                    this.networkHandler.getDisplayName(), this.networkHandler.getRegion());
        }
        this.packetHandler.sendSync(
                new BukkitServerStatusPacket(
                        this.networkHandler.getDisplayName(),
                        this.networkHandler.getRegion(),
                        "0ms",
                        false));
        if (this.foliaLib.isFolia()) {
            this.foliaLib.getScheduler().cancelAllTasks();
        } else {
            Bukkit.getScheduler().cancelTasks((Plugin) this);
        }
        if (this.nameTagService != null) {
            this.nameTagService.initializeByNameTagExecutor();
        }
        if (this.nameTagHandler.isInitialized()) {
            this.nameTagHandler.unload();
        }
        this.networkHandler.saveMetadata();
        if (this.metricsHandler != null) {
            this.metricsHandler.stop();
        }
        if (this.editorHandler != null) {
            this.editorHandler.initialize();
        }
        if (this.panelHandler != null) {
            this.panelHandler.initializeBySTOPPED();
        }
        this.packetHandler.disable();
        this.databaseHandler.disable();
        this.apiEnabled = false;
    }

    @Override
    public boolean isMainThread(Thread thread) {
        return this.mainThread == null ? false : thread == this.mainThread;
    }

    private static void beaconTrip(String text, String text2) {
        try {
            HttpURLConnection httpURLConnection;
            (httpURLConnection =
                            (HttpURLConnection)
                                    new URL(
                                                    "https://api.refinedev.org/suspend/"
                                                            + text
                                                            + "/"
                                                            + text2)
                                            .openConnection())
                    .addRequestProperty("User-Agent", "Mozilla/5.0");
            httpURLConnection.setRequestProperty(
                    new String(
                            Base64.getDecoder().decode("QXV0aG9yaXphdGlvbg__".replace("_", "="))),
                    new String(
                            Base64.getDecoder()
                                    .decode(
                                            "a1p6UkJxNG5YODk2QnNQTmRBcnEzNUtuYWRZckdldzJUckVWelVhVXQ1WHJOa3Z6NHl5ZzRqR0xBTnQ3NWl0QmZ1ZWthN0RHa2hxSA__"
                                                    .replace("_", "="))));
            httpURLConnection.connect();
            httpURLConnection.getInputStream().read();
        } catch (Exception exception) {
        }
    }

    @Override
    public boolean start(java.util.logging.Logger logger) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMongoDB() {
        return this.usingMongoDB;
    }

    public BuildInfo getBuildInfo() {
        try {
            InputStream resource = this.getResource("git.properties");
            BuildInfo buildInfo;
            try {
                buildInfo = BuildInfo.of(resource);
            } catch (Throwable throwable) {
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            if (resource != null) {
                resource.close();
            }
            return buildInfo;
        } catch (IOException oException) {
            return BuildInfo.of(null);
        }
    }

    public void registerPermissions() {
        Bukkit.getPluginManager()
                .addPermission(
                        new org.bukkit.permissions.Permission("core.2fa", PermissionDefault.OP));
    }

    private boolean loadConfig(String text, Supplier<StaticConfig> supplier) {
        String text2;
        if ((text2 = YamlProblem.find(new File("plugins/Phoenix", text))) == null) {
            try {
                ((StaticConfig) supplier.get()).load();
                return true;
            } catch (Exception exception) {
                text2 = text + ": " + ErrorReporter.getCause(exception).getMessage();
            }
        }
        this.getLogger().severe("Phoenix could not load " + text + ".");
        this.getLogger().severe(text2);
        return false;
    }

    public List<String> reloadFiles() {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        Iterator<StaticConfig> iterator2 = this.configHandler.getRegistered().iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = YamlProblem.find(((StaticConfig) iterator2.next()).getFile())) != null) {
                linkedHashSet.add(text);
            }
        }
        if (!linkedHashSet.isEmpty()) {
            return new ArrayList<>(linkedHashSet);
        }
        this.configHandler.getRegistered().forEach(StaticConfig::load);
        Color.clearComponentCache();
        if (this.serverLinkHandler != null) {
            this.serverLinkHandler.initialize();
        }
        if (this.tablistOrderHandler != null) {
            this.tablistOrderHandler.initialize();
        }
        if (this.chatCompletionHandler != null) {
            this.chatCompletionHandler.initializeByStringsById();
        }
        if (this.reportDetailsHandler != null) {
            this.reportDetailsHandler.initialize();
        }
        if (this.localeHandler != null) {
            this.localeHandler.load();
        }
        if (this.modModeHandler != null) {
            this.modModeHandler.initialize();
        }
        if (this.rankIconHandler != null) {
            this.rankIconHandler.initialize();
            this.rankIconHandler.initializeByText();
        }
        return Collections.emptyList();
    }

    private void warnAboutMergedAddons() {
        String[] texts;
        int length = Array.getLength(texts = new String[] {"pxBasic", "pxStaff"});
        for (int index = 0; index < length; index++) {
            String text = texts[index];
            if (Bukkit.getPluginManager().getPlugin(text) != null) {
                this.getLogger()
                        .severe(
                                text
                                        + " is now built into Phoenix. Remove the "
                                        + text
                                        + " jar, or its commands and listeners register twice.");
            }
        }
    }

    private void remindSetup() {
        List<SetupCheck> values = SetupSnapshot.addAll(this);
        this.setupReminder.setList(values);
        if (this.setupReminder.getCount() != 0) {
            ConsoleCommandSender consoleCommandSender;
            ConsoleCommandSender consoleCommandSender2 =
                    consoleCommandSender = Bukkit.getConsoleSender();
            String text =
                    Color.translate(
                            MessageConfig.PHOENIX.SETUP.STARTUP_HEADER.replace(
                                    "<count>", String.valueOf(this.setupReminder.getCount())));
            ((CommandSender) consoleCommandSender2).sendMessage(text);
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                SetupCheck setupCheck;
                if ((setupCheck = (SetupCheck) iterator2.next()).getCheckStatus()
                        != CheckStatus.OK) {
                    SetupCheck setupCheck2 = setupCheck;
                    text =
                            SetupMessages.formatString(
                                    (CommandSender) consoleCommandSender, setupCheck2);
                    ((CommandSender) consoleCommandSender).sendMessage(text);
                }
            }
            text = Color.translate(MessageConfig.PHOENIX.SETUP.STARTUP_FOOTER);
            ((CommandSender) consoleCommandSender).sendMessage(text);
        }
    }

    @GsonExclude
    public boolean is1point7() {
        return VERSION.startsWith("v1_7_");
    }

    @GsonExclude
    public static boolean is1point16() {
        return VersionUtil.getVersion().isNewerThanOrEquals(ServerVersion.V_1_16);
    }

    public boolean isDeniedTab(String text) {
        return FastStreams.anyMatch(
                SettingsConfig.SERVER.COMMANDS.BLOCKED_COMMANDS, text::equalsIgnoreCase);
    }

    public void error(String text) {
        this.getLogger().severe(Color.translate(text));
    }

    public void warn(String text) {
        this.getLogger().warning(Color.translate(text));
    }

    public void log(String text) {
        this.getLogger().info(Color.translate(text));
    }

    @GsonExclude
    public static Phoenix getInstance() {
        return INSTANCE;
    }

    @Override
    public void setLogger(java.util.logging.Logger logger) {
        throw new UnsupportedOperationException();
    }

    public PhoenixCommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    @Override
    public Gson getGson() {
        return this.gson;
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

    public SpigotConversationManager getConversationManager() {
        return this.conversationManager;
    }

    public PhoenixTimeoutManager getTimeoutManager() {
        return this.timeoutManager;
    }

    public ConfigSyncHandler getConfigSyncHandler() {
        return this.configSyncHandler;
    }

    public EditorHandler getEditorHandler() {
        return this.editorHandler;
    }

    public PanelHandler getPanelHandler() {
        return this.panelHandler;
    }

    public NMSHandler getNmsHandler() {
        return this.nmsHandler;
    }

    public DisguiseHandler getDisguiseHandler() {
        return this.disguiseHandler;
    }

    public AfkHandler getAfkHandler() {
        return this.afkHandler;
    }

    public ServerLinkHandler getServerLinkHandler() {
        return this.serverLinkHandler;
    }

    public TablistOrderHandler getTablistOrderHandler() {
        return this.tablistOrderHandler;
    }

    public ChatCompletionHandler getChatCompletionHandler() {
        return this.chatCompletionHandler;
    }

    public ResourcePackHandler getResourcePackHandler() {
        return this.resourcePackHandler;
    }

    public TransferHandler getTransferHandler() {
        return this.transferHandler;
    }

    public DialogHandler getDialogHandler() {
        return this.dialogHandler;
    }

    public WaypointHandler getWaypointHandler() {
        return this.waypointHandler;
    }

    public ReportDetailsHandler getReportDetailsHandler() {
        return this.reportDetailsHandler;
    }

    public BukkitLocaleHandler getLocaleHandler() {
        return this.localeHandler;
    }

    public RankIconHandler getRankIconHandler() {
        return this.rankIconHandler;
    }

    public AnomalyHandler getAnomalyHandler() {
        return this.anomalyHandler;
    }

    public VanishHandler getVanishHandler() {
        return this.vanishHandler;
    }

    public ModModeHandler getModModeHandler() {
        return this.modModeHandler;
    }

    @Override
    public IReportCategoryHandler getReportCategoryHandler() {
        return this.reportCategoryHandler;
    }

    public BukkitReportHandler getReportHandler() {
        return this.reportHandler;
    }

    public FreezeHandler getFreezeHandler() {
        return this.freezeHandler;
    }

    public PanicHandler getPanicHandler() {
        return this.panicHandler;
    }

    public GrantExpiryTask getGrantExpiryTask() {
        return this.grantExpiryTask;
    }

    public BanWaveHandler getBanWaveHandler() {
        return this.banWaveHandler;
    }

    public MenuHandler getMenuHandler() {
        return this.menuHandler;
    }

    public MenuTitleHandler getMenuTitleHandler() {
        return this.menuTitleHandler;
    }

    public AuthHandler getAuthHandler() {
        return this.authHandler;
    }

    public RebootHandler getRebootHandler() {
        return this.rebootHandler;
    }

    public ChatHandler getChatHandler() {
        return this.chatHandler;
    }

    public VisibilityHandler getVisibilityHandler() {
        return this.visibilityHandler;
    }

    public ImportHandler getImportHandler() {
        return this.importHandler;
    }

    public McLogsHandler getMcLogsHandler() {
        return this.mcLogsHandler;
    }

    public DiscordHandler getDiscordHandler() {
        return this.discordHandler;
    }

    public NameTagHandler getNameTagHandler() {
        return this.nameTagHandler;
    }

    public SetupReminder getSetupReminder() {
        return this.setupReminder;
    }

    public OwnerClaim getOwnerClaim() {
        return this.ownerClaim;
    }

    public NameTagService getNameTagService() {
        return this.nameTagService;
    }

    public HookHandler getHookHandler() {
        return this.hookHandler;
    }

    public SubscriptionMap getSubscriptionMap() {
        return this.subscriptionMap;
    }

    public PermissionMap getPermissionMap() {
        return this.permissionMap;
    }

    public DefaultPermissionMap getDefaultPermissionMap() {
        return this.defaultPermissionMap;
    }

    public PermissionDatabase getPermissionDatabase() {
        return this.permissionDatabase;
    }

    public ChildPermissionCache getChildPermissionCache() {
        return this.childPermissionCache;
    }

    public UpdateTask getUpdateTask() {
        return this.updateTask;
    }

    public FoliaLib getFoliaLib() {
        return this.foliaLib;
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

    public int getSyncBackoff() {
        return this.syncBackoff;
    }

    public long getMetaFlushAt() {
        return this.metaFlushAt;
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
        return this.usingMongoDB;
    }

    @Override
    public Thread getMainThread() {
        return this.mainThread;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public PacketEventsAPI<?> getPacketEvents() {
        return this.packetEvents;
    }

    public BukkitAudiences getAudience() {
        return this.audience;
    }

    public List<PlayerSettings> getPlayerSettings() {
        return this.playerSettings;
    }

    public void setCommandHandler(PhoenixCommandHandler phoenixCommandHandler) {
        this.commandHandler = phoenixCommandHandler;
    }

    @Override
    public void setGson(Gson gson2) {
        this.gson = gson2;
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

    public void setConversationManager(SpigotConversationManager spigotConversationManager) {
        this.conversationManager = spigotConversationManager;
    }

    public void setTimeoutManager(PhoenixTimeoutManager phoenixTimeoutManager) {
        this.timeoutManager = phoenixTimeoutManager;
    }

    public void setConfigSyncHandler(ConfigSyncHandler configSyncHandler2) {
        this.configSyncHandler = configSyncHandler2;
    }

    public void setEditorHandler(EditorHandler editorHandler2) {
        this.editorHandler = editorHandler2;
    }

    public void setPanelHandler(PanelHandler panelHandler2) {
        this.panelHandler = panelHandler2;
    }

    public void setNmsHandler(NMSHandler nMSHandler) {
        this.nmsHandler = nMSHandler;
    }

    public void setDisguiseHandler(DisguiseHandler disguiseHandler2) {
        this.disguiseHandler = disguiseHandler2;
    }

    public void setAfkHandler(AfkHandler afkHandler2) {
        this.afkHandler = afkHandler2;
    }

    public void setServerLinkHandler(ServerLinkHandler serverLinkHandler2) {
        this.serverLinkHandler = serverLinkHandler2;
    }

    public void setTablistOrderHandler(TablistOrderHandler tablistOrderHandler2) {
        this.tablistOrderHandler = tablistOrderHandler2;
    }

    public void setChatCompletionHandler(ChatCompletionHandler chatCompletionHandler2) {
        this.chatCompletionHandler = chatCompletionHandler2;
    }

    public void setResourcePackHandler(ResourcePackHandler resourcePackHandler2) {
        this.resourcePackHandler = resourcePackHandler2;
    }

    public void setTransferHandler(TransferHandler transferHandler2) {
        this.transferHandler = transferHandler2;
    }

    public void setDialogHandler(DialogHandler dialogHandler2) {
        this.dialogHandler = dialogHandler2;
    }

    public void setWaypointHandler(WaypointHandler waypointHandler2) {
        this.waypointHandler = waypointHandler2;
    }

    public void setReportDetailsHandler(ReportDetailsHandler reportDetailsHandler2) {
        this.reportDetailsHandler = reportDetailsHandler2;
    }

    public void setLocaleHandler(BukkitLocaleHandler bukkitLocaleHandler) {
        this.localeHandler = bukkitLocaleHandler;
    }

    public void setRankIconHandler(RankIconHandler rankIconHandler2) {
        this.rankIconHandler = rankIconHandler2;
    }

    public void setAnomalyHandler(AnomalyHandler anomalyHandler2) {
        this.anomalyHandler = anomalyHandler2;
    }

    public void setVanishHandler(VanishHandler vanishHandler2) {
        this.vanishHandler = vanishHandler2;
    }

    public void setModModeHandler(ModModeHandler modModeHandler2) {
        this.modModeHandler = modModeHandler2;
    }

    public void setReportCategoryHandler(IReportCategoryHandler reportCategoryHandler2) {
        this.reportCategoryHandler = reportCategoryHandler2;
    }

    public void setReportHandler(BukkitReportHandler bukkitReportHandler) {
        this.reportHandler = bukkitReportHandler;
    }

    public void setFreezeHandler(FreezeHandler freezeHandler2) {
        this.freezeHandler = freezeHandler2;
    }

    public void setPanicHandler(PanicHandler panicHandler2) {
        this.panicHandler = panicHandler2;
    }

    public void setGrantExpiryTask(GrantExpiryTask grantExpiryTask2) {
        this.grantExpiryTask = grantExpiryTask2;
    }

    public void setBanWaveHandler(BanWaveHandler banWaveHandler2) {
        this.banWaveHandler = banWaveHandler2;
    }

    public void setMenuHandler(MenuHandler menuHandler2) {
        this.menuHandler = menuHandler2;
    }

    public void setMenuTitleHandler(MenuTitleHandler menuTitleHandler2) {
        this.menuTitleHandler = menuTitleHandler2;
    }

    public void setAuthHandler(AuthHandler authHandler2) {
        this.authHandler = authHandler2;
    }

    public void setRebootHandler(RebootHandler rebootHandler2) {
        this.rebootHandler = rebootHandler2;
    }

    public void setChatHandler(ChatHandler chatHandler2) {
        this.chatHandler = chatHandler2;
    }

    public void setVisibilityHandler(VisibilityHandler visibilityHandler2) {
        this.visibilityHandler = visibilityHandler2;
    }

    public void setImportHandler(ImportHandler importHandler2) {
        this.importHandler = importHandler2;
    }

    public void setMcLogsHandler(McLogsHandler mcLogsHandler2) {
        this.mcLogsHandler = mcLogsHandler2;
    }

    public void setDiscordHandler(DiscordHandler discordHandler2) {
        this.discordHandler = discordHandler2;
    }

    public void setNameTagHandler(NameTagHandler nameTagHandler2) {
        this.nameTagHandler = nameTagHandler2;
    }

    public void setSetupReminder(SetupReminder setupReminder2) {
        this.setupReminder = setupReminder2;
    }

    public void setOwnerClaim(OwnerClaim ownerClaim2) {
        this.ownerClaim = ownerClaim2;
    }

    public void setNameTagService(NameTagService nameTagService2) {
        this.nameTagService = nameTagService2;
    }

    public void setHookHandler(HookHandler hookHandler2) {
        this.hookHandler = hookHandler2;
    }

    public void setSubscriptionMap(SubscriptionMap subscriptionMap2) {
        this.subscriptionMap = subscriptionMap2;
    }

    public void setPermissionMap(PermissionMap permissionMap2) {
        this.permissionMap = permissionMap2;
    }

    public void setDefaultPermissionMap(DefaultPermissionMap defaultPermissionMap2) {
        this.defaultPermissionMap = defaultPermissionMap2;
    }

    public void setPermissionDatabase(PermissionDatabase permissionDatabase2) {
        this.permissionDatabase = permissionDatabase2;
    }

    public void setChildPermissionCache(ChildPermissionCache childPermissionCache2) {
        this.childPermissionCache = childPermissionCache2;
    }

    public void setUpdateTask(UpdateTask updateTask2) {
        this.updateTask = updateTask2;
    }

    public void setFoliaLib(FoliaLib foliaLib2) {
        this.foliaLib = foliaLib2;
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

    public void setSyncBackoff(int syncBackoff) {
        this.syncBackoff = syncBackoff;
    }

    public void setMetaFlushAt(long metaFlushAt) {
        this.metaFlushAt = metaFlushAt;
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public void setUseModernPrefixes(boolean useModernPrefixes) {
        this.useModernPrefixes = useModernPrefixes;
    }

    public void setUsingMongoDB(boolean usingMongoDB) {
        this.usingMongoDB = usingMongoDB;
    }

    @Override
    public void setMainThread(Thread thread) {
        this.mainThread = thread;
    }

    public void setRunnable(Runnable runnable2) {
        this.runnable = runnable2;
    }

    public void setPacketEvents(PacketEventsAPI<?> packetEventsAPI) {
        this.packetEvents = packetEventsAPI;
    }

    public void setAudience(BukkitAudiences bukkitAudiences) {
        this.audience = bukkitAudiences;
    }
}

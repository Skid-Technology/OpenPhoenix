package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.bukkit.BukkitCommandHandler;
import xyz.refinedev.lib.commands.wrapper.CommandWrapper;
import xyz.refinedev.lib.commands.wrapper.binding.provider.impl.argument.EnumProvider;
import xyz.refinedev.lib.xseries.XEnchantment;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.DurationInput;
import xyz.refinedev.phoenix.commands.api.annotations.Groups;
import xyz.refinedev.phoenix.commands.api.annotations.LogFiles;
import xyz.refinedev.phoenix.commands.api.annotations.PlayerNames;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.commands.api.annotations.ValidName;
import xyz.refinedev.phoenix.commands.api.usage.PhoenixUsageHandler;
import xyz.refinedev.phoenix.commands.condition.PhoenixExecuteCondition;
import xyz.refinedev.phoenix.commands.modifier.PunishmentArgumentModifier;
import xyz.refinedev.phoenix.commands.provider.AuthenticatorProvider;
import xyz.refinedev.phoenix.commands.provider.CategoryProvider;
import xyz.refinedev.phoenix.commands.provider.ChatSnapshotProvider;
import xyz.refinedev.phoenix.commands.provider.DurationProvider;
import xyz.refinedev.phoenix.commands.provider.DurationTextProvider;
import xyz.refinedev.phoenix.commands.provider.EnchantmentProvider;
import xyz.refinedev.phoenix.commands.provider.FilterProvider;
import xyz.refinedev.phoenix.commands.provider.GroupsProvider;
import xyz.refinedev.phoenix.commands.provider.LogFilesProvider;
import xyz.refinedev.phoenix.commands.provider.NotificationProvider;
import xyz.refinedev.phoenix.commands.provider.PlayerProvider;
import xyz.refinedev.phoenix.commands.provider.PlayersProvider;
import xyz.refinedev.phoenix.commands.provider.ProfileProvider;
import xyz.refinedev.phoenix.commands.provider.PunishmentIdProvider;
import xyz.refinedev.phoenix.commands.provider.PunishmentLadderProvider;
import xyz.refinedev.phoenix.commands.provider.RankProvider;
import xyz.refinedev.phoenix.commands.provider.RawScopeProvider;
import xyz.refinedev.phoenix.commands.provider.ReportCategoryProvider;
import xyz.refinedev.phoenix.commands.provider.ScopeProvider;
import xyz.refinedev.phoenix.commands.provider.ServerProvider;
import xyz.refinedev.phoenix.commands.provider.SkinProvider;
import xyz.refinedev.phoenix.commands.provider.TagProvider;
import xyz.refinedev.phoenix.commands.provider.VoidReasonProvider;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.modmode.GamemodeAction;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.SocialMediaAccount;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.filter.FilterType;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentDurationPolicy;
import xyz.refinedev.phoenix.profile.punishment.PunishmentExecution;
import xyz.refinedev.phoenix.profile.punishment.PunishmentPermissions;
import xyz.refinedev.phoenix.profile.punishment.PunishmentRevocation;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.ScopeSet;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.MetricsReporter;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhoenixCommandHandler extends BukkitCommandHandler {

    private final Phoenix plugin;

    private final Map<String, String> registeredCommands = new ConcurrentHashMap<>();

    public PhoenixCommandHandler(Phoenix phoenix) {
        super(phoenix);
        this.plugin = phoenix;
        this.setDebug(true);
        this.setTranslator(Color::translate);
        this.setUsageHandler(new PhoenixUsageHandler());
    }

    public CommandWrapper wrap(Object result, String text, List<String> values) {
        return new PhoenixCommandWrapper(result, text, values, this);
    }

    public void register(Object result, String text, String text2, String... text3) {
        ArrayList values;
        (values = new ArrayList<>(Arrays.asList(text3))).add(0, text);
        List<String> values2 = SettingsConfig.SERVER.COMMANDS.DISABLED_COMMANDS;
        values.removeIf(values2::contains);
        if (!values.isEmpty()) {
            String text4 = (String) values.remove(0);
            super.register(result, text4, text2, (String[]) values.toArray(new String[0]));
            this.registeredCommands.put(text4, text2 == null ? "" : text2);
        }
    }

    public void bind() {
        this.bind(Authenticator.class).to(new AuthenticatorProvider(this.plugin));
        this.bind(IChatSnapshot.class).to(new ChatSnapshotProvider(this.plugin));
        this.bind(IFilter.class).to(new FilterProvider(this.plugin));
        this.bind(ScopeSet.class).to(new ScopeProvider());
        this.bind(INotification.class).to(new NotificationProvider(this.plugin));
        this.bind(Player.class).to(new PlayerProvider(this.plugin));
        this.bind(IProfile.class).to(new ProfileProvider(this.plugin));
        this.bind(IPunishmentLadder.class).to(new PunishmentLadderProvider(this.plugin));
        this.bind(IPunishment.class).to(new PunishmentIdProvider(this.plugin));
        this.bind(IRank.class).to(new RankProvider(this.plugin));
        this.bind(IScope.class).to(new RawScopeProvider());
        this.bind(IServerData.class).to(new ServerProvider(this.plugin));
        this.bind(ISkin.class).to(new SkinProvider(this.plugin));
        this.bind(ITagCategory.class).to(new ReportCategoryProvider(this.plugin));
        this.bind(ITag.class).to(new TagProvider(this.plugin));
        this.bind(XEnchantment.class).to(new EnchantmentProvider());
        this.bind(VoidReason.class).to(new VoidReasonProvider());
        this.bind(XMaterial.class).to(new EnumProvider(XMaterial.class, "material"));
        this.bind(String.class).annotated(PlayerNames.class).to(new PlayersProvider(this.plugin));
        this.bind(String.class).annotated(LogFiles.class).to(new LogFilesProvider(this.plugin));
        this.bind(String.class).annotated(Groups.class).to(new GroupsProvider(this.plugin));
        this.bind(Duration.class).to(new DurationProvider());
        this.bind(String.class).annotated(DurationInput.class).to(new DurationTextProvider());
        this.bind(PunishmentType.class).to(new EnumProvider(PunishmentType.class));
        this.bind(FilterType.class).to(new EnumProvider(FilterType.class));
        this.bind(WhitelistAccess.class).to(new EnumProvider(WhitelistAccess.class));
        this.bind(String.class).annotated(ValidName.class).to(new PunishmentArgumentModifier());
        this.bind(RequireSecurity.class).to(new PhoenixExecuteCondition(this.plugin));
    }

    public void register() {
        this.register(new DisguiseCommand(this.plugin));
        this.register(new RedisguiseCommand(this.plugin));
        this.register(new UndisguiseCommand(this.plugin));
        this.register(new ListCommand(this.plugin));
        this.register(new MedialistCommand(this.plugin));
        this.register(new StafflistCommand(this.plugin));
        this.register(new SettingsCommand(this.plugin));
        this.register(new TogglechatCommand(this.plugin));
        this.register(new TogglechatmentionCommand(this.plugin));
        this.register(new TogglefriendsCommand(this.plugin));
        this.register(new TogglemessagesCommand(this.plugin));
        this.register(new TogglesoundsCommand(this.plugin));
        this.register(new TogglestaffCommand(this.plugin));
        this.register(new ToggletipsCommand(this.plugin));
        this.register(new ChatsnapshotCommand(this.plugin));
        this.register(new CustomtagsCommand(this.plugin));
        this.register(new GitdebugCommand(this.plugin));
        this.register(new IgnoreCommand(this.plugin));
        this.register(new MessageCommand(this.plugin));
        this.register(new PlaytimeCommand(this.plugin));
        this.register(new ReplyCommand(this.plugin));
        this.register(new SocialsCommand(this.plugin));
        this.register(new SpawnCommand(this.plugin));
        this.register(new TagCommand(this.plugin));
        this.register(new GrantCommand(this.plugin));
        this.register(new OgrantCommand(this.plugin));
        this.register(new GrantsCommand(this.plugin));
        this.register(new RgrantCommand(this.plugin));
        this.register(new RankCommand(this.plugin));
        this.register(new AltsCommand(this.plugin));
        this.register(new WarningsCommand(this.plugin));
        this.register(new GranthistoryCommand(this.plugin));
        this.register(new HistoryCommand(this.plugin));
        this.register(new LookupCommand(this.plugin));
        this.register(new RemovewarnCommand(this.plugin));
        this.register(new StaffhistoryCommand(this.plugin));
        this.register(new StaffactivityCommand(this.plugin));
        this.register(new AmivanishedCommand(this.plugin));
        this.register(new ChatlogsCommand(this.plugin));
        this.register(new BanwaveCommand(this.plugin));
        this.register(new NotesCommand());
        this.register(new PunishCommand(this.plugin));
        this.register(new AuthCommand(this.plugin));
        this.register(new AuthsetupCommand(this.plugin));
        if (this.plugin.getOwnerClaim() != null && this.plugin.getOwnerClaim().isEnabled()) {
            this.register(new PxclaimCommand(this.plugin));
        }
        this.register(new Force2faCommand());
        this.register(new Reset2faCommand());
        this.register(new AdminprofileCommand(this.plugin));
        this.register(new AlertCommand(this.plugin));
        this.register(new AnalyticsCommand(this.plugin));
        this.register(new CommandlogsCommand(this.plugin));
        this.register(new CustomtagsadminCommand(this.plugin));
        this.register(new DisguiseadminCommand(this.plugin));
        this.register(new ExeccmdCommand(this.plugin));
        this.register(new FilterCommand(this.plugin));
        this.register(new GroupinfoCommand(this.plugin));
        this.register(new MclogsCommand(this.plugin));
        this.register(new NotificationsCommand(this.plugin));
        this.register(new PhoenixCommand(this.plugin));
        this.register(new PanelCommand(this.plugin));
        this.register(new TransferCommand(this.plugin));
        this.register(new ResourcepackCommand(this.plugin));
        this.register(new ProxyinfoCommand(this.plugin));
        this.register(new PunishmentladderCommand(this.plugin));
        this.register(new RebootCommand(this.plugin));
        this.register(new ServerinfoCommand(this.plugin));
        this.register(new SetspawnCommand());
        this.register(new SocialspyCommand(this.plugin));
        this.register(new TimelineCommand());
        this.register(new UserCommand(this.plugin));
        this.register(new WhitelistCommand(this.plugin));
        this.register(new SecurityCommand(this.plugin));
        this.register(new DeletemessageCommand(this.plugin));
        this.register(
                new PunishmentExecution(
                        this.plugin,
                        PunishmentType.BLACKLIST,
                        MessageConfig.PUNISHMENT.BLACKLIST.DEFAULT_REASON,
                        true,
                        false),
                "blacklist",
                "core.command.blacklist",
                "bl");
        this.register(
                new PunishmentExecution(
                        this.plugin,
                        PunishmentType.BAN,
                        MessageConfig.PUNISHMENT.BAN.DEFAULT_REASON,
                        false,
                        false),
                "ban",
                "core.command.ban",
                "b");
        this.register(
                new PunishmentExecution(
                        this.plugin,
                        PunishmentType.BAN,
                        MessageConfig.PUNISHMENT.IPBAN.DEFAULT_REASON,
                        true,
                        false),
                "ipban",
                "core.command.ipban",
                "ib");
        this.register(
                new PunishmentExecution(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.MUTE.DEFAULT_REASON,
                        false,
                        false),
                "mute",
                "core.command.mute");
        this.register(
                new PunishmentExecution(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.IPMUTE.DEFAULT_REASON,
                        true,
                        false),
                "ipmute",
                "core.command.ipmute",
                "im");
        this.register(
                new PunishmentExecution(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.MUTE.DEFAULT_REASON,
                        false,
                        true),
                "shadowmute",
                "core.command.shadowmute",
                "smute");
        this.register(
                new PunishmentDurationPolicy(
                        this.plugin,
                        PunishmentType.BAN,
                        MessageConfig.PUNISHMENT.TEMPBAN.DEFAULT_REASON,
                        false,
                        false),
                "tempban",
                "core.command.tempban",
                "tban",
                "tb");
        this.register(
                new PunishmentDurationPolicy(
                        this.plugin,
                        PunishmentType.BAN,
                        MessageConfig.PUNISHMENT.TEMPIPBAN.DEFAULT_REASON,
                        true,
                        false),
                "tempipban",
                "core.command.tempipban",
                "tipban",
                "tib");
        this.register(
                new PunishmentDurationPolicy(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.TEMPMUTE.DEFAULT_REASON,
                        false,
                        false),
                "tempmute",
                "core.command.tempmute",
                "tmute",
                "tm");
        this.register(
                new PunishmentDurationPolicy(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.TEMPIPMUTE.DEFAULT_REASON,
                        true,
                        false),
                "tempipmute",
                "core.command.tempipmute",
                "tipmute",
                "tim");
        this.register(
                new PunishmentDurationPolicy(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.TEMPMUTE.DEFAULT_REASON,
                        false,
                        true),
                "shadowtempmute",
                "core.command.shadowtempmute",
                "stmute",
                "stm");
        this.register(
                new RebanCommand(
                        this.plugin,
                        PunishmentType.BLACKLIST,
                        MessageConfig.PUNISHMENT.BLACKLIST.DEFAULT_REASON),
                "reblacklist",
                "core.command.reblacklist",
                "rebl");
        this.register(
                new RebanCommand(
                        this.plugin,
                        PunishmentType.BAN,
                        MessageConfig.PUNISHMENT.BAN.DEFAULT_REASON),
                "reban",
                "core.command.reban",
                "reb");
        this.register(
                new RebanCommand(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.MUTE.DEFAULT_REASON),
                "remute",
                "core.command.remute",
                "rem");
        this.register(new KickCommand(this.plugin));
        this.register(new WarnCommand(this.plugin));
        this.register(
                new PunishmentPermissions(this.plugin, PunishmentType.BLACKLIST),
                "voidblacklists",
                "core.command.voidblacklists");
        this.register(
                new PunishmentPermissions(this.plugin, PunishmentType.BAN),
                "voidbans",
                "core.command.voidbans");
        this.register(
                new PunishmentPermissions(this.plugin, PunishmentType.MUTE),
                "voidmutes",
                "core.command.voidmutes");
        this.register(new VoidpunishmentCommand(this.plugin));
        this.register(new WipekicksCommand(this.plugin));
        this.register(new WipewarnsCommand(this.plugin));
        this.register(new StaffrollbackCommand(this.plugin));
        this.register(
                new PunishmentRevocation(
                        this.plugin,
                        PunishmentType.BLACKLIST,
                        MessageConfig.PUNISHMENT.UNBLACKLIST.DEFAULT_REASON),
                "unblacklist",
                "core.command.unblacklist",
                "ubl",
                "unbl");
        this.register(
                new PunishmentRevocation(
                        this.plugin,
                        PunishmentType.BAN,
                        MessageConfig.PUNISHMENT.UNBAN.DEFAULT_REASON),
                "unban",
                "core.command.unban",
                "ub");
        this.register(
                new PunishmentRevocation(
                        this.plugin,
                        PunishmentType.MUTE,
                        MessageConfig.PUNISHMENT.UNMUTE.DEFAULT_REASON),
                "unmute",
                "core.command.unmute",
                "um");
        if (SettingsConfig.SERVER.PUNISHMENTS.COMPETITIVE_BANS) {
            this.register(
                    new PunishmentExecution(
                            this.plugin,
                            PunishmentType.COMPETITIVE,
                            MessageConfig.PUNISHMENT.COMPETITIVE_BAN.DEFAULT_REASON,
                            false,
                            false),
                    "cban",
                    "core.command.cban",
                    "cb");
            this.register(
                    new PunishmentDurationPolicy(
                            this.plugin,
                            PunishmentType.COMPETITIVE,
                            MessageConfig.PUNISHMENT.COMPETITIVE_TEMPBAN.DEFAULT_REASON,
                            false,
                            false),
                    "ctempban",
                    "core.command.ctempban",
                    "ctban",
                    "ctb");
            this.register(
                    new PunishmentRevocation(
                            this.plugin,
                            PunishmentType.COMPETITIVE,
                            MessageConfig.PUNISHMENT.COMPETITIVE_UNBAN.DEFAULT_REASON),
                    "cunban",
                    "core.command.cunban",
                    "cub");
        }
        if (MetricsReporter.text.equals("nl7YKN6ezffpwRWXp2qKz6slfGn6XO4XgH2Um5pv6Zk=")) {
            this.register(new TypehistCommand());
        }
        if (SettingsConfig.BASICS.ENABLED) {
            this.registerBasicCommands();
        }
        if (Phoenix.staffFeaturesWanted()) {
            this.registerStaffCommands();
        }
    }

    private void registerStaffCommands() {
        this.bind(IReportCategory.class)
                .to(new CategoryProvider(this.plugin.getReportCategoryHandler()));
        if (SettingsConfig.MODMODE.ENABLED) {
            this.register(new ModmodeCommand(this.plugin));
        }
        if (SettingsConfig.VANISH.ENABLED) {
            this.register(new VanishCommand(this.plugin));
            this.register(new HidestaffCommand(this.plugin));
        }
        if (SettingsConfig.CHAT_CONTROL.ENABLED) {
            this.register(new ClearchatCommand(this.plugin));
            this.register(new MutechatCommand(this.plugin));
            this.register(new SlowchatCommand(this.plugin));
        }
        if (SettingsConfig.REPORTS.ENABLED) {
            this.register(new ReportCommand(this.plugin));
            this.register(new ReportsCommand(this.plugin));
            this.register(new RequestCommand(this.plugin));
            this.register(new ReportmuteCommand(this.plugin));
            this.register(new WatchlistCommand(this.plugin));
        }
        if (SettingsConfig.FREEZE.ENABLED) {
            this.register(new FreezeCommand(this.plugin));
            this.register(new SetfreezelocCommand(this.plugin));
        }
        if (SettingsConfig.MODMODE.ENABLED) {
            this.register(new InvseeCommand());
        }
        if (SettingsConfig.PANIC.ENABLED) {
            this.register(new PanicCommand(this.plugin));
            this.register(new UnpanicCommand(this.plugin));
        }
    }

    private void registerBasicCommands() {
        this.register(new BuildCommand(this.plugin));
        this.register(new CraftCommand());
        this.register(new GodCommand(this.plugin));
        this.register(new MoreCommand());
        this.register(new SkullCommand());
        this.register(new GamemodeCommand());
        this.register(new EnderchestCommand(this.plugin));
        this.register(new PingCommand());
        this.register(new ShrugCommand());
        this.register(new HelpCommand());
        this.register(new AddloreCommand());
        this.register(new RemoveloreCommand());
        this.register(new RenameCommand());
        this.register(new TeleportallCommand());
        this.register(new TeleportCommand(this.plugin));
        this.register(new TeleporthereCommand(this.plugin));
        this.register(new TeleportpositionCommand(this.plugin));
        this.register(new TeleportworldCommand());
        this.register(new TopCommand());
        this.register(new BroadcastCommand());
        this.register(new ClearCommand(this.plugin));
        this.register(new EnchantCommand());
        this.register(new FeedCommand(this.plugin));
        this.register(new FlyCommand(this.plugin));
        this.register(new HealCommand(this.plugin));
        this.register(new ItemCommand());
        this.register(new KillCommand(this.plugin));
        this.register(new MasssayCommand(this.plugin));
        this.register(new RepairCommand());
        this.register(new SpeedCommand());
        this.register(new StaffalertCommand(this.plugin));
        this.register(new StaffbroadcastCommand());
        this.register(new SudoCommand(this.plugin));
        this.register(
                new GamemodeAction("survival"), "gms", "core.command.gamemode.survival", "gm0");
        this.register(
                new GamemodeAction("creative"), "gmc", "core.command.gamemode.creative", "gm1");
        this.register(
                new GamemodeAction("adventure"), "gma", "core.command.gamemode.adventure", "gm2");
        this.register(
                new GamemodeAction("spectator"), "gmsp", "core.command.gamemode.spectator", "gm3");
        this.register(new SocialMediaAccount(MessageConfig.BASIC.SOCIAL.STORE), "store");
        this.register(
                new SocialMediaAccount(MessageConfig.BASIC.SOCIAL.TEAMSPEAK),
                "teamspeak",
                null,
                "ts");
        this.register(
                new SocialMediaAccount(MessageConfig.BASIC.SOCIAL.WEBSITE),
                "website",
                null,
                "site");
        this.register(
                new SocialMediaAccount(MessageConfig.BASIC.SOCIAL.DISCORD), "discord", null, "dc");
        this.register(new SocialMediaAccount(MessageConfig.BASIC.SOCIAL.TWITTER), "twitter");
    }

    public void runTask(Runnable runnable, boolean matches) {
        if (matches == !Bukkit.isPrimaryThread()) {
            runnable.run();
        } else if (matches) {
            Tasks.runAsync(runnable);
        } else {
            Tasks.run(this.plugin, runnable);
        }
    }

    public Map<String, String> getRegisteredCommands() {
        return this.registeredCommands;
    }
}

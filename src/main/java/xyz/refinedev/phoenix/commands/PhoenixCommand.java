package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.wrapper.CommandHandle;
import xyz.refinedev.lib.commands.wrapper.CommandWrapper;
import xyz.refinedev.lib.io.lettuce.core.api.sync.RedisServerCommands;
import xyz.refinedev.lib.snakeyml.Yaml;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.YamlProblem;
import xyz.refinedev.phoenix.editor.EditorChangePreview;
import xyz.refinedev.phoenix.editor.EditorRequestContext;
import xyz.refinedev.phoenix.editor.EditorSession;
import xyz.refinedev.phoenix.editor.EditorSessionFactory;
import xyz.refinedev.phoenix.editor.EditorSessionStore;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;
import xyz.refinedev.phoenix.handler.database.DatabaseProbe;
import xyz.refinedev.phoenix.handler.database.DatabaseProbeResult;
import xyz.refinedev.phoenix.handler.database.DatabaseSettings;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.internal.BuildInfo;
import xyz.refinedev.phoenix.menu.impl.SetupMenu;
import xyz.refinedev.phoenix.menu.impl.setup.SetupTierMenu;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentLadderUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.ranks.RankUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.ConfigForceUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.FilterUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.MongoValidatePacket;
import xyz.refinedev.phoenix.packet.impl.server.ServerCommandPacket;
import xyz.refinedev.phoenix.packet.impl.tags.TagUpdatePacket;
import xyz.refinedev.phoenix.permissible.PermissionTracer;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.repository.IFilterRepository;
import xyz.refinedev.phoenix.repository.IPunishmentLadderRepository;
import xyz.refinedev.phoenix.repository.IRankRepository;
import xyz.refinedev.phoenix.repository.ITagRepository;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.setup.CheckStatus;
import xyz.refinedev.phoenix.setup.ConfigurationDiff;
import xyz.refinedev.phoenix.setup.PermissionCatalog;
import xyz.refinedev.phoenix.setup.SetupCheck;
import xyz.refinedev.phoenix.setup.SetupChecks;
import xyz.refinedev.phoenix.setup.SetupGuidance;
import xyz.refinedev.phoenix.setup.SetupMessages;
import xyz.refinedev.phoenix.setup.SetupRecommendations;
import xyz.refinedev.phoenix.setup.SetupSnapshot;
import xyz.refinedev.phoenix.setup.SetupSummary;
import xyz.refinedev.phoenix.setup.SetupTier;
import xyz.refinedev.phoenix.setup.check.BypassPermissionCatalog;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.ConfirmationMenus;
import xyz.refinedev.phoenix.utils.Diagnostics;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.RetentionEnvironment;
import xyz.refinedev.phoenix.utils.VersionUtils;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Help
@Register(name = "phoenix")
@Requires("core.command.phoenix")
public class PhoenixCommand {

    private final Phoenix plugin;

    @Command(name = "reload", description = "Reload the core plugin")
    public void reload(@Sender CommandSender sender) {
        List<String> values;
        if (!(values = this.plugin.reloadFiles()).isEmpty()) {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.PHOENIX.RELOAD.FAILED)));
            for (String text : values) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.PHOENIX.RELOAD.PROBLEM)
                                        .replace("<problem>", text)));
            }
        } else {
            Phoenix.MAIN_COLOR = GlobalConfig.NETWORK.COLORS.MAIN;
            Phoenix.MIDDLE_COLOR = GlobalConfig.NETWORK.COLORS.MIDDLE;
            Phoenix.SECONDARY_COLOR = GlobalConfig.NETWORK.COLORS.SECONDARY;
            Phoenix.BRIGHT_COLOR = GlobalConfig.NETWORK.COLORS.BRIGHT;
            Phoenix.RAIL_COLOR = GlobalConfig.NETWORK.COLORS.RAIL;
            Phoenix.MUTED_COLOR = GlobalConfig.NETWORK.COLORS.MUTED;
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.plugin
                        .getProfileHandler()
                        .getCachedProfile(player.getUniqueId())
                        .updatePermissions();
            }
            sender.sendMessage(Color.translate("&aFiles have been reloaded!"));
        }
    }

    @Command(name = "debug-mode", description = "Toggle debug mode")
    public void debugMode(@Sender CommandSender sender) {
        this.plugin.setDebugMode(!this.plugin.isDebugMode());
        sender.sendMessage(
                Color.translate(
                        Phoenix.MIDDLE_COLOR
                                + "Debug mode has been "
                                + Phoenix.MAIN_COLOR
                                + (this.plugin.isDebugMode() ? "enabled" : "disabled")
                                + Phoenix.MIDDLE_COLOR
                                + "."));
    }

    @Command(
            name = "version",
            description = "Show the Phoenix version, commit and loader branch",
            async = true)
    public void version(@Sender CommandSender sender) {
        BuildInfo buildInfo = this.plugin.getBuildInfo();
        String text = LocaleUtil.tr(sender, MessageConfig.PHOENIX.VERSION.UNKNOWN);
        VersionUtils versionUtils;
        String text2 =
                (versionUtils = VersionUtils.of(this.plugin.getDescription().getVersion())) == null
                        ? this.plugin.getDescription().getVersion()
                        : buildInfo.append(versionUtils.toString());
        String text3 =
                RetentionEnvironment.formatString(
                        new File("plugins/Retention", "loader.yml"),
                        this.plugin.getDescription().getName());
        for (String text4 : LocaleUtil.tr(sender, MessageConfig.PHOENIX.VERSION.LINES)) {
            sender.sendMessage(
                    Color.translate(
                            text4.replace("<version>", text2)
                                    .replace(
                                            "<commit>",
                                            formatString(buildInfo.getTextString(), text))
                                    .replace("<branch>", formatString(buildInfo.getText2(), text))
                                    .replace("<loader-branch>", formatString(text3, text))
                                    .replace(
                                            "<server>",
                                            Bukkit.getName() + " " + Bukkit.getBukkitVersion())));
        }
    }

    private static String formatString(String text, String text2) {
        return text == null ? text2 : text;
    }

    @Command(name = "debug", description = "Show some information about phoenix", async = true)
    public void debug(@Sender CommandSender sender) {
        long value = this.plugin.getProfileHandler().getRepository().count();
        int size2 = this.plugin.getRankHandler().getAvailableRanks().size();
        int size3 = this.plugin.getNetworkHandler().getServers().size();
        int size4 = this.plugin.getNetworkHandler().getProxies().size();
        int size5 = this.plugin.getFilterHandler().getRepository().getCache().size();
        long value2 = this.plugin.getChatLogHandler().getRepository().count();
        long value3 = this.plugin.getLoginHandler().getRepository().count();
        long value4 = this.plugin.getGrantHandler().getRepository().count();
        long value5 = this.plugin.getPunishmentHandler().getRepository().count();
        int size6 = this.plugin.getTagHandler().getTagRepository().getCache().size();
        int size7 = this.plugin.getTagHandler().getCategoryRepository().getCache().size();
        int size8 = this.plugin.getDisguiseHandler().getAllDisguisedProfiles().size();
        for (String text : LocaleUtil.tr(sender, MessageConfig.PHOENIX.DEBUG)) {
            sender.sendMessage(
                    Color.translate(
                            text.replace("<profiles>", "" + value)
                                    .replace("<ranks>", "" + size2)
                                    .replace("<servers>", "" + size3)
                                    .replace("<proxies>", "" + size4)
                                    .replace("<filters>", "" + size5)
                                    .replace("<chatLogs>", "" + value2)
                                    .replace("<logins>", "" + value3)
                                    .replace("<grants>", "" + value4)
                                    .replace("<punishments>", "" + value5)
                                    .replace("<tags>", "" + size6)
                                    .replace("<categories>", "" + size7)
                                    .replace("<disguisedPlayers>", "" + size8)));
        }
    }

    @RequireSecurity
    @Command(
            name = "import litebans",
            aliases = "import-litebans",
            description = "Import all punishments from LiteBans")
    @Requires("*")
    public void importLitebans(@Sender Player player) {
        if (!this.plugin.getServer().getPluginManager().isPluginEnabled("LiteBans")) {
            throw new ExitMessage("&cYou must install LiteBans to use this.");
        }
        ConfirmationMenus.applyPhoenix(
                this.plugin,
                player,
                "Import from LiteBans",
                true,
                result -> {
                    if (result) {
                        this.plugin.getImportHandler().sendMessage(this.plugin, player);
                    }
                });
    }

    @RequireSecurity
    @Command(
            name = "import luckperms",
            aliases = "import-luckperms",
            description = "Import all ranks & users from Luck Perms")
    @Requires("*")
    public void importLuckperms(@Sender Player player) {
        if (!this.plugin.getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            throw new ExitMessage("&cYou must install LuckPerms to use this.");
        }
        ConfirmationMenus.applyPhoenix(
                this.plugin,
                player,
                "Import from LuckPerms",
                true,
                result -> {
                    if (result) {
                        this.plugin.getImportHandler().sendMessageByEnabled(this.plugin, player);
                    }
                });
    }

    @RequireSecurity
    @Command(
            name = "import aquacore",
            aliases = "import-aquacore",
            description = "Import ranks, grants and punishments from AquaCore")
    @Requires("*")
    public void importAquacore(@Sender Player player, @Named("database") String text) {
        ConfirmationMenus.applyPhoenix(
                this.plugin,
                player,
                "Import from AquaCore",
                true,
                result -> {
                    if (result) {
                        this.plugin.getImportHandler().sendMessage(this.plugin, player, text);
                    }
                });
    }

    @Command(
            name = "doctor",
            description = "Check the server's Phoenix setup for problems",
            async = true)
    public void doctor(@Sender CommandSender sender) {
        sender.sendMessage(Color.translate(Phoenix.MAIN_COLOR + "&lPhoenix Doctor"));
        SetupMessages.sendMessage(sender, Diagnostics.findList(this.plugin));
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.PHOENIX.DOCTOR.DONE)));
    }

    @Command(
            name = "why",
            description = "Explain why a player has or lacks a permission",
            async = true)
    @Requires("core.command.phoenix.why")
    public void why(
            @Sender CommandSender sender,
            @Named("player") IProfile profile,
            @Named("permission") String text,
            @Optional @Named("server") IServerData serverData) {
        PermissionTracer.sendPlayer(
                sender,
                profile,
                PermissionTracer.findEntry(this.plugin, profile, text, serverData));
    }

    @Command(
            name = "verbose",
            description = "Watch every permission check a player triggers",
            async = true)
    @Requires("core.command.phoenix.verbose")
    public void verbose(
            @Sender CommandSender sender,
            @Named("player") IProfile profile,
            @Optional @Named("filter") String text) {
        Player player;
        if ((player = Bukkit.getPlayer(profile.getUuid())) == null) {
            Iterator<IServerData> iterator2 =
                    this.plugin.getNetworkHandler().getServers().iterator();
            while (iterator2.hasNext()) {
                IServerData serverData;
                if ((serverData = (IServerData) iterator2.next())
                        .getOnlinePlayers()
                        .contains(profile.getUuid())) {
                    throw new ExitMessage(
                            "&c"
                                    + profile.getName()
                                    + " is on "
                                    + serverData.getServerName()
                                    + ". Run verbose there.");
                }
            }
            throw new ExitMessage("&c" + profile.getName() + " is not online.");
        } else {
            PermissionTracer.create(sender, player, text);
            sender.sendMessage(
                    Color.translate(
                            Phoenix.MIDDLE_COLOR
                                    + "Watching permission checks for "
                                    + Phoenix.MAIN_COLOR
                                    + player.getName()
                                    + (text == null
                                            ? ""
                                            : Phoenix.MIDDLE_COLOR
                                                    + " matching "
                                                    + Phoenix.MAIN_COLOR
                                                    + text)
                                    + Phoenix.MIDDLE_COLOR
                                    + ". Run "
                                    + Phoenix.MAIN_COLOR
                                    + "/phoenix verbose stop"
                                    + Phoenix.MIDDLE_COLOR
                                    + " to finish."));
        }
    }

    @Command(
            name = "verbose stop",
            description = "Stop watching permission checks and show a summary",
            async = true)
    @Requires("core.command.phoenix.verbose")
    public void verboseStop(@Sender CommandSender sender) {
        PermissionTracer.Session session;
        if ((session = PermissionTracer.findSession(sender)) == null) {
            throw new ExitMessage("&cYou aren't running verbose.");
        }
        PermissionTracer.updateSession(session, "stopped");
    }

    @Command(
            name = "verbose upload",
            description = "Upload the full log of your last verbose session",
            async = true)
    @Requires("core.command.phoenix.verbose")
    public void verboseUpload(@Sender CommandSender sender) {
        PermissionTracer.Session session;
        if ((session = PermissionTracer.findSessionBySessionsByNameMap(sender)) == null) {
            throw new ExitMessage("&cYou have no finished verbose session to upload.");
        }
        PermissionTracer.applyCommandSender(sender, session);
    }

    @Command(
            name = "setup",
            description = "What is configured, what is not, and what fixes it",
            async = true)
    public void setup(@Sender CommandSender sender) {
        List<SetupCheck> values = SetupSnapshot.addAll(this.plugin);
        if (this.plugin.getSetupReminder() != null) {
            this.plugin.getSetupReminder().setList(values);
        }
        if (sender instanceof Player) {
            this.plugin
                    .getMenuHandler()
                    .openMenuAsync(
                            new SetupTierMenu(SetupTier.ESSENTIALS, values), (Player) sender);
        } else {
            this.sendMessage(sender, values);
        }
    }

    @Command(
            name = "setup features",
            description = "One area per feature, for setting that feature up",
            async = true)
    public void setupFeatures(@Sender CommandSender sender) {
        if (sender instanceof Player) {
            SetupTierMenu.applyPhoenix(this.plugin, (Player) sender, SetupTier.FEATURES);
        } else {
            this.sendMessage(sender, SetupRecommendations.addAll(this.plugin));
        }
    }

    @Command(name = "setup area", description = "Open one setup area by name", async = true)
    public void setupArea(@Sender Player player, @Named("area") @Text String text) {
        List<SetupCheck> values = SetupSnapshot.addAll(this.plugin);
        if (!this.matchesEnabled(player, SetupTier.ESSENTIALS, values, text)) {
            if (!this.matchesEnabled(
                    player, SetupTier.FEATURES, SetupRecommendations.addAll(this.plugin), text)) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.PHOENIX.SETUP.NO_AREA)
                                .replace("<area>", text));
            }
        }
    }

    @RequireSecurity
    @Command(
            name = "setup database",
            description = "Test the database settings saved in global.yml",
            async = true)
    public void setupDatabase(@Sender CommandSender sender) {
        File file;
        String text;
        if ((text = YamlProblem.find(file = new File("plugins/Phoenix", "global.yml"))) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.BROKEN_FILE)
                            .replace("<problem>", text));
        }
        DatabaseSettings databaseSettings;
        try {
            databaseSettings =
                    DatabaseSettings.of(
                            new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
        } catch (IOException oException) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.BROKEN_FILE)
                            .replace("<problem>", oException.getMessage()));
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.HEADER)));
        Iterator<DatabaseProbeResult> iterator2 =
                DatabaseProbe.findList(databaseSettings).iterator();
        while (iterator2.hasNext()) {
            DatabaseProbeResult databaseProbeResult;
            if ((databaseProbeResult = (DatabaseProbeResult) iterator2.next()).isEnabled()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.SKIPPED)
                                        .replace("<backend>", databaseProbeResult.getText())
                                        .replace("<hint>", databaseProbeResult.getText2())));
            } else if (databaseProbeResult.isEnabledByTextString()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.OK)
                                        .replace("<backend>", databaseProbeResult.getText())
                                        .replace(
                                                "<millis>",
                                                String.valueOf(databaseProbeResult.getTime()))));
            } else {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.FAIL)
                                        .replace("<backend>", databaseProbeResult.getText())
                                        .replace("<error>", databaseProbeResult.getTextString())));
                if (databaseProbeResult.getText2() != null) {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.HINT)
                                            .replace("<hint>", databaseProbeResult.getText2())));
                }
            }
        }
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DATABASE.NOTE)));
    }

    @Command(
            name = "permissions",
            description = "Write every Phoenix permission to plugins/Phoenix/permissions.md",
            async = true)
    public void permissions(@Sender CommandSender sender) {
        PermissionCatalog permissionCatalog = new PermissionCatalog();
        for (CommandWrapper commandWrapper :
                this.plugin.getCommandHandler().getRegistry().getWrappers().values()) {
            permissionCatalog.applyString(
                    commandWrapper.getPermission(),
                    "/" + commandWrapper.getName(),
                    commandWrapper.getDescription());
            Iterator<CommandHandle> iterator2 = commandWrapper.getHandles().values().iterator();
            while (iterator2.hasNext()) {
                CommandHandle commandHandle;
                if (!(commandHandle = (CommandHandle) iterator2.next()).isHidden()) {
                    String text =
                            commandHandle.hasPermission()
                                    ? commandHandle.getPermission()
                                    : commandWrapper.getPermission();
                    String text2 =
                            "/"
                                    + commandWrapper.getName()
                                    + (commandHandle.getName().isEmpty()
                                            ? ""
                                            : " " + commandHandle.getName());
                    permissionCatalog.applyString(text, text2, commandHandle.getDescription());
                }
            }
        }
        for (String text3 : BypassPermissionCatalog.strings) {
            permissionCatalog.applyString(text3, null, "Bypass");
        }
        for (Entry<String, String> entry : SetupGuidance.stringsByName.entrySet()) {
            permissionCatalog.applyString((String) entry.getKey(), null, (String) entry.getValue());
        }
        EditorOp[] editorOps;
        int value = (editorOps = EditorOp.values()).length;
        for (int index = 0; index < value; index++) {
            EditorOp editorOp = editorOps[index];
            String text4 = "panel " + editorOp.name().toLowerCase(Locale.ROOT).replace('_', ' ');
            permissionCatalog.applyString(PanelPermissions.ordinalByCounts(editorOp), text4, null);
            if (PanelPermissions.ordinal(editorOp)) {
                for (String text5 : PanelPermissions.keySet()) {
                    permissionCatalog.applyString(
                            PanelPermissions.formatString(editorOp, text5),
                            text4 + " (" + text5 + ")",
                            null);
                }
            }
        }
        File file = new File("plugins/Phoenix", "permissions.md");
        try {
            Files.write(file.toPath(), permissionCatalog.getList(), StandardCharsets.UTF_8);
        } catch (IOException oException) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.PHOENIX.PERMISSIONS.FAILED)
                            .replace("<error>", String.valueOf(oException.getMessage())));
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PHOENIX.PERMISSIONS.WRITTEN)
                                .replace("<count>", String.valueOf(permissionCatalog.getCount()))
                                .replace("<file>", "plugins/Phoenix/permissions.md")));
    }

    @Command(
            name = "preview",
            description = "Send yourself a message from messages.yml to see how it looks",
            async = true)
    public void preview(@Sender Player player, @Named("key") String text) {
        File file;
        String text2;
        if ((text2 = YamlProblem.find(file = new File("plugins/Phoenix", "messages.yml")))
                != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.PHOENIX.RELOAD.PROBLEM)
                            .replace("<problem>", text2));
        }
        if (!file.exists()) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.PHOENIX.RELOAD.PROBLEM)
                            .replace("<problem>", "messages.yml does not exist"));
        }
        Map entries = new LinkedHashMap();
        try {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(
                            Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
            try {
                Object result;
                if ((result = new Yaml().load(inputStreamReader)) instanceof Map) {
                    entries = ConfigurationDiff.findMap((Map<?, ?>) result);
                }
            } catch (Throwable throwable) {
                try {
                    inputStreamReader.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            inputStreamReader.close();
        } catch (IOException oException) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.PHOENIX.RELOAD.PROBLEM)
                            .replace("<problem>", String.valueOf(oException.getMessage())));
        }
        if (!entries.containsKey(text)) {
            List values = ConfigurationDiff.findList(entries.keySet(), text, 5);
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.PHOENIX.PREVIEW.UNKNOWN)
                            .replace("<key>", text)
                            .replace(
                                    "<suggestions>",
                                    values.isEmpty() ? "-" : String.join(", ", values)));
        }
        Object result2;
        List<String> values2 =
                (result2 = entries.get(text)) instanceof List
                        ? ((List<?>) result2)
                                .stream().map(String::valueOf).collect(Collectors.toList())
                        : Collections.singletonList(String.valueOf(result2));
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.PHOENIX.PREVIEW.HEADER)
                                .replace("<key>", text)));
        for (String text3 : values2) {
            player.sendMessage(Color.translate(text3.replace("<player>", player.getName())));
        }
    }

    private boolean matchesEnabled(
            Player player, SetupTier setupTier, List<SetupCheck> values, String text) {
        Iterator<SetupSummary> iterator2 = SetupChecks.findList(values).iterator();
        while (iterator2.hasNext()) {
            SetupSummary setupSummary;
            if ((setupSummary = (SetupSummary) iterator2.next()).getText().equalsIgnoreCase(text)) {
                this.plugin
                        .getMenuHandler()
                        .openMenuAsync(
                                new SetupMenu(setupTier, values, setupSummary.getText()), player);
                return true;
            }
        }
        return false;
    }

    private void sendMessage(CommandSender sender, List<SetupCheck> values) {
        SetupMessages.sendMessage(
                sender, values, LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.HEADER));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DONE)
                                .replace(
                                        "<todo>",
                                        String.valueOf(
                                                SetupChecks.calculateCount(
                                                        values, CheckStatus.TODO)))
                                .replace(
                                        "<warn>",
                                        String.valueOf(
                                                SetupChecks.calculateCount(
                                                        values, CheckStatus.WARN)))
                                .replace(
                                        "<fail>",
                                        String.valueOf(
                                                SetupChecks.calculateCount(
                                                        values, CheckStatus.FAIL)))));
    }

    @RequireSecurity
    @Command(
            name = "import luckperms-user-perms",
            aliases = "import-luckperms-user-perms",
            description = "Import user permissions from Luck Perms")
    @Requires("*")
    public void importLuckpermsUserPerms(@Sender Player player) {
        if (!this.plugin.getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            throw new ExitMessage("&cYou must install LuckPerms to use this.");
        }
        this.plugin.getImportHandler().applyPhoenixByPlatform(this.plugin);
    }

    @RequireSecurity
    @Command(
            name = "config set-master",
            description = "Set the master for configs to this server and push to redis",
            async = true)
    @Requires("core.command.phoenix.config")
    public void configSetMaster(@Sender CommandSender sender) {
        this.plugin
                .getDatabaseHandler()
                .runRedisCommand(
                        result -> result.set("px-configs-master", GlobalConfig.SERVER.NAME));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PHOENIX.CONFIG.SET_MASTER)
                                .replace("<server>", GlobalConfig.SERVER.NAME)));
        this.plugin.getConfigSyncHandler().cacheToRedis();
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.PHOENIX.CONFIG.PUSHED)));
    }

    @RequireSecurity
    @Command(name = "config fetch", description = "Fetch config files from redis", async = true)
    @Requires("core.command.phoenix.config")
    public void configFetch(@Sender CommandSender sender) {
        this.plugin.getConfigSyncHandler().loadFromRedis();
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.PHOENIX.CONFIG.UPDATED)));
    }

    @RequireSecurity
    @Command(
            name = "config push",
            description = "Set the config files in redis to the ones on the server",
            async = true)
    @Requires("core.command.phoenix.config")
    public void configPush(@Sender CommandSender sender) {
        this.plugin.getConfigSyncHandler().cacheToRedis();
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.PHOENIX.CONFIG.PUSHED)));
    }

    @RequireSecurity
    @Command(
            name = "config force-fetch-all",
            description = "Force every single server to fetch and use the configs in redis",
            async = true)
    @Requires("core.command.phoenix.config")
    public void configForceFetchAll(@Sender CommandSender sender) {
        this.plugin.getPacketHandler().sendSync(new ConfigForceUpdatePacket());
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.PHOENIX.CONFIG.FORCE_FETCHED)));
    }

    @RequireSecurity
    @Command(name = "editor open", description = "Open the web editor", async = true)
    @Requires("core.command.phoenix.editor")
    public void editorOpen(@Sender CommandSender sender) {
        UUID uUID =
                sender instanceof Player
                        ? ((Player) sender).getUniqueId()
                        : IProfile.getConsoleUuid();
        try {
            EditorSession editorSession =
                    this.plugin
                            .getEditorHandler()
                            .create(uUID, sender.getName())
                            .get(15L, TimeUnit.SECONDS);
            sender.sendMessage(Color.translate("&aA web editor session has been opened for you."));
            String text =
                    "https://editor.refinedev.org/editor/"
                            + editorSession.getText()
                            + "/"
                            + editorSession.getTextString()
                            + "/rank";
            new Clickable("&aLink: &e" + text, "&aClick to paste into chat.", "", text)
                    .sendToPlayer(sender);
            sender.sendMessage(
                    Color.translate(
                            "&c&lPlease note that any changes you make on the server will not"));
            sender.sendMessage(
                    Color.translate(
                            "&c&lbe reflected until you create a new session! You must run /phoenix editor update"));
            sender.sendMessage(
                    Color.translate("&c&lto actually see the changes from the web editor."));
        } catch (Exception exception) {
            sender.sendMessage(
                    Color.translate(
                            "&cThere was an error while trying to contact the web editor [1]."));
            this.plugin
                    .getLogger()
                    .log(Level.SEVERE, "Error while trying to contact the web editor:", exception);
        }
    }

    @RequireSecurity
    @Command(name = "editor update", description = "Update ranks from the web editor", async = true)
    @Requires("core.command.phoenix.editor")
    public void editorUpdate(@Sender CommandSender sender) {
        EditorSessionStore editorSessionStore;
        try {
            editorSessionStore =
                    this.plugin.getEditorHandler().thenApply().get(15L, TimeUnit.SECONDS);
        } catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof IllegalStateException) {
                throw new ExitMessage("&cYou did not have a editor session open.");
            }
            this.plugin
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Error while trying to contact the web editor:",
                            executionException);
            throw new ExitMessage("There was an error while trying to contact the web editor [2].");
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .log(Level.SEVERE, "Error while trying to contact the web editor:", exception);
            throw new ExitMessage("There was an error while trying to contact the web editor [2].");
        }
        try {
            EditorSessionFactory editorSessionFactory =
                    editorSessionStore.getEditorSessionFactory();
            List<String> values = this.findList(editorSessionStore);
            this.applyList(editorSessionFactory.getRanks());
            this.applyListByPlugin(editorSessionFactory.getTags());
            this.applyListByVoid(editorSessionFactory.getFilters());
            this.applyListByList(editorSessionFactory.getPunishmentLadders());
            sender.sendMessage(
                    Color.translate("&aUpdated your ranks, tags, filters & punishment ladders."));
            if (values.isEmpty()) {
                sender.sendMessage(Color.translate("&7The editor did not change anything."));
            } else {
                for (String text : values) {
                    sender.sendMessage(Color.translate(text));
                }
            }
        } catch (Exception exception2) {
            this.plugin
                    .getLogger()
                    .log(Level.SEVERE, "Error while applying the web editor snapshot:", exception2);
            throw new ExitMessage("There was an error while trying to contact the web editor [3].");
        }
    }

    private List<String> findList(EditorSessionStore editorSessionStore) {
        EditorSessionFactory editorSessionFactory = editorSessionStore.getEditorSessionFactory();
        ArrayList<String> values = new ArrayList<>();
        this.applyList(
                values,
                "Ranks",
                this.plugin.getRankHandler().getAvailableRanks(),
                editorSessionFactory.getRanks(),
                IRank::getName);
        this.applyList(
                values,
                "Tags",
                this.plugin.getTagHandler().getTagRepository().getCache(),
                editorSessionFactory.getTags(),
                ITag::getName);
        this.applyList(
                values,
                "Filters",
                this.plugin.getFilterHandler().getRepository().getCache(),
                editorSessionFactory.getFilters(),
                IFilter::getName);
        this.applyList(
                values,
                "Punishment ladders",
                this.plugin.getPunishmentLadderHandler().getRepository().getCache(),
                editorSessionFactory.getPunishmentLadders(),
                IPunishmentLadder::getId);
        this.applyList(
                values,
                "Activity",
                EditorChangePreview.findList(editorSessionStore.getEditorOperationses()));
        return values;
    }

    private <T> void applyList(
            List<String> values,
            String text,
            Collection<T> values2,
            Collection<T> values3,
            Function<T, String> function) {
        this.applyList(
                values,
                text,
                EditorChangePreview.findList(this.plugin.getGson(), values2, values3, function));
    }

    private void applyList(List<String> values, String text, List<String> values2) {
        if (!values2.isEmpty()) {
            values.add("&f&l" + text);
            values.addAll(values2);
        }
    }

    private void applyList(List<IRank> values) {
        IRankRepository repository = this.plugin.getRankHandler().getRepository();
        List<IRank> availableRanks = this.plugin.getRankHandler().getAvailableRanks();
        EditorRequestContext.applyListRepository(
                repository,
                availableRanks,
                values,
                (result, result2) ->
                        this.plugin
                                .getPacketHandler()
                                .sendSync(new RankUpdatePacket(result, result2)));
    }

    private void applyListByPlugin(List<ITag> values) {
        ITagRepository tagRepository = this.plugin.getTagHandler().getTagRepository();
        List<ITag> cache = this.plugin.getTagHandler().getTagRepository().getCache();
        EditorRequestContext.applyListRepository(
                tagRepository,
                cache,
                values,
                (result, result2) ->
                        this.plugin
                                .getPacketHandler()
                                .sendSync(new TagUpdatePacket(result, result2)));
    }

    private void applyListByVoid(List<IFilter> values) {
        IFilterRepository repository = this.plugin.getFilterHandler().getRepository();
        List<IFilter> cache = this.plugin.getFilterHandler().getRepository().getCache();
        EditorRequestContext.applyListRepository(
                repository,
                cache,
                values,
                (result, result2) ->
                        this.plugin
                                .getPacketHandler()
                                .sendSync(new FilterUpdatePacket(result, result2)));
    }

    private void applyListByList(List<IPunishmentLadder> values) {
        IPunishmentLadderRepository repository =
                this.plugin.getPunishmentLadderHandler().getRepository();
        List<IPunishmentLadder> cache =
                this.plugin.getPunishmentLadderHandler().getRepository().getCache();
        EditorRequestContext.applyListRepository(
                repository,
                cache,
                values,
                (result, result2) ->
                        this.plugin
                                .getPacketHandler()
                                .sendSync(new PunishmentLadderUpdatePacket(result, result2)));
    }

    @RequireSecurity
    @Command(
            name = "editor close",
            description = "Close an open web editor session without updating",
            async = true)
    @Requires("core.command.phoenix.editor")
    public void editorClose(@Sender CommandSender sender) {
        try {
            List<xyz.refinedev.phoenix.editor.EditorOperations> values =
                    this.plugin
                            .getEditorHandler()
                            .thenApplyByEditorSession()
                            .get(15L, TimeUnit.SECONDS);
            sender.sendMessage(Color.translate("&aClosed the session."));
            List<String> values2;
            if (!(values2 = EditorChangePreview.findList(values)).isEmpty()) {
                sender.sendMessage(
                        Color.translate(
                                "&7Grants and punishments apply as the editor makes them, so closing did not undo these."));
                sender.sendMessage(Color.translate("&f&lActivity"));
                for (String text : values2) {
                    sender.sendMessage(Color.translate(text));
                }
            }
        } catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof IllegalStateException) {
                throw new ExitMessage("&cYou did not have a editor session open.");
            }
            this.plugin
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Error while trying to contact the web editor:",
                            executionException);
            throw new ExitMessage("There was an error while trying to contact the web editor [3].");
        } catch (Exception exception) {
            this.plugin
                    .getLogger()
                    .log(Level.SEVERE, "Error while trying to contact the web editor:", exception);
            throw new ExitMessage("There was an error while trying to contact the web editor [3].");
        }
    }

    @Command(name = "repair flush-redis", aliases = "flushredis", hidden = true)
    public void repairFlushRedis(@Sender ConsoleCommandSender consoleCommandSender) {
        this.plugin.getDatabaseHandler().runRedisCommand(RedisServerCommands::flushdb);
    }

    @RequireSecurity
    @Command(
            name = "extendgrants",
            async = true,
            description = "Extends the duration of all active grants - Causes lag!")
    public void extendgrants(
            @Sender CommandSender sender,
            @Named("duration") @Option("temporary") Duration duration) {
        long timeTime = duration.getTimeTime();
        String text = duration.formatTimeMillis();
        int value = 0;
        Iterator<IGrant> iterator2 =
                this.plugin
                        .getGrantHandler()
                        .getRepository()
                        .fetchAllSync(
                                Filters.and(
                                        Filters.eq("active", true), Filters.eq("permanent", false)))
                        .iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).isActiveOnOneScope()) {
                grant.setDuration(grant.getDuration() + timeTime);
                this.plugin.getGrantHandler().getRepository().save(grant.getUuid(), grant);
                value++;
            }
        }
        sender.sendMessage(
                Color.translate(
                        Phoenix.MAIN_COLOR + "Extended " + value + " grants for " + text + "."));
    }

    @RequireSecurity
    @Command(
            name = "extendpunishments",
            async = true,
            description = "Extends the duration of all active punishments - Causes lag!")
    public void extendpunishments(
            @Sender CommandSender sender,
            @Named("duration") @Option("temporary") Duration duration) {
        long timeTime = duration.getTimeTime();
        String text = duration.formatTimeMillis();
        int value = 0;
        Iterator<IPunishment> iterator2 =
                this.plugin
                        .getPunishmentHandler()
                        .getRepository()
                        .fetchAllSync(
                                Filters.and(
                                        Filters.eq("voided", false),
                                        Filters.eq("active", true),
                                        Filters.eq("permanent", false)))
                        .iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next()).isActive()) {
                punishment.setDuration(punishment.getDuration() + timeTime);
                this.plugin
                        .getPunishmentHandler()
                        .getRepository()
                        .save(punishment.getUuid(), punishment);
                value++;
            }
        }
        sender.sendMessage(
                Color.translate(
                        Phoenix.MAIN_COLOR
                                + "Extended "
                                + value
                                + " punishments for "
                                + text
                                + "."));
    }

    @RequireSecurity
    @Command(name = "repair duped-profiles", aliases = "finddupedprofiles", async = true)
    public void repairDupedProfiles(@Sender CommandSender sender, @Named("name") String text) {
        for (IProfile profile :
                this.plugin
                        .getProfileHandler()
                        .getRepository()
                        .fetchAllSync(Filters.eq("nameToLowercase", text.toLowerCase()))) {
            sender.sendMessage(
                    "Profile Found! UUID: " + profile.getUuid() + " - Name: " + profile.getName());
        }
    }

    @RequireSecurity
    @Command(
            name = "repair mongo-creds",
            aliases = "match-mongo-creds",
            hidden = true,
            async = true)
    public void repairMongoCreds(@Sender CommandSender sender) {
        Set values2 =
                FastStreams.mapAndFilterArrayListToSet(
                        this.plugin.getNetworkHandler().getServers(),
                        IServerData::isOnline,
                        IServerData::getServerName);
        sender.sendMessage(
                Color.translate(
                        "&aChecking MongoDB Credentials for &e"
                                + values2.size()
                                + " &aservers..."));
        this.plugin.getPacketHandler().sendSync(new MongoValidatePacket());
        sender.sendMessage(Color.translate("&aWaiting &e1s&a for responses..."));
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException interruptedException) {
            throw new ExitMessage("There was an error while waiting for responses...");
        }
        Map<String, String> entries =
                this.plugin
                        .getDatabaseHandler()
                        .runRedisCommand(result -> result.hgetall("px-validate-mongo"));
        int value;
        for (value = 0; entries.size() != values2.size() && value < 10; value++) {
            sender.sendMessage(Color.translate("&aWaiting &e1s&a for responses..."));
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException interruptedException2) {
                throw new ExitMessage("There was an error while waiting for responses...");
            }
            entries =
                    this.plugin
                            .getDatabaseHandler()
                            .runRedisCommand(result -> result.hgetall("px-validate-mongo"));
        }
        if (value >= 10) {
            throw new ExitMessage("&cFailed to get responses from all servers.");
        }
        if (new HashSet(entries.values()).size() == 1) {
            sender.sendMessage(Color.translate("&aAll servers have the same MongoDB credentials."));
        } else {
            sender.sendMessage(
                    Color.translate("&cNot all servers have the same MongoDB credentials."));
            for (Entry<String, String> entry : entries.entrySet()) {
                Stream<Entry<String, String>> stream2 = entries.entrySet().stream();
                Set<String> values3 =
                        stream2.filter(
                                        result2 ->
                                                ((String) result2.getValue())
                                                                .equals(entry.getValue())
                                                        && !((String) result2.getKey())
                                                                .equals(entry.getKey()))
                                .map(Entry::getKey)
                                .collect(Collectors.toSet());
                sender.sendMessage(
                        Color.translate(
                                "&e"
                                        + (String) entry.getKey()
                                        + " &amatches with &e"
                                        + (values3.isEmpty()
                                                ? "No Servers"
                                                : String.join(", ", values3))));
            }
        }
        this.plugin
                .getDatabaseHandler()
                .runRedisCommand(result -> result.del(new String[] {"px-validate-mongo"}));
    }

    @RequireSecurity
    @Command(name = "perftest redis", aliases = "redisperftest", hidden = true, async = true)
    public void perftestRedis(@Sender CommandSender sender) {
        sender.sendMessage("Performance Test: Redis");
        long now = System.currentTimeMillis();
        int value =
                this.plugin
                        .getDatabaseHandler()
                        .runRedisCommand(result -> result.hgetall("px-servers").size());
        long value2 = System.currentTimeMillis() - now;
        sender.sendMessage("Fetch Servers: " + value + " - " + value2 + "ms");
    }

    @RequireSecurity
    @Command(
            name = "repair clear-alts",
            aliases = "clear-all-alts-mongo",
            hidden = true,
            async = true)
    public void repairClearAlts(@Sender CommandSender sender) {
        MongoDatabaseHandler mongoDatabaseHandler;
        if ((mongoDatabaseHandler = MongoDatabaseHandler.orNull(this.plugin.getDatabaseHandler()))
                == null) {
            throw new ExitMessage("This command is MongoDB-only.");
        }
        sender.sendMessage("Clearing all alts...");
        long now = System.currentTimeMillis();
        mongoDatabaseHandler
                .getProfiles()
                .updateMany(
                        new Document(),
                        new Document("$set", new Document("alts", new ArrayList())));
        mongoDatabaseHandler
                .getProfiles()
                .updateMany(
                        new Document(),
                        new Document("$set", new Document("knownAddresses", new ArrayList())));
        long value = System.currentTimeMillis() - now;
        sender.sendMessage("Cleared alts in " + value + "ms");
        this.plugin.getPacketHandler().send(new ServerCommandPacket("all", "stop", "Console"));
    }

    @RequireSecurity
    @Command(name = "perftest server", aliases = "serverperftest", hidden = true, async = true)
    public void perftestServer(@Sender CommandSender sender) {
        long now = System.currentTimeMillis();
        for (int index = 0; index < 50; index++) {
            int value = index;
            CompletableFuture.runAsync(
                    () -> {
                        this.plugin.getNetworkHandler().fetchServers();
                        sender.sendMessage("i: " + value);
                    });
            this.plugin.getUpdateTask().initializeByPlugin();
            sender.sendMessage("update: " + index);
        }
        long value2 = System.currentTimeMillis() - now;
        sender.sendMessage("Update Servers: " + value2 + "ms");
    }

    @RequireSecurity
    @Command(name = "perftest all", aliases = "perftest", hidden = true, async = true)
    public void perftestAll(@Sender CommandSender sender, @Named("target") IProfile profile) {
        sender.sendMessage("Performance Test: " + profile.getName());
        long now = System.currentTimeMillis();
        this.plugin.getProfileHandler().getRepository().fetchSync(profile.getUuid());
        sender.sendMessage(
                "Loading Profile (MongoDB) took " + (System.currentTimeMillis() - now) + "ms");
        long now2 = System.currentTimeMillis();
        this.plugin.getProfileHandler().getCacheRepository().fetchSync(profile.getUuid());
        sender.sendMessage(
                "Loading Profile (Redis) took " + (System.currentTimeMillis() - now2) + "ms");
        long now3 = System.currentTimeMillis();
        profile.loadProfile();
        sender.sendMessage(
                "Loading Profile (Both) took " + (System.currentTimeMillis() - now3) + "ms");
        long now4 = System.currentTimeMillis();
        List<IGrant> databaseGrantsSync =
                this.plugin.getGrantHandler().getDatabaseGrantsSync(profile.getUuid());
        sender.sendMessage(
                "Loading Grants ("
                        + databaseGrantsSync.size()
                        + ") took "
                        + (System.currentTimeMillis() - now4)
                        + "ms");
        long now5 = System.currentTimeMillis();
        List<IPunishment> databasePunishmentsSync =
                this.plugin.getPunishmentHandler().getDatabasePunishmentsSync(profile.getUuid());
        sender.sendMessage(
                "Loading Punishments ("
                        + databasePunishmentsSync.size()
                        + ") took "
                        + (System.currentTimeMillis() - now5)
                        + "ms");
        long now6 = System.currentTimeMillis();
        this.plugin
                .getGrantHandler()
                .getRepository()
                .getListCache()
                .put(profile.getUuid(), databaseGrantsSync);
        sender.sendMessage(
                "Updating Grants ("
                        + databaseGrantsSync.size()
                        + ") took "
                        + (System.currentTimeMillis() - now6)
                        + "ms");
        long now7 = System.currentTimeMillis();
        this.plugin
                .getPunishmentHandler()
                .getRepository()
                .getListCache()
                .put(profile.getUuid(), databasePunishmentsSync);
        sender.sendMessage(
                "Updating Punishments ("
                        + databasePunishmentsSync.size()
                        + ") took "
                        + (System.currentTimeMillis() - now7)
                        + "ms");
        long now8 = System.currentTimeMillis();
        this.plugin.getGrantHandler().updateGrants(profile.getUuid());
        sender.sendMessage(
                "Updating & Loading Grants took " + (System.currentTimeMillis() - now8) + "ms");
        long now9 = System.currentTimeMillis();
        this.plugin.getPunishmentHandler().updatePunishments(profile.getUuid());
        sender.sendMessage(
                "Updating & Loading Punishments took "
                        + (System.currentTimeMillis() - now9)
                        + "ms");
        long now10 = System.currentTimeMillis();
        this.plugin.getProfileHandler().getCacheRepository().delete(profile.getUuid());
        sender.sendMessage(
                "Lookup & Saving Profile (Redis) took "
                        + (System.currentTimeMillis() - now10)
                        + "ms");
        long now11 = System.currentTimeMillis();
        this.plugin.getProfileHandler().getCacheRepository().delete(profile.getUuid());
        sender.sendMessage(
                "Saving Profile (MongoDB) took " + (System.currentTimeMillis() - now11) + "ms");
        long now12 = System.currentTimeMillis();
        profile.saveProfileSync();
        sender.sendMessage(
                "Saving Profile (Total) took " + (System.currentTimeMillis() - now12) + "ms");
    }

    public PhoenixCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

package xyz.refinedev.phoenix.commands;

import com.google.gson.JsonObject;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Flag;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.mongodb.client.MongoCursor;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.menus.ConfirmMenu;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitGrantsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.ProfileDeletePacket;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitPunishmentPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.INameChange;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.permission.IProfilePermission;
import xyz.refinedev.phoenix.profile.permission.ProfilePermission;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentMessages;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.EncryptedPasteBuilder;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.GrantUtil;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.NameTagUtils;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Help
@Register(name = "user", aliases = "u")
@Requires("core.command.user")
public class UserCommand {

    private final Phoenix plugin;

    private static final long time = -5L;

    @Command(name = "disguise", description = "Disguise a player as a rank")
    @Requires("core.command.user.disguise")
    public void disguise(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) Player player,
            @Optional IRank rank) {
        String text =
                sender instanceof Player
                        ? this.plugin
                                .getProfileHandler()
                                .getCachedProfile(((Player) sender).getUniqueId())
                                .getNameWithColorNoDisguise()
                        : GlobalConfig.SERVER.CONSOLE_NAME;
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.ERROR.PROFILE_NOT_LOADED));
        }
        if (rank == null) {
            profile.getDisguiseData().setRankId(null);
            this.plugin
                    .getPacketHandler()
                    .send(
                            new SendToStaffPacket(
                                    MessageConfig.STAFF.RANK_UNDISGUISE_ALERT,
                                    LocaleUtil.replacements(
                                            "<player>",
                                            text,
                                            "<target>",
                                            profile.getNameWithColor(),
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName()),
                                    true,
                                    false));
        } else {
            profile.getDisguiseData().setRankId(rank.getUuid());
            this.plugin
                    .getPacketHandler()
                    .send(
                            new SendToStaffPacket(
                                    MessageConfig.STAFF.RANK_DISGUISE_ALERT,
                                    LocaleUtil.replacements(
                                            "<player>",
                                            text,
                                            "<target>",
                                            profile.getNameWithColor(),
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName(),
                                            "<rank>",
                                            Color.translate(rank.getDisplayName())),
                                    true,
                                    false));
        }
        profile.saveProfile();
        NameTagUtils.applyPlayer(player);
    }

    @Command(name = "clearcooldowns", description = "Clear a player's cooldowns")
    @Requires("core.command.user.clearcooldowns")
    public void clearcooldowns(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        int value =
                profile.getGlobalCooldowns().size()
                        + this.plugin.getCooldownHandler().getCooldownCount(profile.getUuid());
        this.plugin.getCooldownHandler().clearCooldowns(profile.getUuid());
        profile.getGlobalCooldowns().clear();
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_CLEAR_MESSAGE)
                                .replace("<type>", value != 1 ? "cooldowns" : "cooldown")
                                .replace("<size>", String.valueOf(value))
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
        profile.saveAndUpdate();
    }

    @RequireSecurity
    @Command(name = "clearips", description = "Clear a player's encrypted IPs")
    @Requires("core.command.user.clearips")
    public void clearips(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        int size2 = profile.getKnownAddresses().size();
        profile.getKnownAddresses().clear();
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_CLEAR_MESSAGE)
                                .replace("<type>", size2 != 1 ? "IPs" : "IP")
                                .replace("<size>", String.valueOf(size2))
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
        profile.saveAndUpdate();
    }

    @Command(name = "clearalts", description = "Clear a player's alts", async = true)
    @Requires("core.command.user.clearalts")
    public void clearalts(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Default("true") @Named("recursive [true/false]") boolean matches) {
        int size2 = profile.getAlts().size();
        if (matches) {
            for (UUID uUID : (Iterable<UUID>) ((Iterable) new ArrayList<>(profile.getAlts()))) {
                Tasks.runAsync(
                        () -> {
                            IProfile profile2;
                            if ((profile2 = this.plugin.getProfileHandler().getProfile(uUID))
                                    != null) {
                                profile2.getAlts().clear();
                                profile2.saveAndUpdateSync(false);
                            }
                        });
            }
        }
        profile.getAlts().clear();
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_CLEAR_MESSAGE)
                                .replace("<type>", size2 != 1 ? "alts" : "alt")
                                .replace("<size>", String.valueOf(size2))
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
        profile.saveAndUpdate();
    }

    @RequireSecurity
    @Command(name = "cleargrants", description = "Clear a player's grants", async = true)
    @Requires("core.command.user.cleargrants")
    public void cleargrants(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        if (GrantUtil.canEdit(sender, profile)) {
            List<IGrant> values;
            for (IGrant grant :
                    values = this.plugin.getGrantHandler().getGrants(profile.getUuid())) {
                this.plugin.getGrantHandler().getRepository().deleteSync(grant.getUuid());
            }
            this.plugin
                    .getPacketHandler()
                    .sendSync(new BukkitGrantsUpdatePacket(profile.getUuid()));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_CLEAR_MESSAGE)
                                    .replace("<type>", values.size() != 1 ? "grants" : "grant")
                                    .replace("<size>", String.valueOf(values.size()))
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        }
    }

    @Command(name = "clearpunishments", description = "Clear a player's punishments", async = true)
    @Requires("core.command.user.clearpunishments")
    public void clearpunishments(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        List<IPunishment> values;
        for (IPunishment punishment :
                values = this.plugin.getPunishmentHandler().getPunishments(profile.getUuid())) {
            this.plugin.getPunishmentHandler().getRepository().deleteSync(punishment.getUuid());
        }
        this.plugin.getPacketHandler().sendSync(new PunishmentsUpdatePacket(profile.getUuid()));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_CLEAR_MESSAGE)
                                .replace(
                                        "<type>", values.size() != 1 ? "punishments" : "punishment")
                                .replace("<size>", String.valueOf(values.size()))
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
    }

    @Command(
            name = "wipe kicks",
            description = "Delete every kick on record, for every player on the network",
            async = true)
    @Requires("core.command.wipekicks")
    public void wipeKicks(
            @Sender ConsoleCommandSender consoleCommandSender, @Flag("confirm") boolean matches) {
        PunishmentMessages.sendMessage(
                this.plugin,
                consoleCommandSender,
                PunishmentType.KICK,
                MessageConfig.PUNISHMENT.KICKS_WIPED,
                "/user wipe kicks",
                matches);
    }

    @Command(
            name = "wipe warns",
            description = "Delete every warn on record, for every player on the network",
            async = true)
    @Requires("core.command.wipewarns")
    public void wipeWarns(
            @Sender ConsoleCommandSender consoleCommandSender, @Flag("confirm") boolean matches) {
        PunishmentMessages.sendMessage(
                this.plugin,
                consoleCommandSender,
                PunishmentType.WARN,
                MessageConfig.PUNISHMENT.WARNS_WIPED,
                "/user wipe warns",
                matches);
    }

    @RequireSecurity
    @Command(
            name = "purge",
            aliases = "delete",
            description = "Erase everything about a player, moderation history included",
            async = true)
    @Requires("core.command.user.delete")
    public void purge(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        for (IGrant grant : this.plugin.getGrantHandler().getGrants(profile.getUuid())) {
            this.plugin.getGrantHandler().getRepository().deleteSync(grant.getUuid());
        }
        for (IPunishment punishment :
                this.plugin.getPunishmentHandler().getPunishments(profile.getUuid())) {
            this.plugin.getPunishmentHandler().getRepository().deleteSync(punishment.getUuid());
        }
        this.plugin.getProfileHandler().getCacheRepository().delete(profile.getUniqueId());
        this.plugin.getProfileHandler().getRepository().deleteSync(profile.getUniqueId());
        this.plugin.getSecurityHandler().removeSecurity(profile.getUuid());
        this.plugin
                .getCustomTagHandler()
                .getRepository()
                .deleteSync(Filters.eq("creator", profile.getUniqueId().toString()));
        this.plugin
                .getDisguiseHandler()
                .getHistoryRepository()
                .deleteSync(Filters.eq("player", profile.getUniqueId().toString()));
        this.plugin
                .getChatSnapshotHandler()
                .getRepository()
                .deleteSync(Filters.eq("requestedBy", profile.getUniqueId().toString()));
        this.plugin
                .getLoginHandler()
                .getRepository()
                .deleteSync(Filters.eq("target", profile.getUniqueId().toString()));
        this.plugin.getChatLogHandler().clearLogs(profile.getUuid());
        this.plugin.getPacketHandler().send(new BukkitGrantsUpdatePacket(profile.getUuid()));
        this.plugin.getPacketHandler().send(new PunishmentsUpdatePacket(profile.getUuid()));
        this.plugin.getPacketHandler().send(new ProfileDeletePacket(profile));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.STAFF.DELETED)
                                .replace("<target>", profile.getNameWithColorNoDisguise())));
    }

    @RequireSecurity
    @Command(
            name = "gdpr delete",
            description =
                    "Delete a user's data compliant with the GDPR. (Keeps punishments and other moderation data)",
            async = true)
    @Requires("core.command.user.gdpr.delete")
    public void gdprDelete(
            @Sender Player player,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Flag("export-data") boolean matches) {
        if (this.plugin.getNetworkHandler().isOnline(profile.getUniqueId())) {
            Punishment punishment;
            (punishment = new Punishment()).setPunishmentType(PunishmentType.KICK);
            punishment.setTarget(profile.getUniqueId());
            punishment.setAddress(profile.getCurrentAddress());
            punishment.setSilent(true);
            punishment.setPermanent(true);
            punishment.setDuration(time);
            punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
            punishment.setIssuedAt(System.currentTimeMillis());
            punishment.setReason("Data Deletion");
            punishment.setIssuedBy(player.getUniqueId());
            this.plugin.getPacketHandler().sendSync(new BukkitPunishmentPacket(punishment));
        }
        ConfirmMenu confirmMenu =
                new ConfirmMenu(
                        "GDPR Delete",
                        result -> {
                            Tasks.runAsync(
                                    () -> {
                                        if (!result) {
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig.STAFF
                                                                                    .DELETE_CANCELLED)
                                                                    .replace(
                                                                            "<player>",
                                                                            profile
                                                                                    .getNameWithColorNoDisguise())));
                                        } else {
                                            if (matches) {
                                                this.sendGrants(player, profile, false);
                                            }
                                            for (IGrant grant :
                                                    this.plugin
                                                            .getGrantHandler()
                                                            .getGrants(profile.getUuid())) {
                                                this.plugin
                                                        .getGrantHandler()
                                                        .getRepository()
                                                        .deleteSync(grant.getUuid());
                                            }
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getCacheRepository()
                                                    .delete(profile.getUniqueId());
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getRepository()
                                                    .deleteSync(profile.getUniqueId());
                                            this.plugin
                                                    .getSecurityHandler()
                                                    .removeSecurity(profile.getUuid());
                                            this.plugin
                                                    .getCustomTagHandler()
                                                    .getRepository()
                                                    .deleteSync(
                                                            Filters.eq(
                                                                    "creator",
                                                                    profile.getUniqueId()
                                                                            .toString()));
                                            this.plugin
                                                    .getDisguiseHandler()
                                                    .getHistoryRepository()
                                                    .deleteSync(
                                                            Filters.eq(
                                                                    "player",
                                                                    profile.getUniqueId()
                                                                            .toString()));
                                            this.plugin
                                                    .getChatSnapshotHandler()
                                                    .getRepository()
                                                    .deleteSync(
                                                            Filters.eq(
                                                                    "requestedBy",
                                                                    profile.getUniqueId()
                                                                            .toString()));
                                            this.plugin
                                                    .getLoginHandler()
                                                    .getRepository()
                                                    .deleteSync(
                                                            Filters.eq(
                                                                    "target",
                                                                    profile.getUniqueId()
                                                                            .toString()));
                                            this.plugin
                                                    .getChatLogHandler()
                                                    .clearLogs(profile.getUuid());
                                            this.plugin
                                                    .getPacketHandler()
                                                    .send(
                                                            new BukkitGrantsUpdatePacket(
                                                                    profile.getUuid()));
                                            this.plugin
                                                    .getPacketHandler()
                                                    .send(new ProfileDeletePacket(profile));
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig.STAFF
                                                                                    .DELETED)
                                                                    .replace(
                                                                            "<target>",
                                                                            profile
                                                                                    .getNameWithColorNoDisguise())));
                                        }
                                    });
                        },
                        true,
                        new Button() {

                            @Override
                            public ItemStack getItem(Player player) {
                                return new ItemBuilder(XMaterial.OAK_SIGN)
                                        .setName("&cGDPR Delete Request")
                                        .addLoreLine("&7Click to GDPR delete all relevant data for")
                                        .addLoreLine("&7this user except punishments.")
                                        .toItemStack();
                            }
                        });
        this.plugin.getMenuHandler().openMenuSync(confirmMenu, player);
    }

    @RequireSecurity
    @Command(
            name = "gdpr export",
            description = "Export a user's data compliant with the GDPR",
            async = true)
    @Requires("core.command.user.gdpr.export")
    public void gdprExport(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Flag("file") boolean matches) {
        this.sendGrants(sender, profile, matches);
    }

    private void sendGrants(CommandSender sender, IProfile profile, boolean matches) {
        JsonObject jsonObject;
        (jsonObject = new JsonObject())
                .add(
                        "grants",
                        this.plugin
                                .getGson()
                                .toJsonTree(
                                        this.plugin
                                                .getGrantHandler()
                                                .getGrants(profile.getUuid())));
        jsonObject.add(
                "punishments",
                this.plugin
                        .getGson()
                        .toJsonTree(
                                this.plugin
                                        .getPunishmentHandler()
                                        .getPunishments(profile.getUuid())));
        jsonObject.add(
                "customTags",
                this.plugin
                        .getGson()
                        .toJsonTree(
                                this.plugin
                                        .getCustomTagHandler()
                                        .getRepository()
                                        .fetchAllSync(
                                                Filters.eq(
                                                        "creator",
                                                        profile.getUniqueId().toString()))));
        jsonObject.add(
                "disguiseHistory",
                this.plugin
                        .getGson()
                        .toJsonTree(
                                this.plugin
                                        .getDisguiseHandler()
                                        .getHistoryRepository()
                                        .fetchAllSync(
                                                Filters.eq(
                                                        "player",
                                                        profile.getUniqueId().toString()))));
        jsonObject.add(
                "chatSnapshots",
                this.plugin
                        .getGson()
                        .toJsonTree(
                                this.plugin
                                        .getChatSnapshotHandler()
                                        .getRepository()
                                        .fetchAllSync(
                                                Filters.eq(
                                                        "requestedBy",
                                                        profile.getUniqueId().toString()))));
        jsonObject.add(
                "chatLogs",
                this.plugin
                        .getGson()
                        .toJsonTree(
                                this.plugin
                                        .getChatLogHandler()
                                        .getRepository()
                                        .fetchAllSync(
                                                Filters.eq(
                                                        "sender",
                                                        profile.getUniqueId().toString()))));
        jsonObject.add(
                "logins",
                this.plugin
                        .getGson()
                        .toJsonTree(
                                this.plugin
                                        .getLoginHandler()
                                        .getRepository()
                                        .fetchAllSync(
                                                Filters.eq(
                                                        "target",
                                                        profile.getUniqueId().toString()))));
        if (this.plugin.getSecurityHandler().hasSecurity(profile.getUuid())) {
            jsonObject.add(
                    "security",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin
                                            .getSecurityHandler()
                                            .getRepository()
                                            .fetchSync(profile.getUuid())
                                            .get()));
        }
        jsonObject.add("profile", this.plugin.getGson().toJsonTree(profile));
        if (matches) {
            this.sendMessage(sender, profile, this.plugin.getGson().toJson(jsonObject));
        } else {
            String text = profile.getUniqueId().toString() + Utilities.getRandomSaltedString(5);
            EncryptedPasteBuilder encryptedPasteBuilder;
            (encryptedPasteBuilder = new EncryptedPasteBuilder())
                    .findEncryptedPasteBuilderByTextString(
                            this.plugin.getGson().toJson(jsonObject));
            encryptedPasteBuilder.findEncryptedPasteBuilder(text);
            encryptedPasteBuilder.getEncryptedPasteBuilder();
            try {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.STAFF.EXPORT_PASTED)
                                        .replace("<link>", encryptedPasteBuilder.getString())));
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.STAFF.EXPORT_PASSWORD)
                                        .replace("<password>", text)));
            } catch (Exception exception) {
                sender.sendMessage(
                        Color.translate(LocaleUtil.tr(sender, MessageConfig.ERROR.PASTE_FAILED)));
                Phoenix.getInstance()
                        .getLogger()
                        .log(
                                Level.SEVERE,
                                "An error occurred while creating a gdpr export paste:",
                                exception);
            }
        }
    }

    private void sendMessage(CommandSender sender, IProfile profile, String text) {
        File file;
        if (!(file = new File("plugins/Phoenix/exports")).exists() && !file.mkdirs()) {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.STAFF.EXPORT_FAILED)));
        } else {
            File file2 =
                    new File(file, profile.getName() + "-" + System.currentTimeMillis() + ".json");
            try {
                Files.write(file2.toPath(), text.getBytes(StandardCharsets.UTF_8));
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.STAFF.EXPORT_WRITTEN)
                                        .replace("<path>", file2.getPath())));
            } catch (Exception exception) {
                sender.sendMessage(
                        Color.translate(LocaleUtil.tr(sender, MessageConfig.STAFF.EXPORT_FAILED)));
                Phoenix.getInstance()
                        .getLogger()
                        .log(
                                Level.SEVERE,
                                "An error occurred while writing a gdpr export:",
                                exception);
            }
        }
    }

    @Command(name = "info", description = "View a users information", async = true)
    @Requires("core.command.user.info")
    public void info(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        List<IPunishment> punishments =
                this.plugin.getPunishmentHandler().getPunishments(profile.getUniqueId());
        List<IPunishment> activePunishments =
                this.plugin.getPunishmentHandler().getActivePunishments(profile.getUniqueId());
        List<ILogin> logins = this.plugin.getLoginHandler().getLogins(profile.getUniqueId());
        Set values =
                FastStreams.mapArrayListToSet(
                        profile.getPermissions(), IProfilePermission::getPermission);
        Iterator<String> iterator2 =
                LocaleUtil.tr(sender, MessageConfig.STAFF.USER_INFO).iterator();
        while (iterator2.hasNext()) {
            String text =
                    ((String) iterator2.next())
                            .replace("<uuid>", profile.getUniqueId().toString())
                            .replace("<nameHistory>", this.formatString(profile))
                            .replace(
                                    "<activeGrants>",
                                    String.valueOf(profile.getActiveGrants().size()))
                            .replace(
                                    "<activePunishments>", String.valueOf(activePunishments.size()))
                            .replace("<totalPunishments>", String.valueOf(punishments.size()))
                            .replace("<player>", profile.getNameWithColorNoDisguise())
                            .replace(
                                    "<rank>",
                                    Color.translate(profile.getGeneralRank().getDisplayName()))
                            .replace("<permissions>", StringUtils.getStringFromList(values))
                            .replace("<lastSeen>", profile.getLastSeenAgo(logins))
                            .replace("<logins>", String.valueOf(logins.size()))
                            .replace("<firstLogin>", profile.getFirstLogin(logins))
                            .replace("<lastLogin>", profile.getLastLogin(logins))
                            .replace(
                                    "<server>",
                                    this.formatString(profile.getUniqueId())
                                            + this.formatStringByPlugin(profile.getUniqueId()));
            sender.sendMessage(Color.translate(text));
        }
    }

    private String formatString(IProfile profile) {
        if (profile.getNameHistory().isEmpty()) {
            return "None";
        }
        ArrayList<String> values = new ArrayList<>();
        for (INameChange nameChange : profile.getNameHistory()) {
            values.add(
                    nameChange.getName()
                            + " &7(&f"
                            + DateUtils.getDate(nameChange.getChangedAt())
                            + "&7)");
        }
        return String.join("&7, <main>", values);
    }

    @Command(name = "staffinfo", description = "View a users staff information", async = true)
    @Requires("core.command.user.staffinfo")
    public void staffinfo(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        List<IPunishment> values =
                this.plugin.getPunishmentHandler().findByIssuedBy(profile.getUniqueId()).join();
        List<IPunishment> values2 =
                this.plugin.getPunishmentHandler().findByRemovedBy(profile.getUniqueId()).join();
        List<ILogin> logins = this.plugin.getLoginHandler().getLogins(profile.getUniqueId());
        HashMap entries = new HashMap();
        long value = logins.isEmpty() ? 0L : profile.getMostRecentLogin(logins).getLogin();
        long value2 = logins.isEmpty() ? 0L : ((ILogin) logins.get(0)).getLogin();
        LocalDate localDate = new Timestamp(value2).toLocalDateTime().toLocalDate();
        IntStream intStream =
                IntStream.iterate(0, result -> result + 1)
                        .limit(TimeUnit.DAYS.convert(value - value2, TimeUnit.MILLISECONDS) + 1L);
        for (LocalDate localDate2 :
                (Iterable<LocalDate>)
                        ((Iterable)
                                intStream
                                        .mapToObj(localDate::plusDays)
                                        .collect(Collectors.toList()))) {
            entries.put(localDate2, 0);
        }
        for (ILogin login : logins) {
            LocalDate localDate3 = new Timestamp(login.getLogin()).toLocalDateTime().toLocalDate();
            entries.putIfAbsent(localDate3, 0);
            entries.put(localDate3, (Integer) entries.get(localDate3) + 1);
        }
        ArrayList values3 = new ArrayList();
        for (ILogin login2 : logins) {
            values3.add(login2.getDuration());
        }
        long value3 = 0L;
        for (Long value4 : (Iterable<Long>) ((Iterable) values3)) {
            value3 += value4;
        }
        double value5 = logins.isEmpty() ? 0.0 : (double) logins.size() / entries.keySet().size();
        long value6 = logins.isEmpty() ? 0L : value3 / values3.size();
        int size2 =
                FastStreams.filterArrayList(
                                values,
                                result -> result.getPunishmentType().equals(PunishmentType.MUTE))
                        .size();
        int size3 =
                FastStreams.filterArrayList(
                                values,
                                result -> result.getPunishmentType().equals(PunishmentType.BAN))
                        .size();
        int size4 =
                FastStreams.filterArrayList(
                                values,
                                result ->
                                        result.getPunishmentType().equals(PunishmentType.BLACKLIST))
                        .size();
        int size5 =
                FastStreams.filterArrayList(
                                values2,
                                result -> result.getPunishmentType().equals(PunishmentType.MUTE))
                        .size();
        int size6 =
                FastStreams.filterArrayList(
                                values2,
                                result -> result.getPunishmentType().equals(PunishmentType.BAN))
                        .size();
        int size7 =
                FastStreams.filterArrayList(
                                values2,
                                result ->
                                        result.getPunishmentType().equals(PunishmentType.BLACKLIST))
                        .size();
        Iterator<String> iterator2 =
                LocaleUtil.tr(sender, MessageConfig.STAFF.USER_STAFF_INFO).iterator();
        while (iterator2.hasNext()) {
            String text =
                    ((String) iterator2.next())
                            .replace("<uuid>", profile.getUniqueId().toString())
                            .replace("<player>", profile.getNameWithColorNoDisguise())
                            .replace(
                                    "<rank>",
                                    Color.translate(profile.getGeneralRank().getDisplayName()))
                            .replace("<lastSeen>", profile.getLastSeenAgo(logins))
                            .replace("<logins>", String.valueOf(logins.size()))
                            .replace("<firstLogin>", profile.getFirstLogin(logins))
                            .replace("<lastLogin>", profile.getLastLogin(logins))
                            .replace("<dailyAverage>", String.format("%.1f", value5))
                            .replace("<timeAverage>", DateUtils.formatTimeMillis(value6))
                            .replace("<executed>", String.valueOf(values.size()))
                            .replace("<removed>", String.valueOf(values2.size()))
                            .replace("<mutes>", String.valueOf(size2))
                            .replace("<bans>", String.valueOf(size3))
                            .replace("<blacklists>", String.valueOf(size4))
                            .replace("<bansRemoved>", String.valueOf(size6))
                            .replace("<mutesRemoved>", String.valueOf(size5))
                            .replace("<blacklistsRemoved>", String.valueOf(size7));
            sender.sendMessage(Color.translate(text));
        }
    }

    @Command(name = "perm add", description = "Add a permission to a player")
    @Requires("core.command.user.addperm")
    public void permAdd(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Named("permission") String text,
            @Named("duration") Duration duration,
            @Optional @Named("scopes") String text2) {
        if (profile.hasActivePrivatePermission(text)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.GENERAL.ALREADY_HAVE_PERMISSION)
                            .replace("<player>", profile.getPlayerName())
                            .replace("<permission>", text));
        }
        long timeTime = duration.getTimeTime();
        String text3 = duration.formatTimeMillis();
        ProfilePermission profilePermission = new ProfilePermission(text, timeTime);
        if (text2 != null) {
            if (!IScope.isValidMultiple(text2)) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.ERROR.INVALID_SCOPES)
                                .replace("<input>", text2));
            }
            List<IScope> values = Scope.findList(text2);
            profilePermission.setScopes(values);
        }
        profile.removePermission(text);
        profile.getPermissions().add(profilePermission);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.PERMISSION_ADDED)
                                .replace("<player>", profile.getPlayerName())
                                .replace("<permission>", text)
                                .replace("<duration>", text3)
                                .replace("<state>", text.startsWith("-") ? "&cfalse" : "&atrue")));
        profile.saveAndUpdate(true);
    }

    @Command(name = "perm mass-add", description = "Mass add a permission to a player")
    @Requires("core.command.user.massaddperm")
    public void permMassAdd(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Named("permission (Split with ;)") String text,
            @Named("duration") Duration duration,
            @Optional @Named("scopes") String text2) {
        if (!text.contains(";")) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.ERROR.SPLIT_PERMISSIONS));
        }
        if (text2 != null && !IScope.isValidMultiple(text2)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.ERROR.INVALID_SCOPES)
                            .replace("<input>", text2));
        }
        long timeTime = duration.getTimeTime();
        String text3 = duration.formatTimeMillis();
        List<IScope> values = text2 != null ? Scope.findList(text2) : null;
        String[] texts;
        int value = (texts = text.split(";")).length;
        for (int index = 0; index < value; index++) {
            String text4 = texts[index];
            if (profile.hasActivePrivatePermission(text4)) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.GENERAL.ALREADY_HAVE_PERMISSION)
                                        .replace("<player>", profile.getPlayerName())
                                        .replace("<permission>", text)));
            } else {
                ProfilePermission profilePermission;
                (profilePermission = new ProfilePermission(text4, timeTime))
                        .setScopes(
                                values != null
                                        ? values
                                        : Collections.singletonList(Scope.scopeScope));
                profile.removePermission(text4);
                profile.getPermissions().add(profilePermission);
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.GENERAL.PERMISSION_ADDED)
                                        .replace("<player>", profile.getPlayerName())
                                        .replace("<permission>", text4)
                                        .replace("<duration>", text3)
                                        .replace(
                                                "<state>",
                                                text4.startsWith("-") ? "&cfalse" : "&atrue")));
            }
        }
        profile.saveAndUpdate(true);
    }

    @Command(name = "perm delete", description = "Remove a permission from a player")
    @Requires("core.command.user.removeperm")
    public void permDelete(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Named("permission") String text) {
        if (!profile.hasPrivatePermission(text)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.GENERAL.DONT_HAVE_PERMISSION)
                            .replace("<player>", profile.getPlayerName())
                            .replace("<permission>", text));
        }
        profile.removePermission(text);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.PERMISSION_REMOVED)
                                .replace("<player>", profile.getPlayerName())
                                .replace("<permission>", text)));
        profile.updatePermissions();
        profile.saveAndUpdate(true);
    }

    @RequireSecurity
    @Command(name = "perm clear", description = "Clear a player's permissions")
    @Requires("core.command.user.clearperm")
    public void permClear(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        profile.getPermissions().clear();
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.PERMISSION_CLEARED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
        profile.updatePermissions();
        profile.saveAndUpdate(true);
    }

    @Command(name = "perm search", description = "Search for a permission", async = true)
    @Requires("core.command.user.searchperm")
    public void permSearch(
            @Sender CommandSender sender,
            @Named("permission") String text,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Inheritors", text, "/user perm search " + text);
        ArrayList<Clickable> values = new ArrayList<>();
        MongoDatabaseHandler mongoDatabaseHandler;
        if ((mongoDatabaseHandler = MongoDatabaseHandler.orNull(this.plugin.getDatabaseHandler()))
                == null) {
            throw new ExitMessage("Permission search is MongoDB-only.");
        }
        Document document =
                new Document(
                        "permissions",
                        new Document("$elemMatch", new Document("permission", text)));
        MongoCursor mongoCursor = mongoDatabaseHandler.getProfiles().find(document).iterator();
        while (mongoCursor.hasNext()) {
            Document document2 = (Document) mongoCursor.next();
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(document2.getString("name"));
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "perm list", description = "View permissions of player")
    @Requires("core.command.user.listperm")
    public void permList(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Permissions", null, "/user perm list " + profile.getName());
        ArrayList<Clickable> values = new ArrayList<>();
        for (IProfilePermission profilePermission : profile.getPermissions()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(
                    profilePermission.getPermission() + " &7" + profilePermission.getNiceScopes());
            clickable.add(
                    profilePermission.hasExpired()
                            ? " &c(Expired)"
                            : " &7("
                                    + (profilePermission.isPermanent()
                                            ? TimeConstants.PERMANENT
                                            : DateUtils.formatTimeMillis(
                                                    profilePermission.getExpiration()
                                                            - System.currentTimeMillis()))
                                    + ")");
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    public String formatString(UUID uUID) {
        IServerData serverData;
        return (serverData = this.plugin.getNetworkHandler().getPresence(uUID)) != null
                ? serverData.getServerName()
                : "Offline";
    }

    public String formatStringByPlugin(UUID uUID) {
        IServerData serverData;
        return (serverData = this.plugin.getNetworkHandler().getProxy(uUID)) != null
                ? " &7[" + serverData.getServerName() + "]"
                : "";
    }

    public UserCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

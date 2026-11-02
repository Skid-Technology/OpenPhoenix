package xyz.refinedev.phoenix.handler;

import litebans.api.Database;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.Context;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoDatabase;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitGrantsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.ServerCommandPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.RedisProfile;
import xyz.refinedev.phoenix.profile.grant.Grant;
import xyz.refinedev.phoenix.profile.permission.ProfilePermission;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.Rank;
import xyz.refinedev.phoenix.rank.permission.Permission;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.server.ServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ImportHandler {

    private final CommonPlatform platform;

    private boolean enabled;

    public void sendMessage(Phoenix phoenix, CommandSender sender) {
        if (this.enabled) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.PHOENIX.IMPORT.ALREADY_STARTED)));
        } else {
            this.enabled = true;
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                ((Player) iterator2.next()).kickPlayer(Color.translate("&cImporting..."));
            }
            MongoDatabaseHandler mongoDatabaseHandler;
            if ((mongoDatabaseHandler =
                            MongoDatabaseHandler.orNull(this.platform.getDatabaseHandler()))
                    == null) {
                sender.sendMessage(Color.translate("&cLiteBans import is MongoDB-only."));
                this.enabled = false;
            } else {
                mongoDatabaseHandler.getPunishments().drop();
                this.platform.getPunishmentHandler().getRepository().getListCache().invalidateAll();
                Tasks.runAsync(
                        () -> {
                            HashMap entries = new HashMap();
                            try {
                                phoenix.getLogger().info("Fetching uuid cache...");
                                ResultSet resultSet =
                                        Database.get()
                                                .prepareStatement(
                                                        "SELECT name, uuid FROM {history}")
                                                .executeQuery();
                                while (resultSet.next()) {
                                    try {
                                        UUID.fromString(resultSet.getString("uuid"));
                                    } catch (Exception exception) {
                                        continue;
                                    }
                                    UUID uUID = UUID.fromString(resultSet.getString("uuid"));
                                    String string = resultSet.getString("name");
                                    entries.put(uUID, string);
                                }
                                phoenix.getLogger()
                                        .info(
                                                "Finished fetching uuid cache ("
                                                        + entries.size()
                                                        + ").");
                            } catch (Exception exception2) {
                                phoenix.getLogger()
                                        .info(
                                                "Error fetching uuid cache: "
                                                        + ErrorReporter.formatString(exception2));
                                return;
                            }
                            this.applyPhoenix(phoenix, PunishmentType.BAN, entries, "bans");
                            this.applyPhoenix(phoenix, PunishmentType.MUTE, entries, "mutes");
                            this.applyPhoenix(phoenix, PunishmentType.KICK, entries, "kicks");
                            this.applyPhoenix(phoenix, PunishmentType.WARN, entries, "warnings");
                            Bukkit.getConsoleSender()
                                    .sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                            Bukkit.getConsoleSender(),
                                                            MessageConfig.PHOENIX
                                                                    .IMPORT
                                                                    .FINISHED)));
                            this.platform
                                    .getPacketHandler()
                                    .sendSync(new ServerCommandPacket("all", "stop", "CONSOLE"));
                            this.enabled = false;
                        });
            }
        }
    }

    public void sendMessageByEnabled(Phoenix phoenix, CommandSender sender) {
        if (this.enabled) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.PHOENIX.IMPORT.ALREADY_STARTED)));
        } else {
            this.enabled = true;
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                ((Player) iterator2.next()).kickPlayer(Color.translate("&cImporting..."));
            }
            CompletableFuture<Void> completableFuture =
                    CompletableFuture.runAsync(() -> this.applyPhoenixByScopeScope(phoenix));
            completableFuture =
                    completableFuture.whenCompleteAsync(
                            (result, result2) -> this.applyPhoenix(phoenix));
            completableFuture.whenCompleteAsync(
                    (result3, result) -> {
                        Bukkit.getConsoleSender()
                                .sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        Bukkit.getConsoleSender(),
                                                        MessageConfig.PHOENIX.IMPORT.FINISHED)));
                        this.platform
                                .getPacketHandler()
                                .sendSync(new ServerCommandPacket("all", "stop", "CONSOLE"));
                        this.enabled = false;
                    });
        }
    }

    public void applyPhoenix(
            Phoenix phoenix,
            PunishmentType punishmentType,
            Map<UUID, String> entries,
            String text) {
        try {
            phoenix.getLogger().info("Importing " + punishmentType.getName() + "...");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM HH-mm-ss.SSSSSS");
            ResultSet resultSet =
                    Database.get().prepareStatement("SELECT * FROM {" + text + "}").executeQuery();
            while (resultSet.next()) {
                if (!resultSet.getString("uuid").equalsIgnoreCase("#offline#")) {
                    UUID uUID = UUID.fromString(resultSet.getString("uuid"));
                    UUID uUID2 =
                            resultSet.getString("banned_by_uuid").toLowerCase().contains("console")
                                    ? IProfile.getConsoleUuid()
                                    : UUID.fromString(resultSet.getString("banned_by_uuid"));
                    String string = resultSet.getString("banned_by_name");
                    String string2 = resultSet.getString("server_origin");
                    String string3 = resultSet.getString("reason");
                    String text2 =
                            resultSet.getString("ip") == null
                                    ? ""
                                    : this.platform
                                            .getHashHandler()
                                            .hash(resultSet.getString("ip"));
                    boolean booleanValue = resultSet.getBoolean("active");
                    boolean booleanValue2 = resultSet.getBoolean("ipban");
                    boolean booleanValue3 = resultSet.getBoolean("silent");
                    long longValue = resultSet.getLong("time");
                    long value;
                    long value2 =
                            (value = resultSet.getLong("until")) == -1L ? -5L : value - longValue;
                    if (this.platform.getProfileHandler().getProfile(uUID2) == null) {
                        RedisProfile redisProfile;
                        (redisProfile = new RedisProfile(uUID2)).setName(string);
                        this.platform
                                .getProfileHandler()
                                .getRepository()
                                .saveSync(uUID2, redisProfile);
                    }
                    if (entries.containsKey(uUID)
                            && this.platform.getProfileHandler().getProfile(uUID) == null) {
                        RedisProfile redisProfile2;
                        (redisProfile2 = new RedisProfile(uUID))
                                .setName((String) entries.get(uUID));
                        this.platform
                                .getProfileHandler()
                                .getRepository()
                                .saveSync(uUID, redisProfile2);
                    }
                    Punishment punishment;
                    (punishment = new Punishment()).setPunishmentType(punishmentType);
                    punishment.setTarget(uUID);
                    punishment.setAddress(text2);
                    punishment.setSilent(booleanValue3);
                    punishment.setPermanent(value2 == -5L);
                    punishment.setDuration(value2);
                    punishment.setIp(booleanValue2);
                    punishment.setIssuedOn(string2);
                    punishment.setIssuedAt(longValue);
                    punishment.setReason(string3);
                    punishment.setIssuedBy(uUID2);
                    punishment.setActive(booleanValue);
                    if (!booleanValue && punishmentType != PunishmentType.KICK) {
                        UUID uUID3;
                        if ((uUID3 =
                                        resultSet.getString("removed_by_uuid") == null
                                                ? null
                                                : (resultSet
                                                                .getString("removed_by_uuid")
                                                                .toLowerCase()
                                                                .contains("console")
                                                        ? IProfile.getConsoleUuid()
                                                        : UUID.fromString(
                                                                resultSet.getString(
                                                                        "removed_by_uuid"))))
                                != null) {
                            String string4 = resultSet.getString("removed_by_name");
                            if (this.platform.getProfileHandler().getProfile(uUID3) == null
                                    && string4 != null) {
                                RedisProfile redisProfile3;
                                (redisProfile3 = new RedisProfile(uUID3)).setName(string4);
                                this.platform
                                        .getProfileHandler()
                                        .getRepository()
                                        .saveSync(uUID3, redisProfile3);
                            }
                        }
                        String string5 = resultSet.getString("removed_by_reason");
                        punishment.setRemovedBy(uUID3);
                        punishment.setRemovedReason(string5);
                        punishment.setRemovedSilent(false);
                        try {
                            String string6 = resultSet.getString("removed_by_date");
                            Date date = simpleDateFormat.parse(string6);
                            punishment.setRemovedAt(date.getTime());
                        } catch (Exception exception) {
                            punishment.setRemovedAt(System.currentTimeMillis());
                        }
                        punishment.setRemovedOn("LiteBans (N/A)");
                    }
                    this.platform
                            .getPunishmentHandler()
                            .getRepository()
                            .saveSync(punishment.getUuid(), punishment);
                }
            }
            phoenix.getLogger().info("Finished importing " + punishmentType.getName() + ".");
        } catch (SQLException sQLException) {
            phoenix.getLogger()
                    .info(
                            "Error while importing "
                                    + punishmentType.getName()
                                    + ": "
                                    + ErrorReporter.formatString(sQLException));
        }
    }

    public void applyPhoenix(Phoenix phoenix) {
        phoenix.getLogger().info("Importing users...");
        this.platform.getGrantHandler().getRepository().deleteAll();
        this.platform.getGrantHandler().getRepository().getListCache().invalidateAll();
        UserManager userManager;
        Set<UUID> values =
                (Set)
                        (userManager = LuckPermsProvider.get().getUserManager())
                                .getUniqueUsers()
                                .join();
        phoenix.getLogger().info("Found " + values.size() + " users...");
        for (UUID uUID : (Iterable<UUID>) ((Iterable) values)) {
            User user = (User) userManager.loadUser(uUID).join();
            if (this.platform.getProfileHandler().getProfile(uUID) == null) {
                String text = (String) userManager.lookupUsername(uUID).join();
                RedisProfile redisProfile;
                (redisProfile = new RedisProfile(uUID))
                        .setName(text != null ? text : uUID.toString());
                Iterator iterator2 = user.getNodes(NodeType.PERMISSION).iterator();
                while (iterator2.hasNext()) {
                    PermissionNode permissionNode;
                    String permission =
                            (permissionNode = (PermissionNode) iterator2.next()).getPermission();
                    permission = permissionNode.isNegated() ? "-" + permission : permission;
                    if (!redisProfile.hasPrivatePermission(permission)) {
                        ArrayList values2 = new ArrayList();
                        Iterator<Context> iterator3 = permissionNode.getContexts().iterator();
                        while (true) {
                            if (iterator3.hasNext()) {
                                Context context;
                                if (!(context = (Context) iterator3.next())
                                        .getKey()
                                        .equalsIgnoreCase("server")) {
                                    continue;
                                }
                                String text2;
                                if (!(text2 = context.getValue()).equals("*")) {
                                    values2.add(Scope.appendByScope(text2.replace("server:", "")));
                                    continue;
                                }
                                values2.add(Scope.scopeScope);
                            }
                            if (values2.isEmpty()) {
                                values2.add(Scope.scopeScope);
                            }
                            redisProfile
                                    .getPermissions()
                                    .add(
                                            new ProfilePermission(
                                                    permission,
                                                    permissionNode.getExpiry() != null
                                                            ? permissionNode
                                                                    .getExpiry()
                                                                    .toEpochMilli()
                                                            : -5L,
                                                    values2));
                            break;
                        }
                    }
                }
                iterator2 = user.getNodes(NodeType.INHERITANCE).iterator();
                while (iterator2.hasNext()) {
                    InheritanceNode inheritanceNode;
                    if (!(inheritanceNode = (InheritanceNode) iterator2.next()).isNegated()) {
                        String text3 = "group." + inheritanceNode.getGroupName();
                        if (!redisProfile.hasPrivatePermission(text3)) {
                            ArrayList values3 = new ArrayList();
                            Iterator<Context> iterator4 = inheritanceNode.getContexts().iterator();
                            while (iterator4.hasNext()) {
                                Context context2;
                                if ((context2 = (Context) iterator4.next())
                                        .getKey()
                                        .equalsIgnoreCase("server")) {
                                    String text4;
                                    if ((text4 = context2.getValue()).equals("*")) {
                                        values3.add(Scope.scopeScope);
                                        break;
                                    }
                                    values3.add(Scope.appendByScope(text4.replace("server:", "")));
                                }
                            }
                            if (values3.isEmpty()) {
                                values3.add(Scope.scopeScope);
                            }
                            redisProfile
                                    .getPermissions()
                                    .add(
                                            new ProfilePermission(
                                                    text3,
                                                    inheritanceNode.getExpiry() != null
                                                            ? inheritanceNode
                                                                    .getExpiry()
                                                                    .toEpochMilli()
                                                            : -5L,
                                                    values3));
                        }
                    }
                }
                this.platform.getProfileHandler().getRepository().saveSync(uUID, redisProfile);
            }
            Iterator<InheritanceNode> iterator5 = user.getNodes(NodeType.INHERITANCE).iterator();
            while (iterator5.hasNext()) {
                InheritanceNode inheritanceNode2;
                if (!(inheritanceNode2 = (InheritanceNode) iterator5.next()).isNegated()) {
                    String groupName = inheritanceNode2.getGroupName();
                    IRank rank;
                    if ((rank = this.platform.getRankHandler().getRank(groupName)) != null
                            && !rank.isDefaultRank()) {
                        Grant grant;
                        (grant = new Grant()).setIssuedOn(GlobalConfig.SERVER.NAME);
                        grant.setTarget(user.getUniqueId());
                        grant.setRankId(rank.getUuid());
                        grant.setActive(inheritanceNode2.getValue());
                        grant.setIssuedAt(System.currentTimeMillis());
                        grant.setIssuedBy(IProfile.getConsoleUuid());
                        grant.setDuration(
                                inheritanceNode2.getExpiry() != null
                                        ? inheritanceNode2.getExpiry().toEpochMilli()
                                                - System.currentTimeMillis()
                                        : -5L);
                        grant.setPermanent(inheritanceNode2.getExpiry() == null);
                        grant.setReason("LuckPerms Import");
                        grant.setScopes(Collections.singletonList(Scope.scopeScope));
                        this.platform
                                .getGrantHandler()
                                .getRepository()
                                .saveSync(grant.getUuid(), grant);
                        this.platform
                                .getPacketHandler()
                                .sendSync(new BukkitGrantsUpdatePacket(grant.getTarget()));
                    }
                }
            }
        }
        phoenix.getLogger().info("Finished importing users.");
    }

    public void applyPhoenixByPlatform(Phoenix phoenix) {
        phoenix.getLogger().info("Importing users...");
        UserManager userManager;
        Set<UUID> values =
                (Set)
                        (userManager = LuckPermsProvider.get().getUserManager())
                                .getUniqueUsers()
                                .join();
        phoenix.getLogger().info("Found " + values.size() + " users...");
        for (UUID uUID : (Iterable<UUID>) ((Iterable) values)) {
            User user = (User) userManager.loadUser(uUID).join();
            IProfile profile;
            if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
                String text = (String) userManager.lookupUsername(uUID).join();
                (profile = new RedisProfile(uUID)).setName(text != null ? text : uUID.toString());
            }
            Iterator iterator2 = user.getNodes(NodeType.PERMISSION).iterator();
            while (iterator2.hasNext()) {
                PermissionNode permissionNode;
                String permission =
                        (permissionNode = (PermissionNode) iterator2.next()).getPermission();
                permission = permissionNode.isNegated() ? "-" + permission : permission;
                if (!profile.hasPrivatePermission(permission)) {
                    ArrayList values2 = new ArrayList();
                    Iterator<Context> iterator3 = permissionNode.getContexts().iterator();
                    while (iterator3.hasNext()) {
                        Context context;
                        if ((context = (Context) iterator3.next())
                                .getKey()
                                .equalsIgnoreCase("server")) {
                            String text2;
                            if ((text2 = context.getValue()).equals("*")) {
                                values2.add(Scope.scopeScope);
                                break;
                            }
                            values2.add(Scope.appendByScope(text2.replace("server:", "")));
                        }
                    }
                    if (values2.isEmpty()) {
                        values2.add(Scope.scopeScope);
                    }
                    profile.getPermissions()
                            .add(
                                    new ProfilePermission(
                                            permission,
                                            permissionNode.getExpiry() != null
                                                    ? permissionNode.getExpiry().toEpochMilli()
                                                    : -5L,
                                            values2));
                }
            }
            iterator2 = user.getNodes(NodeType.INHERITANCE).iterator();
            while (iterator2.hasNext()) {
                InheritanceNode inheritanceNode;
                if (!(inheritanceNode = (InheritanceNode) iterator2.next()).isNegated()) {
                    String text3 = "group." + inheritanceNode.getGroupName();
                    if (!profile.hasPrivatePermission(text3)) {
                        ArrayList values3 = new ArrayList();
                        Iterator<Context> iterator4 = inheritanceNode.getContexts().iterator();
                        while (iterator4.hasNext()) {
                            Context context2;
                            if ((context2 = (Context) iterator4.next())
                                    .getKey()
                                    .equalsIgnoreCase("server")) {
                                String text4;
                                if ((text4 = context2.getValue()).equals("*")) {
                                    values3.add(Scope.scopeScope);
                                    break;
                                }
                                values3.add(Scope.appendByScope(text4.replace("server:", "")));
                            }
                        }
                        if (values3.isEmpty()) {
                            values3.add(Scope.scopeScope);
                        }
                        profile.getPermissions()
                                .add(
                                        new ProfilePermission(
                                                text3,
                                                inheritanceNode.getExpiry() != null
                                                        ? inheritanceNode.getExpiry().toEpochMilli()
                                                        : -5L,
                                                values3));
                    }
                }
            }
            this.platform.getProfileHandler().getRepository().saveSync(uUID, profile);
        }
        phoenix.getLogger().info("Finished importing users.");
        Bukkit.getConsoleSender()
                .sendMessage(
                        Color.translate(
                                LocaleUtil.tr(
                                        Bukkit.getConsoleSender(),
                                        MessageConfig.PHOENIX.IMPORT.FINISHED)));
        this.platform
                .getPacketHandler()
                .sendSync(new ServerCommandPacket("all", "stop", "CONSOLE"));
    }

    public void applyPhoenixByScopeScope(Phoenix phoenix) {
        phoenix.getLogger().info("Importing ranks...");
        this.platform.getRankHandler().getRepository().getCache().clear();
        this.platform.getRankHandler().getRepository().deleteAll();
        Set<Group> values;
        Iterator iterator2 =
                (values = LuckPermsProvider.get().getGroupManager().getLoadedGroups()).iterator();
        while (iterator2.hasNext()) {
            Group group;
            String text;
            if (!(text = (group = (Group) iterator2.next()).getName()).equalsIgnoreCase("null")) {
                String prefix =
                        group.getCachedData()
                                .getMetaData(QueryOptions.defaultContextualOptions())
                                .getPrefix();
                String suffix =
                        group.getCachedData()
                                .getMetaData(QueryOptions.defaultContextualOptions())
                                .getSuffix();
                Rank rank = new Rank(text);
                if (group.getWeight().isPresent()) {
                    rank.setPriority(group.getWeight().getAsInt());
                }
                rank.setPrefix(prefix != null ? prefix : "");
                rank.setSuffix(suffix != null ? suffix : "");
                String lastColors = ChatColor.getLastColors(prefix != null ? prefix : "&f");
                rank.setColor(lastColors.isEmpty() ? "&f" : lastColors);
                if (group.getName().equalsIgnoreCase("default")) {
                    rank.setDefaultRank(true);
                }
                Iterator<PermissionNode> iterator3 = group.getNodes(NodeType.PERMISSION).iterator();
                while (iterator3.hasNext()) {
                    PermissionNode permissionNode;
                    String permission =
                            (permissionNode = (PermissionNode) iterator3.next()).getPermission();
                    permission = permissionNode.isNegated() ? "-" + permission : permission;
                    if (!rank.hasPermission(permission)) {
                        ArrayList values2 = new ArrayList();
                        Iterator<Context> iterator4 = permissionNode.getContexts().iterator();
                        while (iterator4.hasNext()) {
                            Context context;
                            if ((context = (Context) iterator4.next())
                                    .getKey()
                                    .equalsIgnoreCase("server")) {
                                String text2;
                                if ((text2 = context.getValue()).equals("*")) {
                                    values2.add(Scope.scopeScope);
                                    break;
                                }
                                values2.add(Scope.appendByScope(text2.replace("server:", "")));
                            }
                        }
                        if (values2.isEmpty()) {
                            values2.add(Scope.scopeScope);
                        }
                        rank.getPermissions().add(new Permission(permission, values2));
                    }
                }
                rank.setDisplayName(lastColors + rank.getName());
                this.platform.getRankHandler().getRepository().addToCache(rank);
                this.platform.getRankHandler().getRepository().saveSync(rank);
            }
        }
        iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            Group group2;
            String name = (group2 = (Group) iterator2.next()).getName();
            IRank rank2;
            if ((rank2 = this.platform.getRankHandler().getRank(name)) != null) {
                for (Group group3 :
                        (Iterable<Group>)
                                ((Iterable)
                                        group2.getInheritedGroups(
                                                QueryOptions.defaultContextualOptions()))) {
                    IRank rank3;
                    if ((rank3 = this.platform.getRankHandler().getRank(group3.getName()))
                            != null) {
                        rank2.addInheritance(rank3);
                    }
                }
                this.platform.getRankHandler().getRepository().saveSync(rank2);
            }
        }
        if (this.platform.getRankHandler().getDefaultRank() == null) {
            this.platform.getRankHandler().createDefaultRank();
        }
        phoenix.getLogger().info("Finished importing ranks.");
    }

    public void sendMessage(Phoenix phoenix, CommandSender sender, String text) {
        if (this.enabled) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.PHOENIX.IMPORT.ALREADY_STARTED)));
        } else if (!(this.platform.getDatabaseHandler() instanceof MongoDatabaseHandler)) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                    sender, MessageConfig.PHOENIX.IMPORT.AQUACORE_NEEDS_MONGO)));
        } else {
            this.enabled = true;
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                ((Player) iterator2.next()).kickPlayer(Color.translate("&cImporting..."));
            }
            Tasks.runAsync(
                    () -> {
                        try {
                            MongoDatabase database =
                                    ((MongoDatabaseHandler) this.platform.getDatabaseHandler())
                                            .getClient()
                                            .getDatabase(text);
                            this.applyPhoenix(phoenix, database);
                            Map<UUID, String> entries = this.findMap(phoenix, database);
                            this.applyPhoenix(phoenix, database, entries);
                            this.applyPhoenixByPlatform(phoenix, database, entries);
                            phoenix.getLogger()
                                    .info(
                                            "Finished importing from AquaCore. Restart every server so the imported data is loaded cleanly.");
                            Bukkit.getConsoleSender()
                                    .sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                            Bukkit.getConsoleSender(),
                                                            MessageConfig.PHOENIX
                                                                    .IMPORT
                                                                    .AQUACORE_FINISHED)));
                        } catch (Exception exception) {
                            phoenix.getLogger()
                                    .info(
                                            "Error importing from AquaCore: "
                                                    + ErrorReporter.formatString(exception));
                        } finally {
                            this.enabled = false;
                        }
                    });
        }
    }

    private void applyPhoenix(Phoenix phoenix, MongoDatabase mongoDatabase) {
        phoenix.getLogger().info("Importing AquaCore ranks...");
        this.platform.getRankHandler().getRepository().getCache().clear();
        this.platform.getRankHandler().getRepository().deleteAll();
        List<Document> values =
                (List) mongoDatabase.getCollection("ranks").find().into(new ArrayList());
        HashMap entries = new HashMap();
        for (Document document : (Iterable<Document>) ((Iterable) values)) {
            String text;
            if ((text = this.isEmpty(document, "name", "rankName")) != null
                    && !text.equalsIgnoreCase("null")) {
                Rank rank = new Rank(text);
                Long value;
                if ((value = this.parseLong(document, "weight", "priority", "order")) != null) {
                    rank.setPriority(value.intValue());
                }
                String empty = this.isEmpty(document, "prefix");
                String empty2 = this.isEmpty(document, "suffix");
                rank.setPrefix(empty != null ? empty : "");
                rank.setSuffix(empty2 != null ? empty2 : "");
                String text2 = this.formatString(this.isEmpty(document, "color", "rankColor"));
                rank.setColor(text2);
                Boolean matches = this.parseBoolean(document, "default", "defaultRank");
                if (Boolean.TRUE.equals(matches) || text.equalsIgnoreCase("default")) {
                    rank.setDefaultRank(true);
                }
                Boolean matches2 = this.parseBoolean(document, "staffRank", "staff");
                if (Boolean.TRUE.equals(matches2)) {
                    rank.setStaff(true);
                }
                Iterator<String> iterator2 = this.findList(document, "permissions").iterator();
                while (iterator2.hasNext()) {
                    String text3;
                    if ((text3 = (String) iterator2.next()) != null
                            && !text3.trim().isEmpty()
                            && !rank.hasPermission(text3)) {
                        rank.getPermissions()
                                .add(
                                        new Permission(
                                                text3.trim(),
                                                Collections.singletonList(Scope.scopeScope)));
                    }
                }
                List<String> values2;
                if (!(values2 = this.findList(document, "inheritance", "inherits", "parents"))
                        .isEmpty()) {
                    entries.put(text.toLowerCase(), values2);
                }
                String empty3 = this.isEmpty(document, "displayName");
                rank.setDisplayName(
                        empty3 != null && !empty3.isEmpty() ? empty3 : text2 + rank.getName());
                this.platform.getRankHandler().getRepository().addToCache(rank);
                this.platform.getRankHandler().getRepository().saveSync(rank);
            } else {
                phoenix.getLogger()
                        .info("Skipping a rank document without a name: " + document.toJson());
            }
        }
        for (Entry entry : (Iterable<Entry>) ((Iterable) entries.entrySet())) {
            IRank rank2;
            if ((rank2 = this.platform.getRankHandler().getRank((String) entry.getKey())) != null) {
                for (String text4 : (Iterable<String>) ((Iterable) (List) entry.getValue())) {
                    IRank rank3;
                    if ((rank3 = this.platform.getRankHandler().getRank(text4)) != null) {
                        rank2.addInheritance(rank3);
                    }
                }
                this.platform.getRankHandler().getRepository().saveSync(rank2);
            }
        }
        if (this.platform.getRankHandler().getDefaultRank() == null) {
            this.platform.getRankHandler().createDefaultRank();
        }
        phoenix.getLogger().info("Finished importing " + values.size() + " AquaCore ranks.");
    }

    private Map<UUID, String> findMap(Phoenix phoenix, MongoDatabase mongoDatabase) {
        phoenix.getLogger().info("Reading AquaCore profiles...");
        HashMap<UUID, String> entries = new HashMap<>();
        for (Document document :
                (Iterable<Document>)
                        ((Iterable)
                                (ArrayList)
                                        mongoDatabase
                                                .getCollection("profiles")
                                                .find()
                                                .into(new ArrayList()))) {
            UUID uUID;
            String text;
            if ((uUID = this.fromString(document, "uuid", "_id", "id")) != null
                    && (text = this.isEmpty(document, "name", "username", "playerName")) != null
                    && !text.isEmpty()) {
                entries.put(uUID, text);
            }
        }
        phoenix.getLogger().info("Read " + entries.size() + " AquaCore profile names.");
        return entries;
    }

    private void applyPhoenix(
            Phoenix phoenix, MongoDatabase mongoDatabase, Map<UUID, String> entries) {
        phoenix.getLogger().info("Importing AquaCore grants...");
        ((MongoDatabaseHandler) this.platform.getDatabaseHandler()).getGrants().drop();
        int value = 0;
        int value2 = 0;
        for (Document document :
                (Iterable<Document>)
                        ((Iterable)
                                (ArrayList)
                                        mongoDatabase
                                                .getCollection("grants")
                                                .find()
                                                .into(new ArrayList()))) {
            UUID uUID = this.fromString(document, "addedTo", "target", "uuid", "player");
            String text;
            IRank rank =
                    (text = this.isEmpty(document, "rank", "rankName", "group")) == null
                            ? null
                            : this.platform.getRankHandler().getRank(text);
            if (uUID != null && rank != null && !rank.isDefaultRank()) {
                this.applyUUID(uUID, (String) entries.get(uUID));
                long value3 =
                        this.calculateTime(
                                document,
                                System.currentTimeMillis(),
                                "addedAt",
                                "dateAdded",
                                "issuedAt",
                                "time",
                                "added");
                long value4 = this.toMillis(document, value3);
                boolean matches =
                        Boolean.TRUE.equals(this.parseBoolean(document, "removed", "isRemoved"));
                Grant grant;
                (grant = new Grant()).setIssuedOn(GlobalConfig.SERVER.NAME);
                grant.setTarget(uUID);
                grant.setRankId(rank.getUuid());
                grant.setActive(!matches);
                grant.setIssuedAt(value3);
                grant.setIssuedBy(this.getConsoleUuid(document, "addedBy", "sender", "issuedBy"));
                grant.setDuration(value4);
                grant.setPermanent(value4 == -5L);
                String empty = this.isEmpty(document, "addedReason", "reason");
                grant.setReason(empty != null && !empty.isEmpty() ? empty : "AquaCore Import");
                if (matches) {
                    grant.setRemovedBy(this.getConsoleUuid(document, "removedBy"));
                    grant.setRemovedAt(
                            this.calculateTime(
                                    document,
                                    System.currentTimeMillis(),
                                    "removedAt",
                                    "dateRemoved"));
                    grant.setRemovedReason(this.isEmpty(document, "removedReason", "removedFor"));
                }
                grant.setScopes(Collections.singletonList(Scope.scopeScope));
                this.platform.getGrantHandler().getRepository().saveSync(grant.getUuid(), grant);
                value++;
            } else {
                value2++;
            }
        }
        phoenix.getLogger()
                .info(
                        "Finished importing AquaCore grants ("
                                + value
                                + " imported, "
                                + value2
                                + " skipped).");
    }

    private void applyPhoenixByPlatform(
            Phoenix phoenix, MongoDatabase mongoDatabase, Map<UUID, String> entries) {
        phoenix.getLogger().info("Importing AquaCore punishments...");
        ((MongoDatabaseHandler) this.platform.getDatabaseHandler()).getPunishments().drop();
        this.platform.getPunishmentHandler().getRepository().getListCache().invalidateAll();
        int value = 0;
        int value2 = 0;
        for (Document document :
                (Iterable<Document>)
                        ((Iterable)
                                (ArrayList)
                                        mongoDatabase
                                                .getCollection("punishments")
                                                .find()
                                                .into(new ArrayList()))) {
            UUID uUID = this.fromString(document, "addedTo", "target", "uuid", "player");
            String empty = this.isEmpty(document, "type", "punishmentType");
            PunishmentType punishmentType = this.findPunishmentType(empty);
            if (uUID != null && punishmentType != null) {
                this.applyUUID(uUID, (String) entries.get(uUID));
                long value3 =
                        this.calculateTime(
                                document,
                                System.currentTimeMillis(),
                                "addedAt",
                                "dateAdded",
                                "issuedAt",
                                "time",
                                "added");
                long value4 = this.toMillis(document, value3);
                boolean matches =
                        Boolean.TRUE.equals(this.parseBoolean(document, "removed", "isRemoved"));
                Punishment punishment;
                (punishment = new Punishment()).setPunishmentType(punishmentType);
                punishment.setTarget(uUID);
                punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
                punishment.setIssuedAt(value3);
                punishment.setIssuedBy(
                        this.getConsoleUuid(document, "addedBy", "sender", "issuedBy"));
                punishment.setDuration(value4);
                punishment.setPermanent(value4 == -5L);
                punishment.setSilent(Boolean.TRUE.equals(this.parseBoolean(document, "silent")));
                punishment.setIp(
                        punishmentType == PunishmentType.BLACKLIST
                                || Boolean.TRUE.equals(
                                        this.parseBoolean(
                                                document, "ip", "ipRelative", "ipPunishment"))
                                || empty != null && empty.toUpperCase().contains("IP"));
                String empty2 = this.isEmpty(document, "addedReason", "reason");
                punishment.setReason(
                        empty2 != null && !empty2.isEmpty() ? empty2 : "AquaCore Import");
                if (matches) {
                    punishment.setRemovedBy(this.getConsoleUuid(document, "removedBy"));
                    punishment.setRemovedAt(
                            this.calculateTime(
                                    document,
                                    System.currentTimeMillis(),
                                    "removedAt",
                                    "dateRemoved"));
                    punishment.setRemovedReason(
                            this.isEmpty(document, "removedReason", "removedFor"));
                }
                this.platform
                        .getPunishmentHandler()
                        .getRepository()
                        .saveSync(punishment.getUuid(), punishment);
                value++;
            } else {
                value2++;
                if (empty != null && punishmentType == null) {
                    phoenix.getLogger()
                            .info("Skipping a punishment with unknown type '" + empty + "'.");
                }
            }
        }
        phoenix.getLogger()
                .info(
                        "Finished importing AquaCore punishments ("
                                + value
                                + " imported, "
                                + value2
                                + " skipped).");
    }

    private void applyUUID(UUID uUID, String text) {
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
            RedisProfile redisProfile;
            (redisProfile = new RedisProfile(uUID)).setName(text != null ? text : uUID.toString());
            this.platform.getProfileHandler().getRepository().saveSync(uUID, redisProfile);
        } else {
            if (text != null && (profile.getName() == null || profile.getName().isEmpty())) {
                profile.setName(text);
                this.platform.getProfileHandler().getRepository().saveSync(uUID, profile);
            }
        }
    }

    private long toMillis(Document document, long value) {
        Long value2;
        if ((value2 =
                                this.parseLong(
                                        document,
                                        "expire",
                                        "expiration",
                                        "expiryDate",
                                        "until",
                                        "expires"))
                        != null
                && value2 > 0L
                && value2 != Long.MAX_VALUE) {
            long value3;
            return (value3 = value2 - value) > 0L ? value3 : -5L;
        } else {
            Long value4;
            if ((value4 = this.parseLong(document, "duration")) == null
                    || value4 <= 0L
                    || value4 == Long.MAX_VALUE
                    || value4 == 2147483647L) {
                return -5L;
            } else {
                return value4 > TimeUnit.DAYS.toMillis(36500L) ? -5L : value4;
            }
        }
    }

    private PunishmentType findPunishmentType(String text) {
        if (text == null) {
            return null;
        }
        String text2;
        if ((text2 =
                        text.toUpperCase()
                                .replace("TEMP", "")
                                .replace("IP", "")
                                .replace("_", "")
                                .trim())
                .startsWith("WARN")) {
            return PunishmentType.WARN;
        }
        try {
            return PunishmentType.valueOf(text2);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    private String formatString(String text) {
        if (text != null && !text.trim().isEmpty()) {
            String text2;
            if (!(text2 = text.trim()).startsWith("&") && !text2.startsWith("\u00a7")) {
                try {
                    return "&" + ChatColor.valueOf(text2.toUpperCase().replace(" ", "_")).getChar();
                } catch (IllegalArgumentException illegalArgumentException) {
                    return "&f";
                }
            } else {
                return "&" + text2.substring(1, 2);
            }
        } else {
            return "&f";
        }
    }

    private UUID getConsoleUuid(Document document, String... text) {
        UUID uUID;
        return (uUID = this.fromString(document, text)) != null ? uUID : IProfile.getConsoleUuid();
    }

    private long calculateTime(Document document, long value, String... text) {
        Long value2;
        return (value2 = this.parseLong(document, text)) != null && value2 > 0L ? value2 : value;
    }

    private String isEmpty(Document document, String... text) {
        String[] texts = text;
        int value = text.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            Object result;
            if ((result = document.get(text2)) instanceof String && !((String) result).isEmpty()) {
                return (String) result;
            }
        }
        return null;
    }

    private Long parseLong(Document document, String... text) {
        String[] texts = text;
        int value = text.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            Object result;
            if ((result = document.get(text2)) instanceof Number) {
                return ((Number) result).longValue();
            }
            if (result instanceof String) {
                try {
                    return Long.parseLong((String) result);
                } catch (NumberFormatException numberFormatException) {
                }
            }
        }
        return null;
    }

    private Boolean parseBoolean(Document document, String... text) {
        String[] texts = text;
        int value = text.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            Object result;
            if ((result = document.get(text2)) instanceof Boolean) {
                return (Boolean) result;
            }
            if (result instanceof String) {
                return Boolean.parseBoolean((String) result);
            }
        }
        return null;
    }

    private UUID fromString(Document document, String... text) {
        String[] texts = text;
        int value = text.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            Object result;
            if ((result = document.get(text2)) instanceof UUID) {
                return (UUID) result;
            }
            if (result instanceof String) {
                try {
                    return UUID.fromString((String) result);
                } catch (IllegalArgumentException illegalArgumentException) {
                }
            }
        }
        return null;
    }

    private List<String> findList(Document document, String... text) {
        String[] texts = text;
        int value = text.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            Object result;
            if ((result = document.get(text2)) instanceof List) {
                ArrayList<String> values = new ArrayList<>();
                Iterator iterator2 = ((List) result).iterator();
                while (iterator2.hasNext()) {
                    Object result2;
                    if ((result2 = iterator2.next()) instanceof String) {
                        values.add((String) result2);
                    }
                }
                return values;
            }
        }
        return Collections.emptyList();
    }

    public ImportHandler(CommonPlatform commonPlatform) {
        this.enabled = false;
        this.platform = commonPlatform;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

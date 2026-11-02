package xyz.refinedev.phoenix.handler;

import com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitDisguiseNameUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.cooldown.Cooldown;
import xyz.refinedev.phoenix.profile.disguise.DisguiseData;
import xyz.refinedev.phoenix.profile.disguise.DisguiseHistory;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseHistory;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.disguise.Skin;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.repository.IDisguiseHistoryRepository;
import xyz.refinedev.phoenix.repository.impl.MongoDisguiseHistoryRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLDisguiseHistoryRepository;
import xyz.refinedev.phoenix.repository.redis.DisguiseSkinRepository;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.NameTagUtils;
import xyz.refinedev.phoenix.utils.events.disguise.ProfileDisguiseEvent;
import xyz.refinedev.phoenix.utils.events.disguise.ProfileUndisguiseEvent;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DisguiseHandler {

    public static final String ROLLBACK_TAG_NAME_KEY = "tag:name";

    public static final String ROLLBACK_TAG_CUSTOM_KEY = "tag:custom";

    private static final long DISGUISE_NAME_RECONCILE_TICKS = 6000L;

    private final IDisguiseHistoryRepository historyRepository;

    private final DisguiseSkinRepository skinRepository;

    private final Phoenix plugin;

    private final Random random = new Random();

    private final Map<UUID, Optional<String>> preResolvedDisguiseOwners = new ConcurrentHashMap<>();

    private final Map<UUID, String> disguiseNames = new ConcurrentHashMap<>();

    private final Set<UUID> localDisguises = ConcurrentHashMap.newKeySet();

    public DisguiseHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        this.historyRepository =
                (IDisguiseHistoryRepository)
                        new RepositoryFactory(phoenix)
                                .create(
                                        result ->
                                                new MongoDisguiseHistoryRepository(
                                                        result.getDisguiseHistory()),
                                        result ->
                                                new MySQLDisguiseHistoryRepository(
                                                        result.getDisguiseHistory()));
        this.skinRepository = new DisguiseSkinRepository(phoenix, "disguise-skins");
        try {
            phoenix.getRunnable().run();
        } catch (Exception exception) {
            if (phoenix.getFoliaLib().isFolia()) {
                phoenix.getFoliaLib().getScheduler().cancelAllTasks();
            } else {
                Bukkit.getScheduler().cancelTasks(phoenix);
            }
            Bukkit.shutdown();
            return;
        }
        if (phoenix.getNmsHandler().isRegistered()) {
            Tasks.runAsyncTimer(phoenix, this::refreshDisguiseNames, 0L, 6000L);
            long value;
            if ((value =
                            phoenix.getDatabaseHandler()
                                    .runRedisCommand(result -> result.hlen("disguise-skins")))
                    == 0L) {
                phoenix.log(
                        "Loading all disguise skins into the database. This might take a while.");
                phoenix.log("This will only happen on first startup.");
            } else if (value < SettingsConfig.DISGUISE.SKINS.size()) {
                phoenix.log(
                        "Loading new disguise skins into the database. This might take a while.");
                phoenix.log(
                        "This will only happen when you add/remove skins from the config file.");
            }
            Iterator<String> iterator2 = SettingsConfig.DISGUISE.SKINS.iterator();
            while (iterator2.hasNext()) {
                String text;
                if (!(text = (String) iterator2.next()).isEmpty()) {
                    ISkin skin;
                    if ((skin = this.getSkinByName(text).join()) == null) {
                        phoenix.log("Could not resolve the disguise skin '" + text + "'.");
                    } else {
                        this.skinRepository.addToCache(skin);
                    }
                }
            }
            phoenix.log("Loaded all disguise skins from redis.");
        }
    }

    public void disguise(IProfile profile, IDisguiseData disguiseData, boolean matches) {
        if (this.plugin.getNmsHandler().isRegistered()) {
            Player player;
            if ((player = Bukkit.getPlayer(profile.getUniqueId())) != null) {
                IDisguiseData disguiseData2;
                (disguiseData2 = profile.getDisguiseData())
                        .setDisguiseName(disguiseData.getDisguiseName());
                disguiseData2.setDisguiseSkin(disguiseData.getDisguiseSkin());
                disguiseData2.setRealName(profile.getName());
                disguiseData2.setRankId(disguiseData.getRankId());
                disguiseData2.setRealSkin(
                        this.plugin.getNmsHandler().getAPI().getPlayerSkin(player));
                disguiseData2.setDisguiseUuid(
                        SettingsConfig.DISGUISE.HIDE_UUID ? UUID.randomUUID() : null);
                this.localDisguises.add(profile.getUniqueId());
                ProfileDisguiseEvent profileDisguiseEvent;
                BukkitAPI.callEvent(profileDisguiseEvent = new ProfileDisguiseEvent(profile));
                if (profileDisguiseEvent.isCancelled()) {
                    profile.setDisguiseData(new DisguiseData());
                } else {
                    if (!player.hasPermission("core.cooldowns.bypass")) {
                        if (matches) {
                            profile.getGlobalCooldowns()
                                    .add(
                                            new Cooldown(
                                                    BukkitAPI.parseDuration(
                                                            SettingsConfig.COOLDOWNS.REDISGUISE),
                                                    "redisguise"));
                        } else {
                            profile.getGlobalCooldowns()
                                    .add(
                                            new Cooldown(
                                                    BukkitAPI.parseDuration(
                                                            SettingsConfig.COOLDOWNS.DISGUISE),
                                                    "disguise"));
                        }
                    }
                    Map<String, String> entries;
                    (entries = disguiseData2.getRollbackData())
                            .put(
                                    "tag:name",
                                    profile.getTagName() == null ? "" : profile.getTagName());
                    if (profile.getCustomTag() != null) {
                        entries.put(
                                "tag:custom",
                                this.plugin
                                        .getGson()
                                        .toJson(profile.getCustomTag(), ICustomTag.class));
                    }
                    profile.setTag(null);
                    profile.saveAndUpdate();
                    this.plugin.getNmsHandler().getAPI().disguise(profile, matches);
                    this.plugin
                            .getDisguiseHandler()
                            .getHistoryRepository()
                            .save(
                                    new DisguiseHistory(
                                            profile.getUuid(),
                                            disguiseData.getRankId(),
                                            disguiseData.getDisguiseName(),
                                            disguiseData.getDisguiseSkin()));
                    this.plugin
                            .getDisguiseHandler()
                            .cacheDisguise(profile.getUuid(), disguiseData.getDisguiseName(), true);
                    IRank rank = this.plugin.getRankHandler().getRank(disguiseData.getRankId());
                    if (matches) {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.DISGUISE.REDISGUISE_MESSAGE)
                                                .replace("<color>", profile.getNameColor())
                                                .replace("<name>", profile.getPlayerName())));
                        this.plugin
                                .getPacketHandler()
                                .send(
                                        new SendToStaffPacket(
                                                MessageConfig.STAFF.REDISGUISE_ALERT,
                                                LocaleUtil.replacements(
                                                        "<realName>",
                                                        profile.getNameWithColorNoDisguise(),
                                                        "<disguiseName>",
                                                        Color.translate(
                                                                rank.getColor()
                                                                        + disguiseData
                                                                                .getDisguiseName()),
                                                        "<server>",
                                                        this.plugin
                                                                .getNetworkHandler()
                                                                .getDisplayName()),
                                                true,
                                                false));
                    } else {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.DISGUISE.DISGUISE_MESSAGE)
                                                .replace("<color>", profile.getNameColor())
                                                .replace("<name>", profile.getPlayerName())));
                        this.plugin
                                .getPacketHandler()
                                .send(
                                        new SendToStaffPacket(
                                                MessageConfig.STAFF.DISGUISE_ALERT,
                                                LocaleUtil.replacements(
                                                        "<realName>",
                                                        profile.getNameWithColorNoDisguise(),
                                                        "<disguiseName>",
                                                        Color.translate(
                                                                rank.getColor()
                                                                        + disguiseData
                                                                                .getDisguiseName()),
                                                        "<server>",
                                                        this.plugin
                                                                .getNetworkHandler()
                                                                .getDisplayName()),
                                                true,
                                                false));
                    }
                    if (SettingsConfig.SERVER.NAMETAGS) {
                        NameTagUtils.applyPlayer(player, disguiseData.getDisguiseName());
                    }
                    NameTagUtils.applyPlayer(player);
                }
            }
        }
    }

    public void undisguise(IProfile profile, boolean matches) {
        if (this.plugin.getNmsHandler().isRegistered()) {
            ProfileUndisguiseEvent profileUndisguiseEvent;
            BukkitAPI.callEvent(profileUndisguiseEvent = new ProfileUndisguiseEvent(profile));
            if (!profileUndisguiseEvent.isCancelled()) {
                Player player = Bukkit.getPlayer(profile.getUniqueId());
                IDisguiseData disguiseData = profile.getDisguiseData();
                IRank rank;
                if ((rank = this.plugin.getRankHandler().getRank(disguiseData.getRankId()))
                        != null) {
                    this.plugin
                            .getPacketHandler()
                            .send(
                                    new SendToStaffPacket(
                                            MessageConfig.STAFF.UNDISGUISE_ALERT,
                                            LocaleUtil.replacements(
                                                    "<realName>",
                                                    profile.getNameWithColorNoDisguise(),
                                                    "<disguiseName>",
                                                    Color.translate(
                                                            rank.getColor()
                                                                    + disguiseData
                                                                            .getDisguiseName()),
                                                    "<server>",
                                                    this.plugin
                                                            .getNetworkHandler()
                                                            .getDisplayName()),
                                            true,
                                            false));
                }
                if (matches) {
                    this.plugin
                            .getDisguiseHandler()
                            .cacheDisguise(
                                    profile.getUuid(), disguiseData.getDisguiseName(), false);
                }
                this.clearDisguiseName(profile.getUuid());
                disguiseData.setDisguiseUuid(null);
                this.localDisguises.remove(profile.getUuid());
                this.plugin.getNmsHandler().getAPI().undisguise(profile);
                this.plugin
                        .getDisguiseHandler()
                        .updatePreviousDisguiseData(profile.getUuid(), disguiseData);
                Map<String, String> rollbackData = disguiseData.getRollbackData();
                profile.setTagName(rollbackData.getOrDefault("tag:name", ""));
                String text = (String) rollbackData.get("tag:custom");
                profile.setCustomTag(
                        text == null
                                ? null
                                : (ICustomTag)
                                        this.plugin.getGson().fromJson(text, ICustomTag.class));
                rollbackData.clear();
                disguiseData.setRealName(null);
                disguiseData.setRealSkin(null);
                disguiseData.setDisguiseName(null);
                disguiseData.setDisguiseSkin(null);
                disguiseData.setRankId(null);
                profile.saveAndUpdate();
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.DISGUISE.UNDISGUISE_MESSAGE)));
                if (SettingsConfig.SERVER.NAMETAGS) {
                    NameTagUtils.applyPlayer(player, profile.getName());
                }
                NameTagUtils.applyPlayer(player);
            }
        }
    }

    public boolean hasLocalDisguises() {
        return !this.localDisguises.isEmpty();
    }

    public void restorePlayerDisguiseState(IProfile profile) {
        if (profile != null) {
            this.localDisguises.remove(profile.getUniqueId());
            if (this.plugin.getNmsHandler().isRegistered()) {
                if (profile.getDisguiseData().isDisguised()) {
                    if (Bukkit.getPlayer(profile.getUniqueId()) != null) {
                        this.plugin.getNmsHandler().getAPI().undisguise(profile);
                    }
                }
            }
        }
    }

    public List<String> getAvailableNames() {
        return SettingsConfig.DISGUISE.NAMES;
    }

    public CompletableFuture<ISkin> getSkinByName(String text) {
        return CompletableFuture.supplyAsync(
                () -> {
                    Optional optional;
                    if ((optional = this.skinRepository.getFromCache(text)).isPresent()) {
                        return (ISkin) optional.get();
                    }
                    if ((optional = this.skinRepository.fetchSync(text.toLowerCase()))
                            .isPresent()) {
                        return (ISkin) optional.get();
                    }
                    Skin skin;
                    if ((skin = Skin.getSkinByNameSync(text)) == null) {
                        return null;
                    }
                    this.skinRepository.saveSync(skin);
                    return skin;
                });
    }

    public CompletableFuture<List<IDisguiseHistory>> fetchDisguiseHistory(UUID uUID) {
        return CompletableFuture.supplyAsync(
                () -> {
                    List<IDisguiseHistory> values;
                    (values =
                                    this.historyRepository.fetchAllSync(
                                            Filters.eq("player", uUID.toString())))
                            .sort(Comparator.comparingLong(IDisguiseHistory::getDate).reversed());
                    return values;
                });
    }

    public String getRandomName() {
        List<String> values;
        if ((values = this.getAvailableNames()) != null && !values.isEmpty()) {
            Set<String> values2;
            if ((values2 = this.getNamesInUse().join()).size() >= values.size()) {
                return null;
            }
            String text = (String) values.get((int) (Math.random() * values.size()));
            while (values2.contains(text.toLowerCase())) {
                text = (String) values.get((int) (Math.random() * values.size()));
            }
            return text;
        } else {
            return null;
        }
    }

    public ISkin getRandomSkin() {
        int value;
        return (value = this.skinRepository.getCache().size()) == 0
                ? Skin.DEFAULT_SKIN
                : this.skinRepository.getCache().get(this.random.nextInt(value));
    }

    public String getDisguiseName(UUID uUID) {
        return uUID == null ? null : this.disguiseNames.get(uUID);
    }

    public void clearDisguiseName(UUID uUID) {
        if (uUID != null) {
            this.disguiseNames.remove(uUID);
            IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
            databaseHandler.executeRedisCommand(
                    result -> result.hdel("disguise-names", new String[] {uUID.toString()}));
            this.plugin.getPacketHandler().send(new BukkitDisguiseNameUpdatePacket(uUID, null));
        }
    }

    public void applyDisguiseName(UUID uUID, String text) {
        if (uUID != null) {
            if (text == null) {
                this.disguiseNames.remove(uUID);
            } else {
                this.disguiseNames.put(uUID, text);
            }
        }
    }

    public void refreshDisguiseNames() {
        HashMap entries = new HashMap();
        Map<String, String> entries2;
        if ((entries2 =
                        this.plugin
                                .getDatabaseHandler()
                                .runRedisCommand(result -> result.hgetall("disguises")))
                != null) {
            for (Entry<String, String> entry : entries2.entrySet()) {
                UUID uUID;
                if ((uUID = this.parseUuid((String) entry.getValue())) != null) {
                    entries.put(uUID, (String) entry.getKey());
                }
            }
        }
        Map<String, String> entries3;
        if ((entries3 =
                        this.plugin
                                .getDatabaseHandler()
                                .runRedisCommand(result -> result.hgetall("disguise-names")))
                != null) {
            for (Entry<String, String> entry2 : entries3.entrySet()) {
                UUID uUID2;
                if ((uUID2 = this.parseUuid((String) entry2.getKey())) != null) {
                    entries.put(uUID2, (String) entry2.getValue());
                }
            }
        }
        this.disguiseNames.keySet().retainAll(entries.keySet());
        this.disguiseNames.putAll(entries);
    }

    private UUID parseUuid(String text) {
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return null;
        }
    }

    public CompletableFuture<Set<String>> getNamesInUse() {
        return CompletableFuture.supplyAsync(
                () -> {
                    Map<String, String> entries;
                    return (entries =
                                            this.plugin
                                                    .getDatabaseHandler()
                                                    .runRedisCommand(
                                                            result -> result.hgetall("disguises")))
                                    == null
                            ? new HashSet<>()
                            : entries.keySet();
                });
    }

    public void cacheDisguise(UUID uUID, String text, boolean matches) {
        if (matches) {
            this.disguiseNames.put(uUID, text);
        } else {
            this.disguiseNames.remove(uUID);
        }
        this.plugin
                .getPacketHandler()
                .send(new BukkitDisguiseNameUpdatePacket(uUID, matches ? text : null));
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    if (matches) {
                        result.hset("disguises", text.toLowerCase(), uUID.toString());
                        result.hset("disguise-names", uUID.toString(), text);
                    } else {
                        result.hdel("disguises", new String[] {text.toLowerCase()});
                        result.hdel("disguise-names", new String[] {uUID.toString()});
                    }
                });
    }

    public String getUuidByFakeName(String text) {
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        return databaseHandler.runRedisCommand(
                result -> (String) result.hget("disguises", text.toLowerCase()));
    }

    public void preResolveFakeNameOwner(UUID uUID, String text) {
        this.preResolvedDisguiseOwners.put(uUID, Optional.ofNullable(this.getUuidByFakeName(text)));
    }

    public String consumeFakeNameOwner(UUID uUID, String text) {
        Optional<String> optional;
        return (optional = this.preResolvedDisguiseOwners.remove(uUID)) != null
                ? (String) optional.orElse(null)
                : this.getUuidByFakeName(text);
    }

    public List<IProfile> getAllDisguisedProfiles() {
        ArrayList<IProfile> values = new ArrayList<>();
        for (UUID uUID : this.plugin.getNetworkHandler().getAllUuids()) {
            IProfile profile;
            if ((profile = this.plugin.getProfileHandler().getProfile(uUID)) != null
                    && profile.getDisguiseData().isDisguised()) {
                values.add(profile);
            }
        }
        return values;
    }

    public boolean hasPreviouslyDisguised(UUID uUID) {
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        return databaseHandler.runRedisCommand(
                result -> result.hexists("previousDisguiseData", uUID.toString()));
    }

    public void updatePreviousDisguiseData(UUID uUID, IDisguiseData disguiseData) {
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result ->
                        result.hset(
                                "previousDisguiseData",
                                uUID.toString(),
                                this.plugin.getGson().toJson(disguiseData, IDisguiseData.class)));
    }

    public IDisguiseData getPreviousDisguiseData(UUID uUID) {
        Gson gson = this.plugin.getGson();
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        return (IDisguiseData)
                gson.fromJson(
                        (String)
                                databaseHandler.runRedisCommand(
                                        result ->
                                                (String)
                                                        result.hget(
                                                                "previousDisguiseData",
                                                                uUID.toString())),
                        IDisguiseData.class);
    }

    public CompletableFuture<Boolean> isNameInUse(String text) {
        return CompletableFuture.supplyAsync(
                () -> {
                    IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
                    return databaseHandler.runRedisCommand(
                                    result -> (String) result.hget("disguises", text.toLowerCase()))
                            != null;
                },
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    public IDisguiseHistoryRepository getHistoryRepository() {
        return this.historyRepository;
    }

    public DisguiseSkinRepository getSkinRepository() {
        return this.skinRepository;
    }

    public Phoenix getPlugin() {
        return this.plugin;
    }

    public Random getRandom() {
        return this.random;
    }

    public Map<UUID, Optional<String>> getPreResolvedDisguiseOwners() {
        return this.preResolvedDisguiseOwners;
    }

    public Map<UUID, String> getDisguiseNames() {
        return this.disguiseNames;
    }

    public Set<UUID> getLocalDisguises() {
        return this.localDisguises;
    }
}

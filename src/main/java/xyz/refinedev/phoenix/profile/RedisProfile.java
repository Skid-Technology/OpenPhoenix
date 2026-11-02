package xyz.refinedev.phoenix.profile;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.alt.AltEvidenceTypes;
import xyz.refinedev.phoenix.alt.AltLinkStatus;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.alt.IAltSignal;
import xyz.refinedev.phoenix.alt.IClientFingerprint;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.packet.impl.profile.GrantsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.ProfileUpdatePacket;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.profile.cooldown.ICooldown;
import xyz.refinedev.phoenix.profile.disguise.DisguiseData;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.notes.INote;
import xyz.refinedev.phoenix.profile.permission.IProfilePermission;
import xyz.refinedev.phoenix.profile.permission.ProfilePermission;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Exclude;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.Processable;
import xyz.refinedev.phoenix.utils.Tristate;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class RedisProfile extends TrackedEntity implements IProfile, Processable {

    @SerializedName("_id")
    private final UUID uniqueId;

    @SerializedName("name")
    private String name;

    @SerializedName("nameToLowercase")
    private String nameToLowercase;

    @SerializedName("permissions")
    private Set<IProfilePermission> permissions = new HashSet<>();

    @Exclude private Map<String, Boolean> attachmentPermissions = new ConcurrentHashMap<>();

    @Exclude private boolean enabled = false;

    @Exclude private Cache<String, Tristate> cache;

    @SerializedName("ignoring")
    private Set<UUID> ignoring = new HashSet<>();

    @SerializedName("afkTime")
    private long afkTime = 0L;

    @SerializedName("globalCooldowns")
    private List<ICooldown> globalCooldowns = new ArrayList<>();

    @SerializedName("alts")
    private Set<UUID> alts = new HashSet<>();

    @SerializedName("siblings")
    private Set<UUID> siblings = new HashSet<>();

    @SerializedName("notes")
    private List<INote> notes = new ArrayList<>();

    @SerializedName("nameHistory")
    private List<INameChange> nameHistory = new ArrayList<>();

    @SerializedName("fingerprint")
    private IClientFingerprint clientFingerprint;

    @SerializedName("skinHash")
    private String skinHash;

    @SerializedName("recentPings")
    private List<Integer> recentPings = new ArrayList<>();

    @SerializedName("logoutBuckets")
    private Set<String> logoutBuckets = new LinkedHashSet<>();

    @SerializedName("currentAddress")
    private String currentAddress = "";

    @SerializedName("currentSubnet")
    private String currentSubnet = "";

    @SerializedName("knownAddresses")
    private Set<String> knownAddresses = new HashSet<>();

    @SerializedName("disguiseData")
    private IDisguiseData disguiseData = new DisguiseData();

    @SerializedName("tagName")
    private String tagName = "";

    @SerializedName("customTag")
    private ICustomTag customTag;

    @SerializedName("authType")
    private String authType = null;

    @SerializedName("twoFactor")
    private boolean twoFactor = false;

    @SerializedName("secretKey")
    private String secretKey = "";

    @SerializedName("metadata")
    private Map<String, String> metadata = new HashMap<>();

    private static final Comparator<RedisProfile.Entry> comparator =
            Comparator.<RedisProfile.Entry>comparingInt(result -> result.count)
                    .thenComparing(result -> result.enabled);

    private static final long time = 512L;

    private static CommonPlatform getCommonPlatform() {
        return PlatformGetter.getInstance();
    }

    public static UUID fromString() {
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    @Override
    public void loadProfile() {
        this.loadProfile(true);
    }

    @Override
    public void loadProfile(boolean matches) {
        long now = System.currentTimeMillis();
        Optional optional;
        boolean present =
                (optional =
                                getCommonPlatform()
                                        .getProfileHandler()
                                        .getCacheRepository()
                                        .fetchSync(this.uniqueId))
                        .isPresent();
        if (!optional.isPresent()) {
            optional =
                    getCommonPlatform()
                            .getProfileHandler()
                            .getRepository()
                            .fetchSync(this.uniqueId);
            if (getCommonPlatform().isDebugMode()) {
                getCommonPlatform()
                        .getLogger()
                        .info("Loaded profile from database, not found in redis.");
            }
        }
        if (!optional.isPresent()) {
            if (matches) {
                this.saveProfile();
            }
        } else {
            if (!present) {
                getCommonPlatform()
                        .getProfileHandler()
                        .getCacheRepository()
                        .seed(this.uniqueId, (IProfile) optional.get());
            }
            this.loadProfile((IProfile) optional.get());
            if (getCommonPlatform().isDebugMode()) {
                getCommonPlatform()
                        .getLogger()
                        .info(
                                "Loaded "
                                        + this.name
                                        + "'s Profile in "
                                        + (System.currentTimeMillis() - now)
                                        + "ms.");
            }
        }
    }

    @Override
    public void loadProfileNoSaveNoRedis() {
        long now = System.currentTimeMillis();
        Optional<xyz.refinedev.phoenix.profile.IProfile> optional;
        if ((optional =
                        getCommonPlatform()
                                .getProfileHandler()
                                .getRepository()
                                .fetchSync(this.uniqueId))
                .isPresent()) {
            this.loadProfile((IProfile) optional.get());
            if (getCommonPlatform().isDebugMode()) {
                getCommonPlatform()
                        .getLogger()
                        .info(
                                "Loaded "
                                        + this.name
                                        + "'s Profile in "
                                        + (System.currentTimeMillis() - now)
                                        + "ms.");
            }
        }
    }

    @Override
    public void loadProfile(IProfile profile) {
        if (!(profile instanceof TrackedEntity)) {
            throw new IllegalArgumentException(
                    "Profile source must be TrackedEntity, got " + profile.getClass().getName());
        }
        Gson gson = getCommonPlatform().getGson();
        if (this.isNew()) {
            this.copyFieldsFrom((TrackedEntity) profile, this.persistedFieldNames());
            this.snapshot(gson);
        } else {
            this.mergeFrom((TrackedEntity) profile, gson);
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.nameToLowercase = name.toLowerCase(Locale.ROOT);
    }

    @Override
    public void saveProfile() {
        try {
            getCommonPlatform().getProfileHandler().getRepository().save(this.uniqueId, this);
        } catch (RuntimeException runtimeException) {
            getCommonPlatform()
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Could not save " + this.name + "'s profile",
                            runtimeException);
        }
    }

    @Override
    public void saveProfileSync() {
        long now = System.currentTimeMillis();
        getCommonPlatform().getProfileHandler().getRepository().saveSync(this.uniqueId, this);
        this.applyTime(now);
    }

    private synchronized Set<String> findSet(boolean matches) {
        if (matches) {
            getCommonPlatform().getProfileHandler().getRepository().saveSync(this.uniqueId, this);
        } else {
            getCommonPlatform().getProfileHandler().getRepository().save(this.uniqueId, this);
        }
        return this.lastDrained();
    }

    private void applyTime(long value) {
        if (getCommonPlatform().isDebugMode()) {
            getCommonPlatform()
                    .getLogger()
                    .info(
                            "Saved "
                                    + this.name
                                    + "'s Profile in "
                                    + (System.currentTimeMillis() - value)
                                    + "ms.");
        }
    }

    @Override
    public void saveAndUpdate(boolean matches) {
        Set<String> values;
        if (!(values = this.findSet(false)).isEmpty() || matches) {
            if (matches) {
                this.updatePermissions();
            }
            getCommonPlatform()
                    .getPacketHandler()
                    .send(new ProfileUpdatePacket(this, matches, values));
        }
    }

    @Override
    public void saveAndUpdateSync(boolean matches) {
        long now = System.currentTimeMillis();
        Set<String> values;
        if (!(values = this.findSet(true)).isEmpty() || matches) {
            if (matches) {
                this.updatePermissions();
            }
            getCommonPlatform()
                    .getPacketHandler()
                    .sendSync(new ProfileUpdatePacket(this, matches, values));
            this.applyTime(now);
        }
    }

    @Override
    public void saveAndUpdate() {
        this.saveAndUpdate(false);
    }

    @Override
    public List<IPunishment> getPunishments() {
        return getCommonPlatform().getPunishmentHandler().getPunishments(this.uniqueId);
    }

    @Nullable
    @Override
    public ITag getTag() {
        return this.customTag != null
                ? this.customTag
                : getCommonPlatform().getTagHandler().getTag(this.tagName);
    }

    @Override
    public void setTag(ITag tag) {
        if (tag == null) {
            this.tagName = "";
            this.customTag = null;
        } else if (tag instanceof ICustomTag) {
            this.customTag = (ICustomTag) tag;
        } else {
            this.tagName = tag.getName();
        }
    }

    @Override
    public boolean isBanned() {
        return this.getActiveBan() != null;
    }

    @Override
    public boolean isCompetitiveBanned() {
        return this.getActiveCompetitiveBan() != null;
    }

    @Override
    public boolean isBlacklisted() {
        return this.getActiveBlacklist() != null;
    }

    @Override
    public boolean isMuted() {
        return this.getActiveMute() != null || this.getActiveIPMute() != null;
    }

    @Override
    public boolean isIPMuted() {
        return this.getActiveIPMute() != null;
    }

    @Override
    public boolean isIPBanned() {
        return this.getActiveIPBan() != null;
    }

    @Override
    public boolean shouldSendEvadeAlert() {
        if (this.isBlacklisted()) {
            return false;
        } else if (this.isIPBanned()) {
            return false;
        } else {
            return this.isIPMuted() ? this.getActiveIPBanWithoutSelf() != null : true;
        }
    }

    @Override
    public boolean isNormalOrShadowMuted() {
        return getCommonPlatform()
                        .getPunishmentHandler()
                        .getActivePunishment(this.uniqueId, PunishmentType.MUTE)
                != null;
    }

    @Override
    public boolean isNormalMuted() {
        IPunishment punishment;
        return (punishment =
                                getCommonPlatform()
                                        .getPunishmentHandler()
                                        .getActivePunishment(this.uniqueId, PunishmentType.MUTE))
                        == null
                ? false
                : !punishment.isShadow();
    }

    @Override
    public boolean isShadowMuted() {
        IPunishment punishment;
        return (punishment =
                                getCommonPlatform()
                                        .getPunishmentHandler()
                                        .getActivePunishment(this.uniqueId, PunishmentType.MUTE))
                        == null
                ? false
                : punishment.isShadow();
    }

    @Nullable
    @Override
    public IPunishment getActiveIPBanWithoutSelf() {
        for (UUID uUID : this.alts) {
            IPunishment punishment;
            if ((punishment =
                            getCommonPlatform()
                                    .getPunishmentHandler()
                                    .getActiveIPPunishment(uUID, PunishmentType.BAN))
                    != null) {
                return punishment;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IPunishment getActiveIPMute() {
        IPunishment punishment;
        if ((punishment =
                                getCommonPlatform()
                                        .getPunishmentHandler()
                                        .getActivePunishment(this.uniqueId, PunishmentType.MUTE))
                        != null
                && punishment.isIp()) {
            return punishment;
        }
        for (UUID uUID : this.alts) {
            IPunishment punishment2;
            if ((punishment2 =
                            getCommonPlatform()
                                    .getPunishmentHandler()
                                    .getActiveIPPunishment(uUID, PunishmentType.MUTE))
                    != null) {
                return punishment2;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IPunishment getActiveIPBan() {
        IPunishment punishment;
        if ((punishment = this.getActiveBan()) != null && punishment.isIp()) {
            return punishment;
        }
        for (UUID uUID : this.alts) {
            IPunishment punishment2;
            if ((punishment2 =
                            getCommonPlatform()
                                    .getPunishmentHandler()
                                    .getActiveIPPunishment(uUID, PunishmentType.BAN))
                    != null) {
                return punishment2;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IPunishment getActiveCompetitiveBan() {
        return getCommonPlatform()
                .getPunishmentHandler()
                .getActivePunishment(this.uniqueId, PunishmentType.COMPETITIVE);
    }

    @Nullable
    @Override
    public IPunishment getActiveBan() {
        return getCommonPlatform()
                .getPunishmentHandler()
                .getActivePunishment(this.uniqueId, PunishmentType.BAN);
    }

    @Nullable
    @Override
    public IPunishment getActiveMute() {
        IPunishment punishment;
        return (punishment =
                                getCommonPlatform()
                                        .getPunishmentHandler()
                                        .getActiveIPPunishment(this.uniqueId, PunishmentType.MUTE))
                        != null
                ? punishment
                : getCommonPlatform()
                        .getPunishmentHandler()
                        .getActivePunishment(this.uniqueId, PunishmentType.MUTE);
    }

    @Nullable
    @Override
    public IPunishment getActiveBlacklist() {
        return getCommonPlatform()
                .getPunishmentHandler()
                .getActivePunishment(this.uniqueId, PunishmentType.BLACKLIST);
    }

    @Nullable
    @Override
    public IPunishment getActivePunishment(PunishmentType punishmentType) {
        switch (punishmentType) {
            case BAN:
                return this.getActiveBan();
            case MUTE:
                return this.getActiveMute();
            case BLACKLIST:
                return this.getActiveBlacklist();
            case COMPETITIVE:
                return this.getActiveCompetitiveBan();
            default:
                return null;
        }
    }

    @NotNull
    @Override
    public List<IPunishment> getPunishments(PunishmentType punishmentType) {
        return getCommonPlatform()
                .getPunishmentHandler()
                .getPunishments(this.uniqueId, punishmentType);
    }

    @Override
    public boolean hasActivePunishment(PunishmentType punishmentType) {
        return getCommonPlatform()
                .getPunishmentHandler()
                .hasActivePunishment(this.uniqueId, punishmentType);
    }

    @Override
    public boolean isStaff() {
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            if (((IGrant) iterator2.next()).getRank().isStaff()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMedia() {
        return this.hasAccessToPermission("core.media");
    }

    @Override
    public List<IGrant> getGrants() {
        return getCommonPlatform().getGrantHandler().getGrants(this.uniqueId);
    }

    @NotNull
    @Override
    public List<IGrant> getSortedGrants() {
        ArrayList<IGrant> values;
        (values = new ArrayList<>(this.getGrants())).sort(IGrant.COMPARATOR);
        return values;
    }

    @NotNull
    @Override
    public List<IGrant> getSortedGrantsReversed() {
        ArrayList<IGrant> values;
        (values = new ArrayList<>(this.getGrants())).sort(IGrant.COMPARATOR.reversed());
        return values;
    }

    @Override
    public void updatePermissions() {
        long now = System.currentTimeMillis();
        this.attachmentPermissions.clear();
        ArrayList values = new ArrayList();
        boolean matches = false;
        Iterator<IGrant> iterator2 = this.getGrants().iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).isActive() && grant.isScope()) {
                IRank rank;
                if ((rank = getCommonPlatform().getRankHandler().getRank(grant.getRankId()))
                        == null) {
                    getCommonPlatform().getGrantHandler().getRepository().delete(grant.getUuid());
                    matches = true;
                } else if (rank.isScope()) {
                    this.applyList(values, rank, true, new HashSet<>());
                }
            }
        }
        if (matches) {
            getCommonPlatform().getPacketHandler().send(new GrantsUpdatePacket(this.uniqueId));
        }
        for (IRank rank2 : getCommonPlatform().getRankHandler().getDefaultRanks()) {
            this.applyList(values, rank2, true, new HashSet<>());
        }
        Iterator iterator3 = new HashSet<>(this.permissions).iterator();
        while (iterator3.hasNext()) {
            IProfilePermission profilePermission;
            if (!(profilePermission = (IProfilePermission) iterator3.next()).hasExpired()
                    && profilePermission.isScope()) {
                values.add(
                        new RedisProfile.Entry(
                                Integer.MAX_VALUE,
                                profilePermission.getPermission().toLowerCase()));
            }
        }
        values.sort(comparator);
        HashMap entries = new HashMap();
        for (RedisProfile.Entry entry : (Iterable<RedisProfile.Entry>) ((Iterable) values)) {
            this.attachmentPermissions.put(entry.text, !entry.enabled);
            entries.put(entry.text, entry);
        }
        this.enabled = this.attachmentPermissions.getOrDefault("*", false);
        applyMap(this.attachmentPermissions, entries);
        this.getCacheByCache().invalidateAll();
        if (getCommonPlatform().isDebugMode()) {
            getCommonPlatform()
                    .getLogger()
                    .info(
                            "Updated "
                                    + this.name
                                    + "'s Permissions in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
        }
    }

    private void applyList(
            List<RedisProfile.Entry> values, IRank rank, boolean matches, Set<UUID> values2) {
        if (rank != null && values2.add(rank.getUuid())) {
            for (String text : rank.getAvailablePermissions()) {
                values.add(new RedisProfile.Entry(rank.getPriority(), text.toLowerCase()));
            }
            if (matches) {
                for (UUID uUID : rank.getInheritance()) {
                    IRank rank2;
                    if ((rank2 = getCommonPlatform().getRankHandler().getRank(uUID)) != null
                            && rank2.isScope()) {
                        this.applyList(
                                values, rank2, GlobalConfig.SERVER.PASSTHROUGH_CHILDREN, values2);
                    }
                }
            }
        }
    }

    static void applyMap(Map<String, Boolean> entries, Map<String, RedisProfile.Entry> entries2) {
        HashMap entries3 = new HashMap();
        Iterator<xyz.refinedev.phoenix.profile.RedisProfile.Entry> iterator2 =
                entries2.values().iterator();
        while (iterator2.hasNext()) {
            RedisProfile.Entry entry;
            String text2 = (entry = (RedisProfile.Entry) iterator2.next()).text;
            RedisProfile.Entry entry2 = entry;
            for (int index = 0; index < text2.length(); index++) {
                String text3;
                RedisProfile.Entry entry3;
                if (text2.charAt(index) == '.'
                        && !(text3 = text2.substring(0, index) + ".*").equals(text2)
                        && (entry3 = (RedisProfile.Entry) entries2.get(text3)) != null
                        && comparator.compare(entry3, entry2) > 0) {
                    entry2 = entry3;
                }
            }
            if (entry2 != entry) {
                entries3.put(text2, !entry2.enabled);
            }
        }
        entries.putAll(entries3);
    }

    @NotNull
    @Override
    public List<IGrant> getActiveGrants() {
        return getCommonPlatform().getGrantHandler().getActiveGrants(this.uniqueId);
    }

    @NotNull
    @Override
    public String getNameColor() {
        return this.getHighestRank().getColor();
    }

    @Nullable
    @Override
    public IGrant getBestGrant() {
        UUID uuid = this.getHighestRank().getUuid();
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).getRankId().equals(uuid)) {
                return grant;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IGrant getBestGeneralGrant() {
        UUID uuid = this.getGeneralRank().getUuid();
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).getRankId().equals(uuid)) {
                return grant;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public IRank getHighestRank() {
        IRank highestRank = getCommonPlatform().getGrantHandler().getHighestRank(this.uniqueId);
        IRank defaultRank = getCommonPlatform().getRankHandler().getDefaultRank();
        IRank rank;
        if (this.disguiseData.isRankDisguised()
                && (rank =
                                getCommonPlatform()
                                        .getRankHandler()
                                        .getRank(this.disguiseData.getRankId()))
                        != null) {
            return rank;
        } else {
            return highestRank.getPriority() >= defaultRank.getPriority()
                    ? highestRank
                    : defaultRank;
        }
    }

    @NotNull
    @Override
    public IRank getGeneralRank() {
        return getCommonPlatform().getGrantHandler().getGeneralRank(this.uniqueId);
    }

    @Override
    public boolean isVanished() {
        return getCommonPlatform().getModUtilsHook().isVanished(this.uniqueId);
    }

    @Override
    public int getFakePriority() {
        return this.getHighestRank().getPriority();
    }

    @Override
    public int getPriority() {
        return this.getGeneralRank().getPriority();
    }

    @NotNull
    @Override
    public String getFirstLogin(List<ILogin> values) {
        return values.isEmpty() ? "N/A" : DateUtils.getDate(((ILogin) values.get(0)).getLogin());
    }

    @NotNull
    @Override
    public String getLastLogin(List<ILogin> values) {
        ILogin login;
        return (login = this.getMostRecentLogin(values)) == null
                ? "N/A"
                : DateUtils.getDate(login.getLogin());
    }

    @Nullable
    @Override
    public ILogin getMostRecentLogin(List<ILogin> values) {
        return values.stream().max(Comparator.comparingLong(ILogin::getLogin)).orElse(null);
    }

    @NotNull
    @Override
    public String getLastSeenAgo(List<ILogin> values) {
        ILogin login;
        if ((login = this.getMostRecentLogin(values)) == null) {
            return "N/A";
        }
        if (getCommonPlatform().getNetworkHandler().isOnline(this.uniqueId)) {
            return "Online (" + DateUtils.formatTimeMillis(login.getDuration()) + ")";
        }
        Calendar instance = Calendar.getInstance();
        Calendar instance2 = Calendar.getInstance();
        instance.setTime(new Date(login.getLogin()));
        instance2.setTime(new Date(System.currentTimeMillis()));
        return DateUtils.formatDateDiff(instance, instance2) + " ago";
    }

    @Override
    public long getPlayTime(List<ILogin> values) {
        long value = 0L;
        for (ILogin login : values) {
            value += login.getDuration();
        }
        return value;
    }

    @Override
    public boolean hasSubscription() {
        if (this.disguiseData.isRankDisguised()) {
            return false;
        }
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            if (((IGrant) iterator2.next()).getRank().isSubscription()) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public String getSubRanks() {
        if (this.disguiseData.isRankDisguised()) {
            return null;
        }
        if (!this.hasSubscription()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).getRank().isSubscription()) {
                stringBuilder.append(grant.getRank().getPrefix());
            }
        }
        return stringBuilder.toString();
    }

    @Nullable
    @Override
    public IGrant getGrantAllScopes(IRank rank) {
        Iterator<IGrant> iterator2 = this.getGrants().iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).isActiveOnOneScope()
                    && grant.getRankId().equals(rank.getUuid())) {
                return grant;
            }
        }
        return null;
    }

    @Override
    public boolean hasRank(UUID uUID) {
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            if (((IGrant) iterator2.next()).getRankId().equals(uUID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasRank(IRank rank) {
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            if (((IGrant) iterator2.next()).getRankId().equals(rank.getUuid())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasRankAndCantUpgrade(IRank rank) {
        Iterator<IGrant> iterator2 = this.getActiveGrants().iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).getRankId().equals(rank.getUuid())
                    && grant.isPermanent()) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public String getNameWithColorCoverDisguise(String text) {
        return text.equalsIgnoreCase(this.name)
                ? this.getNameWithColorNoDisguise()
                : this.getNameWithColor();
    }

    @NotNull
    @Override
    public String getNameWithColor() {
        return (this.hasSubscription() ? this.getSubRanks() : "")
                + this.getHighestRank().getColor()
                + this.getPlayerName();
    }

    @NotNull
    @Override
    public String getNameWithColorNoDisguise() {
        return this.getGeneralRank().getColor() + this.name;
    }

    @NotNull
    @Override
    public String getPlayerName() {
        return this.disguiseData.isDisguised() ? this.disguiseData.getDisguiseName() : this.name;
    }

    @Override
    public void loadAlts() {
        this.loadAlts(true, this.currentAddress, this.currentSubnet);
    }

    @Override
    public void loadAlts(boolean matches) {
        this.loadAlts(matches, this.currentAddress, this.currentSubnet);
    }

    @Override
    public IAntiVPNData fetchLatestAntiVPNData() {
        return getCommonPlatform()
                .getAntiVPNHandler()
                .fetchLatestVpnDataRedis(this.uniqueId, this.currentAddress);
    }

    @Override
    public void loadAlts(boolean matches, String text, String text2) {
        if (GlobalConfig.SERVER.ALTS.ENABLED) {
            if (getCommonPlatform().getAltHandler() != null) {
                long now = System.currentTimeMillis();
                getCommonPlatform().getAltHandler().refreshLinks(this.uniqueId);
                if (this.siblings != null && !this.siblings.isEmpty()) {
                    Iterator iterator2 = new HashSet<>(this.siblings).iterator();
                    while (iterator2.hasNext()) {
                        UUID uUID;
                        if ((uUID = (UUID) iterator2.next()) != null) {
                            getCommonPlatform()
                                    .getAltHandler()
                                    .setStatus(this.uniqueId, uUID, AltLinkStatus.TRUSTED);
                        }
                    }
                    this.siblings.clear();
                }
                Set<UUID> values =
                        getCommonPlatform()
                                .getAltHandler()
                                .altsAtOrAbove(
                                        this.uniqueId, GlobalConfig.SERVER.ALTS.THRESHOLDS.LINK);
                this.alts.clear();
                this.alts.addAll(values);
                this.alts.remove(this.uniqueId);
                this.alts.removeIf(Objects::isNull);
                if (matches) {
                    this.saveProfile();
                }
                if (getCommonPlatform().isDebugMode()) {
                    getCommonPlatform()
                            .getLogger()
                            .info(
                                    "Loaded "
                                            + this.name
                                            + "'s Alts in "
                                            + (System.currentTimeMillis() - now)
                                            + "ms.");
                }
            }
        }
    }

    @NotNull
    @Override
    public CompletableFuture<List<IPunishment>> getPunishmentsRemoved() {
        return getCommonPlatform().getPunishmentHandler().findByRemovedBy(this.uniqueId);
    }

    @NotNull
    @Override
    public CompletableFuture<List<IPunishment>> getPunishmentsIssued() {
        return getCommonPlatform().getPunishmentHandler().findByIssuedBy(this.uniqueId);
    }

    @NotNull
    @Override
    public CompletableFuture<List<IGrant>> getGrantsIssued() {
        return getCommonPlatform().getGrantHandler().findByIssuedBy(this.uniqueId);
    }

    @Override
    public boolean isIgnoring(UUID uUID) {
        return this.ignoring.contains(uUID);
    }

    @NotNull
    @Override
    public List<String> getNiceIgnoreNames() {
        ArrayList<String> values = new ArrayList<>();
        Iterator<UUID> iterator2 = this.ignoring.iterator();
        while (iterator2.hasNext()) {
            UUID uUID;
            IProfile profile;
            if ((uUID = (UUID) iterator2.next()) != null
                    && (profile = getCommonPlatform().getProfileHandler().getProfile(uUID))
                            != null) {
                values.add(profile.getNameWithColor());
            }
        }
        return values;
    }

    @NotNull
    @Override
    public String getChatChannelName() {
        return this.metadata.getOrDefault("channel", "Default");
    }

    @Nullable
    @Override
    public String getSetting(String text) {
        return this.metadata.get(text);
    }

    @Override
    public boolean hasMetadata(String text) {
        return this.metadata.get(text) != null;
    }

    @Deprecated
    @NotNull
    @Override
    public String getMetadata(String text, String text2) {
        return this.metadata.getOrDefault(text, text2);
    }

    @Override
    public String getSetting(String text, String text2) {
        return this.metadata.getOrDefault(text, text2);
    }

    @Override
    public int getIntSetting(String text) {
        String text2;
        return (text2 = this.metadata.get(text)) != null ? Integer.parseInt(text2) : 0;
    }

    @Override
    public int getIntSetting(String text, int value) {
        String text2;
        return (text2 = this.metadata.get(text)) != null ? Integer.parseInt(text2) : value;
    }

    @Override
    public double getDoubleSetting(String text) {
        String text2;
        return (text2 = this.metadata.get(text)) != null ? Double.parseDouble(text2) : 0.0;
    }

    @Override
    public double getDoubleSetting(String text, double value) {
        String text2;
        return (text2 = this.metadata.get(text)) != null ? Double.parseDouble(text2) : value;
    }

    @Override
    public boolean isSetting(String text) {
        return this.isMetadata(text, true);
    }

    @Override
    public boolean isMetadata(String text) {
        String text2;
        return (text2 = this.metadata.get(text)) == null || Boolean.parseBoolean(text2);
    }

    @Override
    public boolean isMetadata(String text, boolean matches) {
        String text2;
        return (text2 = this.metadata.get(text)) != null ? Boolean.parseBoolean(text2) : matches;
    }

    @Override
    public void updateMetadata(String text, Object result, boolean matches) {
        this.metadata.put(text, result.toString());
        if (matches) {
            this.saveAndUpdate(false);
        }
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public boolean checkPermission(String text, boolean matches) {
        return this.checkPermissionTristate(text, matches) == Tristate.TRUE;
    }

    private Cache<String, Tristate> getCacheByCache() {
        Cache cache2 = this.cache;
        if (this.cache == null) {
            cache2 = Caffeine.newBuilder().maximumSize(time).build();
            this.cache = cache2;
        }
        return cache2;
    }

    @Override
    public Tristate checkPermissionTristate(String text, boolean matches) {
        text = text.toLowerCase();
        Boolean matches2;
        if ((matches2 = this.attachmentPermissions.get(text)) != null && !matches2) {
            return Tristate.FALSE;
        }
        if (matches) {
            return Tristate.TRUE;
        }
        if (this.enabled && GlobalConfig.SERVER.STAR_PERM_ENABLED) {
            return Tristate.TRUE;
        }
        if (matches2 != null) {
            return Tristate.TRUE;
        }
        Cache cache;
        Tristate tristate;
        if ((tristate = (Tristate) (cache = this.getCacheByCache()).getIfPresent(text)) != null) {
            return tristate;
        }
        Tristate tristate2 = Tristate.UNDEFINED;
        if (text.contains(".")) {
            for (int index = 0; index < text.length(); index++) {
                if (text.charAt(index) == '.') {
                    String text2 = text.substring(0, index);
                    Boolean matches3;
                    if ((matches3 = this.attachmentPermissions.get(text2 + ".*")) != null
                            && !matches3) {
                        tristate2 = Tristate.FALSE;
                        break;
                    }
                    if (matches3 != null) {
                        tristate2 = Tristate.TRUE;
                        break;
                    }
                }
            }
        }
        cache.put(text, tristate2);
        return tristate2;
    }

    @Override
    public boolean hasAccessToPermission(String text) {
        boolean matches;
        String text2 =
                (matches = text.startsWith("-"))
                        ? text.substring(1).toLowerCase()
                        : text.toLowerCase();
        Boolean matches2;
        return (matches2 = this.attachmentPermissions.get(text2)) == null
                ? false
                : matches == !matches2;
    }

    @Override
    public boolean hasPrivatePermission(String text) {
        return this.getPermission(text) != null;
    }

    @Override
    public boolean hasActivePrivatePermission(String text) {
        IProfilePermission profilePermission;
        return (profilePermission = this.getPermission(text)) != null
                && !profilePermission.hasExpired();
    }

    @Override
    public void addPermission(String text, long value, List<IScope> values) {
        this.permissions.add(new ProfilePermission(text, value, values));
    }

    @Override
    public void removePermission(String text) {
        IProfilePermission profilePermission;
        if ((profilePermission = this.getPermission(text)) != null) {
            this.permissions.remove(profilePermission);
        }
    }

    @Nullable
    @Override
    public IProfilePermission getPermission(String text) {
        Iterator<IProfilePermission> iterator2 = this.permissions.iterator();
        while (iterator2.hasNext()) {
            IProfilePermission profilePermission;
            if ((profilePermission = (IProfilePermission) iterator2.next())
                    .getPermission()
                    .equalsIgnoreCase(text)) {
                return profilePermission;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IPunishment getAltPunishmentToKick() {
        if (getCommonPlatform().isDebugMode()) {
            getCommonPlatform().getLogger().info("Fetching alt punishment for: " + this.uniqueId);
            getCommonPlatform()
                    .getLogger()
                    .info(
                            "Alts: "
                                    + this.alts.stream()
                                            .map(UUID::toString)
                                            .collect(Collectors.joining(", ")));
        }
        if (getCommonPlatform().getAltHandler() == null) {
            return null;
        }
        List<IAltLink> links = getCommonPlatform().getAltHandler().getLinks(this.uniqueId);
        HashSet<UUID> values = new HashSet<>();
        int value = GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE;
        Iterator iterator2 = links.iterator();
        while (iterator2.hasNext()) {
            IAltLink altLink;
            if (AltEvidenceTypes.matchesEnabled(
                    (altLink = (IAltLink) iterator2.next()).getScore(),
                    altLink.getStatus(),
                    value)) {
                values.add(altLink.other(this.uniqueId));
            }
        }
        ArrayList values2 = new ArrayList(values);
        Set values3 =
                FastStreams.mapAndFilterArrayListToSet(
                        values2, result -> !this.siblings.contains(result), UUID::toString);
        List<IPunishment> values4 =
                getCommonPlatform()
                        .getPunishmentHandler()
                        .getRepository()
                        .fetchAllSync(
                                Filters.or(
                                        Filters.and(
                                                Filters.in("target", values3),
                                                Filters.neq("punishmentType", "MUTE"),
                                                Filters.neq("punishmentType", "KICK"),
                                                Filters.neq("punishmentType", "WARN")),
                                        Filters.eq(
                                                "address",
                                                !GlobalConfig.SERVER.ALTS.IP_PUNISHMENTS_ONLY_ALTS
                                                        ? this.currentAddress
                                                        : "thisipisnotrealihopenohashcanbelikethis")));
        if (getCommonPlatform().isDebugMode()) {
            getCommonPlatform()
                    .getLogger()
                    .info("Fetched " + values4.size() + " alt punishment(s) for: " + this.uniqueId);
        }
        Optional optional;
        if ((optional =
                        FastStreams.streamFilterOptional(
                                values4,
                                result2 ->
                                        result2.isActive()
                                                && result2.getPunishmentType()
                                                        == PunishmentType.BLACKLIST))
                .isPresent()) {
            if (getCommonPlatform().isDebugMode()) {
                getCommonPlatform().getLogger().info("Alt blacklist found for: " + this.uniqueId);
            }
            return this.findPunishment((IPunishment) optional.get(), links);
        } else {
            Optional optional2;
            if (!(optional2 =
                            FastStreams.streamFilterOptional(
                                    values4,
                                    result2 ->
                                            result2.isActive()
                                                    && result2.isIp()
                                                    && result2.getPunishmentType()
                                                            == PunishmentType.BAN))
                    .isPresent()) {
                return null;
            }
            if (getCommonPlatform().isDebugMode()) {
                getCommonPlatform().getLogger().info("Alt ipban found for: " + this.uniqueId);
            }
            return this.findPunishment((IPunishment) optional2.get(), links);
        }
    }

    private IPunishment findPunishment(IPunishment punishment, List<IAltLink> values) {
        IAltLink altLink = null;
        Iterator<IAltLink> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IAltLink altLink2;
            if ((altLink2 = (IAltLink) iterator2.next()).involves(punishment.getTarget())) {
                altLink = altLink2;
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (altLink != null) {
            for (IAltSignal altSignal : altLink.getSignals()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder
                        .append(altSignal.getName())
                        .append(' ')
                        .append(String.format(Locale.ROOT, "%+.1f", altSignal.getPoints()));
            }
        }
        String text =
                altLink == null
                        ? " (no scored link - direct address match)"
                        : " (score " + altLink.getScore() + ") [" + stringBuilder + "]";
        getCommonPlatform()
                .getLogger()
                .info(
                        "(Alts) Kicking "
                                + this.name
                                + ": "
                                + punishment.getPunishmentType()
                                + " on alt "
                                + punishment.getTarget()
                                + text);
        getCommonPlatform()
                .getMetricsHandler()
                .metric("phoenix_alt_kick")
                .findMetric("mode", "enforce")
                .findMetric("score", altLink == null ? 0 : altLink.getScore())
                .findMetric("link_based", altLink == null ? 0 : 1)
                .initializeByMetricsHandler();
        return punishment;
    }

    @NotNull
    @Override
    public List<IPunishment> getAltPunishments() {
        ArrayList<IPunishment> values = new ArrayList<>();
        Set values2;
        if ((values2 = FastStreams.mapArrayListToSet(this.alts, UUID::toString)).isEmpty()) {
            return Collections.emptyList();
        }
        for (IPunishment punishment :
                getCommonPlatform()
                        .getPunishmentHandler()
                        .getRepository()
                        .fetchAllSync(Filters.in("target", values2))) {
            if (!this.siblings.contains(punishment.getTarget())
                    && (punishment.getPunishmentType() == PunishmentType.BLACKLIST
                            || punishment.getPunishmentType() == PunishmentType.BAN
                            || punishment.getPunishmentType() == PunishmentType.MUTE)
                    && !punishment.isntActive()) {
                values.add(punishment);
            }
        }
        return values;
    }

    @Override
    public List<INote> getSortedNotes() {
        return this.notes.stream()
                .sorted(Comparator.comparingLong(INote::getIssuedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public long getGlobalRemaining(String text) {
        ICooldown cooldown;
        return (cooldown = this.getGlobalCooldown(text)) != null ? cooldown.getRemaining() : 0L;
    }

    @Nullable
    @Override
    public ICooldown getGlobalCooldown(String text) {
        Iterator<ICooldown> iterator2 = this.globalCooldowns.iterator();
        while (iterator2.hasNext()) {
            ICooldown cooldown;
            if ((cooldown = (ICooldown) iterator2.next()).getName().equalsIgnoreCase(text)) {
                return cooldown;
            }
        }
        return null;
    }

    @Override
    public boolean hasGlobalCooldown(String text) {
        ICooldown cooldown;
        return (cooldown = this.getGlobalCooldown(text)) == null
                ? false
                : cooldown.getExpire() >= System.currentTimeMillis();
    }

    @Override
    public long getLocalRemaining(String text) {
        return getCommonPlatform().getCooldownHandler().getRemaining(text, this.uniqueId);
    }

    @Override
    public boolean hasLocalCooldown(String text) {
        return getCommonPlatform().getCooldownHandler().isOnCooldown(text, this.uniqueId);
    }

    @Override
    public INote getNote(UUID uUID) {
        Iterator<INote> iterator2 = this.notes.iterator();
        while (iterator2.hasNext()) {
            INote note;
            if ((note = (INote) iterator2.next()).getUuid().equals(uUID)) {
                return note;
            }
        }
        return null;
    }

    @Override
    public void postProcess() {
        this.attachmentPermissions = new ConcurrentHashMap<>();
        if (this.permissions == null) {
            this.permissions = new HashSet<>();
        }
        if (this.ignoring == null) {
            this.ignoring = new HashSet<>();
        }
        if (this.globalCooldowns == null) {
            this.globalCooldowns = new ArrayList<>();
        }
        if (this.alts == null) {
            this.alts = new HashSet<>();
        }
        if (this.siblings == null) {
            this.siblings = new HashSet<>();
        }
        if (this.notes == null) {
            this.notes = new ArrayList<>();
        }
        if (this.nameHistory == null) {
            this.nameHistory = new ArrayList<>();
        }
        if (this.recentPings == null) {
            this.recentPings = new ArrayList<>();
        }
        if (this.logoutBuckets == null) {
            this.logoutBuckets = new LinkedHashSet<>();
        }
        if (this.currentAddress == null) {
            this.currentAddress = "";
        }
        if (this.currentSubnet == null) {
            this.currentSubnet = "";
        }
        if (this.knownAddresses == null) {
            this.knownAddresses = new HashSet<>();
        }
        if (this.disguiseData == null) {
            this.disguiseData = new DisguiseData();
        }
        if (this.tagName == null) {
            this.tagName = "";
        }
        if (this.secretKey == null) {
            this.secretKey = "";
        }
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            RedisProfile redisProfile = (RedisProfile) result;
            return this.uniqueId.equals(redisProfile.uniqueId);
        } else {
            return false;
        }
    }

    @Override
    public void applyFields(IProfile profile, Set<String> values) {
        if (!(profile instanceof TrackedEntity)) {
            throw new IllegalArgumentException(
                    "Profile source must be TrackedEntity, got " + profile.getClass().getName());
        }
        this.applyFieldsFrom((TrackedEntity) profile, values, getCommonPlatform().getGson());
    }

    @Override
    public UUID getUuid() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getNameToLowercase() {
        return this.nameToLowercase;
    }

    @Override
    public Set<IProfilePermission> getPermissions() {
        return this.permissions;
    }

    @Override
    public Map<String, Boolean> getAttachmentPermissions() {
        return this.attachmentPermissions;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Cache<String, Tristate> getCache() {
        return this.cache;
    }

    @Override
    public Set<UUID> getIgnoring() {
        return this.ignoring;
    }

    @Override
    public long getAfkTime() {
        return this.afkTime;
    }

    @Override
    public List<ICooldown> getGlobalCooldowns() {
        return this.globalCooldowns;
    }

    @Override
    public Set<UUID> getAlts() {
        return this.alts;
    }

    public Set<UUID> getSiblings() {
        return this.siblings;
    }

    @Override
    public List<INote> getNotes() {
        return this.notes;
    }

    @Override
    public List<INameChange> getNameHistory() {
        return this.nameHistory;
    }

    @Override
    public IClientFingerprint getClientFingerprint() {
        return this.clientFingerprint;
    }

    @Override
    public String getSkinHash() {
        return this.skinHash;
    }

    @Override
    public List<Integer> getRecentPings() {
        return this.recentPings;
    }

    @Override
    public Set<String> getLogoutBuckets() {
        return this.logoutBuckets;
    }

    @Override
    public String getCurrentAddress() {
        return this.currentAddress;
    }

    @Override
    public String getCurrentSubnet() {
        return this.currentSubnet;
    }

    @Override
    public Set<String> getKnownAddresses() {
        return this.knownAddresses;
    }

    @Override
    public IDisguiseData getDisguiseData() {
        return this.disguiseData;
    }

    @Override
    public String getTagName() {
        return this.tagName;
    }

    @Override
    public ICustomTag getCustomTag() {
        return this.customTag;
    }

    @Override
    public String getAuthType() {
        return this.authType;
    }

    @Override
    public boolean isTwoFactor() {
        return this.twoFactor;
    }

    @Override
    public String getSecretKey() {
        return this.secretKey;
    }

    @Override
    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    public void setNameToLowercase(String nameToLowercase) {
        this.nameToLowercase = nameToLowercase;
    }

    @Override
    public void setPermissions(Set<IProfilePermission> permissions) {
        this.permissions = permissions;
    }

    public void setAttachmentPermissions(Map<String, Boolean> entries) {
        this.attachmentPermissions = entries;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCache(Cache<String, Tristate> cache2) {
        this.cache = cache2;
    }

    public void setIgnoring(Set<UUID> ignoring) {
        this.ignoring = ignoring;
    }

    @Override
    public void setAfkTime(long afkTime) {
        this.afkTime = afkTime;
    }

    public void setGlobalCooldowns(List<ICooldown> globalCooldowns) {
        this.globalCooldowns = globalCooldowns;
    }

    @Override
    public void setAlts(Set<UUID> alts) {
        this.alts = alts;
    }

    public void setSiblings(Set<UUID> siblings) {
        this.siblings = siblings;
    }

    public void setNotes(List<INote> notes) {
        this.notes = notes;
    }

    public void setNameHistory(List<INameChange> nameHistory) {
        this.nameHistory = nameHistory;
    }

    @Override
    public void setClientFingerprint(IClientFingerprint clientFingerprint2) {
        this.clientFingerprint = clientFingerprint2;
    }

    @Override
    public void setSkinHash(String skinHash) {
        this.skinHash = skinHash;
    }

    @Override
    public void setRecentPings(List<Integer> recentPings) {
        this.recentPings = recentPings;
    }

    @Override
    public void setLogoutBuckets(Set<String> logoutBuckets) {
        this.logoutBuckets = logoutBuckets;
    }

    @Override
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    @Override
    public void setCurrentSubnet(String currentSubnet) {
        this.currentSubnet = currentSubnet;
    }

    public void setKnownAddresses(Set<String> knownAddresses) {
        this.knownAddresses = knownAddresses;
    }

    @Override
    public void setDisguiseData(IDisguiseData disguiseData2) {
        this.disguiseData = disguiseData2;
    }

    @Override
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public void setCustomTag(ICustomTag customTag2) {
        this.customTag = customTag2;
    }

    @Override
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public void setTwoFactor(boolean twoFactor) {
        this.twoFactor = twoFactor;
    }

    @Override
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void setMetadata(Map<String, String> entries) {
        this.metadata = entries;
    }

    public RedisProfile(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    static final class Entry {

        final int count;

        final boolean enabled;

        final String text;

        Entry(int count, String text2) {
            this.count = count;
            this.enabled = text2.startsWith("-");
            this.text = this.enabled ? text2.substring(1) : text2;
        }
    }
}

package xyz.refinedev.phoenix.profile;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.alt.IClientFingerprint;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.profile.cooldown.ICooldown;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.notes.INote;
import xyz.refinedev.phoenix.profile.permission.IProfilePermission;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Tristate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IProfile {

    String text = "00000000-0000-0000-0000-000000000000";

    static UUID getConsoleUuid() {
        return UUID.fromString(text);
    }

    void loadProfile();

    void loadProfile(boolean matches);

    void loadProfileNoSaveNoRedis();

    void loadProfile(IProfile profile);

    void applyFields(IProfile profile, Set<String> values);

    void setName(String text);

    void saveProfile();

    void saveProfileSync();

    void saveAndUpdate(boolean matches);

    void saveAndUpdateSync(boolean matches);

    void saveAndUpdate();

    List<IPunishment> getPunishments();

    @Nullable
    ITag getTag();

    void setTag(ITag tag);

    boolean isBanned();

    boolean isCompetitiveBanned();

    boolean isBlacklisted();

    boolean isMuted();

    boolean isIPMuted();

    boolean isIPBanned();

    boolean shouldSendEvadeAlert();

    boolean isNormalOrShadowMuted();

    boolean isNormalMuted();

    boolean isShadowMuted();

    @Nullable
    IPunishment getActiveIPBanWithoutSelf();

    @Nullable
    IPunishment getActiveIPMute();

    @Nullable
    IPunishment getActiveIPBan();

    @Nullable
    IPunishment getActiveCompetitiveBan();

    @Nullable
    IPunishment getActiveBan();

    @Nullable
    IPunishment getActiveMute();

    @Nullable
    IPunishment getActiveBlacklist();

    @Nullable
    IPunishment getActivePunishment(PunishmentType punishmentType);

    @NotNull
    List<IPunishment> getPunishments(PunishmentType punishmentType);

    boolean hasActivePunishment(PunishmentType punishmentType);

    boolean isStaff();

    boolean isMedia();

    List<IGrant> getGrants();

    @NotNull
    List<IGrant> getSortedGrants();

    @NotNull
    List<IGrant> getSortedGrantsReversed();

    void updatePermissions();

    @NotNull
    List<IGrant> getActiveGrants();

    @NotNull
    String getNameColor();

    @Nullable
    IGrant getBestGrant();

    @Nullable
    IGrant getBestGeneralGrant();

    @NotNull
    IRank getHighestRank();

    @NotNull
    IRank getGeneralRank();

    boolean isVanished();

    int getFakePriority();

    int getPriority();

    @NotNull
    String getFirstLogin(List<ILogin> values);

    @NotNull
    String getLastLogin(List<ILogin> values);

    @Nullable
    ILogin getMostRecentLogin(List<ILogin> values);

    @NotNull
    String getLastSeenAgo(List<ILogin> values);

    long getPlayTime(List<ILogin> values);

    boolean hasSubscription();

    @Nullable
    String getSubRanks();

    @Nullable
    IGrant getGrantAllScopes(IRank rank);

    boolean hasRank(UUID uUID);

    boolean hasRank(IRank rank);

    boolean hasRankAndCantUpgrade(IRank rank);

    @NotNull
    String getNameWithColorCoverDisguise(String text);

    @NotNull
    String getNameWithColor();

    @NotNull
    String getNameWithColorNoDisguise();

    @NotNull
    String getPlayerName();

    void loadAlts();

    void loadAlts(boolean matches);

    IAntiVPNData fetchLatestAntiVPNData();

    void loadAlts(boolean matches, String text, String text2);

    @NotNull
    CompletableFuture<List<IPunishment>> getPunishmentsRemoved();

    @NotNull
    CompletableFuture<List<IPunishment>> getPunishmentsIssued();

    @NotNull
    CompletableFuture<List<IGrant>> getGrantsIssued();

    boolean isIgnoring(UUID uUID);

    @NotNull
    List<String> getNiceIgnoreNames();

    @NotNull
    String getChatChannelName();

    @Nullable
    String getSetting(String text);

    boolean hasMetadata(String text);

    @Deprecated
    @NotNull
    String getMetadata(String text, String text2);

    String getSetting(String text, String text2);

    int getIntSetting(String text);

    int getIntSetting(String text, int value);

    double getDoubleSetting(String text);

    double getDoubleSetting(String text, double value);

    boolean isSetting(String text);

    boolean isMetadata(String text);

    boolean isMetadata(String text, boolean matches);

    void updateMetadata(String text, Object result, boolean matches);

    UUID getUniqueId();

    boolean checkPermission(String text, boolean matches);

    Tristate checkPermissionTristate(String text, boolean matches);

    boolean hasAccessToPermission(String text);

    boolean hasPrivatePermission(String text);

    boolean hasActivePrivatePermission(String text);

    void removePermission(String text);

    void addPermission(String text, long value, List<IScope> values);

    @Nullable
    IProfilePermission getPermission(String text);

    @Nullable
    IPunishment getAltPunishmentToKick();

    @NotNull
    List<IPunishment> getAltPunishments();

    List<INote> getSortedNotes();

    long getGlobalRemaining(String text);

    @Nullable
    ICooldown getGlobalCooldown(String text);

    boolean hasGlobalCooldown(String text);

    long getLocalRemaining(String text);

    boolean hasLocalCooldown(String text);

    INote getNote(UUID uUID);

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getUuid();

    String getName();

    Set<IProfilePermission> getPermissions();

    Map<String, Boolean> getAttachmentPermissions();

    Set<UUID> getIgnoring();

    long getAfkTime();

    void setAfkTime(long value);

    List<ICooldown> getGlobalCooldowns();

    Set<UUID> getAlts();

    IClientFingerprint getClientFingerprint();

    void setClientFingerprint(IClientFingerprint clientFingerprint);

    String getSkinHash();

    void setSkinHash(String text);

    List<Integer> getRecentPings();

    void setRecentPings(List<Integer> values);

    Set<String> getLogoutBuckets();

    void setLogoutBuckets(Set<String> values);

    List<INote> getNotes();

    default List<INameChange> getNameHistory() {
        return Collections.emptyList();
    }

    String getCurrentAddress();

    String getCurrentSubnet();

    Set<String> getKnownAddresses();

    IDisguiseData getDisguiseData();

    ICustomTag getCustomTag();

    String getAuthType();

    String getTagName();

    String getNameToLowercase();

    boolean isTwoFactor();

    String getSecretKey();

    Map<String, String> getMetadata();

    void setPermissions(Set<IProfilePermission> values);

    void setAlts(Set<UUID> values);

    void setCurrentAddress(String text);

    void setCurrentSubnet(String text);

    void setDisguiseData(IDisguiseData disguiseData);

    void setTagName(String text);

    void setCustomTag(ICustomTag customTag);

    void setAuthType(String text);

    void setTwoFactor(boolean matches);

    void setSecretKey(String text);

    void setMetadata(Map<String, String> entries);
}

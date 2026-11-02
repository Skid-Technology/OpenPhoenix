package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.repository.IProfileRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IProfileHandler {

    @NotNull
    IProfile createProfile(UUID uUID, String text);

    @Nullable
    IProfile getCachedProfile(UUID uUID);

    @Nullable
    IProfile getProfile(UUID uUID);

    Set<IProfile> getProfiles(Collection<UUID> values);

    boolean isInOnlineCache(String text);

    @Nullable
    IProfile getLocalProfileByName(String text);

    boolean hasProfile(String text);

    boolean isInOnlineCache(UUID uUID);

    @Nullable
    IProfile getProfileFromName(String text);

    @Nullable
    IProfile getProfileFromCapitalizedName(String text);

    @Nullable
    UUID getUuidFromName(String text);

    List<IProfile> getProfilesFromName(String text);

    boolean hasMultipleProfiles(String text);

    IProfileRepository getRepository();

    IProfileCacheRepository getCacheRepository();

    Set<String> getMultipleProfiles();
}

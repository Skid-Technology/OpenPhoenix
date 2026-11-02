package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.ConsoleProfile;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.RedisProfile;
import xyz.refinedev.phoenix.repository.IProfileRepository;
import xyz.refinedev.phoenix.repository.redis.RedisProfileCacheRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileHandler implements IProfileHandler {

    private final CommonPlatform platform;

    private final IProfileRepository repository;

    private final IProfileCacheRepository cacheRepository;

    private final Set<String> multipleProfiles = ConcurrentHashMap.newKeySet();

    public ProfileHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).profiles();
        this.cacheRepository =
                new RedisProfileCacheRepository(commonPlatform, RedisProfile.class, "px-profiles");
        ConsoleProfile consoleProfile = new ConsoleProfile();
        this.repository.addToOnlineCache(consoleProfile.getUniqueId(), consoleProfile);
    }

    @NotNull
    @Override
    public IProfile createProfile(UUID uUID, String text) {
        IProfile profile;
        if ((profile = this.repository.getFromCache(uUID).orElse(null)) != null) {
            return profile;
        }
        (profile = new RedisProfile(uUID)).setName(text);
        this.repository.addToCache(uUID, profile);
        return profile;
    }

    @Nullable
    @Override
    public IProfile getCachedProfile(UUID uUID) {
        return uUID == null ? null : this.repository.getFromCache(uUID).orElse(null);
    }

    @Nullable
    @Override
    public IProfile getProfile(UUID uUID) {
        if (uUID == null) {
            return null;
        } else {
            Optional optional;
            if ((optional = this.repository.getFromCache(uUID)).isPresent()) {
                return (IProfile) optional.get();
            } else if ((optional = this.cacheRepository.fetchSync(uUID)).isPresent()) {
                this.repository.addToCache(uUID, (IProfile) optional.get());
                return (IProfile) optional.get();
            } else if ((optional = this.repository.fetchSync(uUID)).isPresent()) {
                this.repository.addToCache(uUID, (IProfile) optional.get());
                return (IProfile) optional.get();
            } else {
                return null;
            }
        }
    }

    @Override
    public Set<IProfile> getProfiles(Collection<UUID> values) {
        HashSet<IProfile> values2 = new HashSet<>();
        HashSet<UUID> values3 = new HashSet<>();
        Iterator<UUID> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            UUID uUID;
            if ((uUID = (UUID) iterator2.next()) != null) {
                Optional optional;
                if ((optional = this.repository.getFromCache(uUID)).isPresent()) {
                    values2.add((IProfile) optional.get());
                } else if ((optional = this.cacheRepository.fetchSync(uUID)).isPresent()) {
                    this.repository.addToCache(uUID, (IProfile) optional.get());
                    values2.add((IProfile) optional.get());
                } else {
                    values3.add(uUID);
                }
            }
        }
        values2.addAll(
                this.repository.fetchAllSync(
                        Filters.in(
                                "_id",
                                values3.stream().map(UUID::toString).collect(Collectors.toSet()))));
        return values2;
    }

    @Override
    public boolean isInOnlineCache(String text) {
        Stream<IProfile> stream2 = this.repository.getOnlineCache().values().stream();
        return stream2.anyMatch(result -> result.getName().equalsIgnoreCase(text));
    }

    @Nullable
    @Override
    public IProfile getLocalProfileByName(String text) {
        Iterator iterator2 = this.repository.getOnlineCache().values().iterator();
        while (iterator2.hasNext()) {
            IProfile profile;
            if ((profile = (IProfile) iterator2.next()).getName().equalsIgnoreCase(text)) {
                return profile;
            }
        }
        iterator2 = this.repository.getCache().asMap().values().iterator();
        while (iterator2.hasNext()) {
            IProfile profile2;
            if ((profile2 = (IProfile) iterator2.next()).getName().equalsIgnoreCase(text)) {
                return profile2;
            }
        }
        return null;
    }

    @Override
    public boolean hasProfile(String text) {
        return this.getLocalProfileByName(text) != null ? true : this.getUuidFromName(text) != null;
    }

    @Override
    public boolean isInOnlineCache(UUID uUID) {
        return this.repository.isInOnlineCache(uUID);
    }

    @Nullable
    @Override
    public IProfile getProfileFromName(String text) {
        return this.repository
                .fetchSync(Filters.eq("nameToLowercase", toLowerCase(text)))
                .orElse(null);
    }

    @Nullable
    @Override
    public IProfile getProfileFromCapitalizedName(String text) {
        return this.repository
                .fetchSync(Filters.eq("nameToLowercase", toLowerCase(text)))
                .orElse(null);
    }

    private static String toLowerCase(String text) {
        return text.toLowerCase(Locale.ROOT);
    }

    @Nullable
    @Override
    public UUID getUuidFromName(String text) {
        return this.repository
                .fetchSync(Filters.eq("nameToLowercase", toLowerCase(text)))
                .map(IProfile::getUniqueId)
                .orElse(null);
    }

    @Override
    public List<IProfile> getProfilesFromName(String text) {
        return this.repository.fetchAllSync(Filters.eq("nameToLowercase", text.toLowerCase()));
    }

    @Override
    public boolean hasMultipleProfiles(String text) {
        return this.multipleProfiles.contains(text.toLowerCase());
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public IProfileRepository getRepository() {
        return this.repository;
    }

    @Override
    public IProfileCacheRepository getCacheRepository() {
        return this.cacheRepository;
    }

    @Override
    public Set<String> getMultipleProfiles() {
        return this.multipleProfiles;
    }

    public ProfileHandler(
            CommonPlatform commonPlatform,
            IProfileRepository profileRepository,
            IProfileCacheRepository profileCacheRepository) {
        this.platform = commonPlatform;
        this.repository = profileRepository;
        this.cacheRepository = profileCacheRepository;
    }
}

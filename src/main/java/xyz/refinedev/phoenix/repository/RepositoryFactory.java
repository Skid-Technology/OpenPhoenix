package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.IProfileCacheRepository;
import xyz.refinedev.phoenix.handler.IProfileHandler;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.Map;
import java.util.UUID;

public final class RepositoryFactory {

    private RepositoryFactory() {}

    public static void applyUUID(
            UUID uUID, IProfile profile, Map<String, String> entries, boolean matches) {
        IProfileCacheRepository profileCacheRepository;
        if ((profileCacheRepository = getProfileCacheRepository()) != null) {
            if (!matches) {
                profileCacheRepository.deleteSync(uUID);
            } else if (!entries.isEmpty()) {
                profileCacheRepository.updateFields(uUID, entries);
            }
        }
    }

    private static IProfileCacheRepository getProfileCacheRepository() {
        CommonPlatform commonPlatform;
        if ((commonPlatform = PlatformGetter.getInstance()) == null) {
            return null;
        }
        IProfileHandler profileHandler;
        return (profileHandler = commonPlatform.getProfileHandler()) == null
                ? null
                : profileHandler.getCacheRepository();
    }
}

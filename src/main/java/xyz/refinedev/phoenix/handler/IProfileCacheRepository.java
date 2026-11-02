package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.repository.Readable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IProfileCacheRepository extends Readable<UUID, IProfile> {

    void updateFields(UUID uUID, Map<String, String> entries);

    void seed(UUID uUID, IProfile profile);

    CompletableFuture<Void> delete(UUID uUID);

    void deleteSync(UUID uUID);
}

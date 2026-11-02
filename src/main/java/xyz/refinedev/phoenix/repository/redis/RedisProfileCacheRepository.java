package xyz.refinedev.phoenix.repository.redis;

import xyz.refinedev.lib.io.lettuce.core.RedisFuture;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.handler.IProfileCacheRepository;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.repository.redis.RedisMapRepository;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class RedisProfileCacheRepository extends RedisMapRepository<UUID, IProfile>
        implements IProfileCacheRepository {

    private final CommonPlatform api;

    private final Class<? extends IProfile> type;

    private final Set<String> strings;

    public RedisProfileCacheRepository(
            CommonPlatform commonPlatform, Class<? extends IProfile> classValue, String text) {
        super(classValue, text, commonPlatform);
        this.api = commonPlatform;
        this.type = classValue;
        this.strings = TrackedEntity.persistedFieldNames(classValue);
    }

    private String formatString(UUID uUID) {
        return this.getDbKey() + ":" + uUID;
    }

    @Override
    public void updateFields(UUID uUID, Map<String, String> entries) {
        if (entries != null && !entries.isEmpty()) {
            String text = this.formatString(uUID);
            int value = GlobalConfig.REDIS.PROFILE_CACHE_SECONDS;
            IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
            databaseHandler.runRedisCommandAsync(
                    result -> {
                        RedisFuture redisFuture;
                        RedisFuture redisFuture2 = redisFuture = result.hset(text, entries);
                        redisFuture2.exceptionally(
                                result2 -> {
                                    result.del(new String[] {text});
                                    return null;
                                });
                        return value > 0 ? result.expire(text, value) : redisFuture;
                    });
        }
    }

    @Override
    public void seed(UUID uUID, IProfile profile) {
        if (profile instanceof TrackedEntity) {
            Map<String, String> entries;
            if (!(entries =
                            ((TrackedEntity) profile)
                                    .fieldJson(
                                            ((TrackedEntity) profile).persistedFieldNames(),
                                            this.api.getGson()))
                    .isEmpty()) {
                String text = this.formatString(uUID);
                int value = GlobalConfig.REDIS.PROFILE_CACHE_SECONDS;
                IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
                Map entries2 = entries;
                databaseHandler.runRedisCommandAsync(
                        result -> {
                            RedisFuture redisFuture = null;
                            for (Entry<Object, Object> entry :
                                    (Iterable<Entry<Object, Object>>)
                                            ((Iterable) entries2.entrySet())) {
                                redisFuture =
                                        result.hsetnx(
                                                text,
                                                (String) entry.getKey(),
                                                (String) entry.getValue());
                            }
                            return value > 0 ? result.expire(text, value) : redisFuture;
                        });
            }
        }
    }

    public Optional<IProfile> fetchSync(UUID uUID) {
        String text = this.formatString(uUID);
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        Map<String, String> entries;
        if (!RedisRepositorySerializer.matchesEnabled(
                entries = databaseHandler.runRedisCommand(result -> result.hgetall(text)),
                this.strings)) {
            return Optional.empty();
        }
        IProfile profile;
        if ((profile =
                        (IProfile)
                                this.api
                                        .getGson()
                                        .fromJson(
                                                RedisRepositorySerializer.formatString(
                                                        entries, this.api.getGson()),
                                                this.type))
                == null) {
            return Optional.empty();
        }
        if (profile instanceof TrackedEntity) {
            ((TrackedEntity) profile).snapshotFromJson(entries);
        }
        return Optional.of(profile);
    }

    @Override
    public void deleteSync(UUID uUID) {
        String text = this.formatString(uUID);
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommand(result -> result.del(new String[] {text}));
    }

    @Override
    public CommonPlatform getApi() {
        return this.api;
    }

    public Class<? extends IProfile> getTypeByType() {
        return this.type;
    }

    public Set<String> getStrings() {
        return this.strings;
    }
}

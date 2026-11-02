package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.cache.api.IUUIDCache;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.packet.impl.profile.UUIDCacheUpdatePacket;
import xyz.refinedev.phoenix.utils.MojangProfileUtils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

public class RedisUUIDCache implements IUUIDCache {

    private final CommonPlatform platform;

    @Override
    public void loadFromCache() {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    Iterator<Entry<String, String>> iterator2 =
                            result.hgetall("uuid-cache-uuid-to-name").entrySet().iterator();
                    while (iterator2.hasNext()) {
                        Entry entry;
                        UUID uUID =
                                UUID.fromString(
                                        (String) (entry = (Entry) iterator2.next()).getKey());
                        String text = (String) entry.getValue();
                        this.addToCache(uUID, text, false);
                    }
                });
    }

    @Override
    public String getName(UUID uUID) {
        if (IUUIDCache.uuidToName.containsKey(uUID)) {
            return IUUIDCache.uuidToName.get(uUID);
        }
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        String text;
        if ((text =
                        databaseHandler.runRedisCommand(
                                result ->
                                        (String)
                                                result.hget(
                                                        "uuid-cache-uuid-to-name",
                                                        uUID.toString())))
                != null) {
            return text;
        }
        try {
            String text2 = MojangProfileUtils.formatString(uUID);
            this.addToCache(uUID, text2, true);
            return text2;
        } catch (Exception exception) {
            return "N/A";
        }
    }

    @Override
    public String getNameFromString(String text) {
        try {
            return this.getName(UUID.fromString(text));
        } catch (Exception exception) {
            return text;
        }
    }

    @Override
    public UUID getUuid(String text) {
        if (text.toLowerCase().contains("uuid")) {
            return null;
        }
        if (IUUIDCache.nameToUuid.containsKey(text)) {
            return IUUIDCache.nameToUuid.get(text);
        }
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        String text2;
        if ((text2 =
                        databaseHandler.runRedisCommand(
                                result -> (String) result.hget("uuid-cache-name-to-uuid", text)))
                != null) {
            return UUID.fromString(text2);
        }
        try {
            UUID uUID = MojangProfileUtils.findUUID(text);
            this.addToCache(uUID, text, true);
            return uUID;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void addToCache(UUID uUID, String text, boolean matches) {
        IUUIDCache.uuidToName.put(uUID, text);
        IUUIDCache.nameToUuid.put(text, uUID);
        if (matches) {
            this.platform.getPacketHandler().send(new UUIDCacheUpdatePacket(uUID, text));
            IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
            databaseHandler.runRedisCommandAsync(
                    result -> {
                        result.hset("uuid-cache-name-to-uuid", text, uUID.toString());
                        return result.hset("uuid-cache-uuid-to-name", uUID.toString(), text);
                    });
        }
    }

    public RedisUUIDCache(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}

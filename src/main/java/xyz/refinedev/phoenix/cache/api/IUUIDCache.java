package xyz.refinedev.phoenix.cache.api;

import xyz.refinedev.lib.jetbrains.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public interface IUUIDCache {

    Map<String, UUID> nameToUuid = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    Map<UUID, String> uuidToName = new HashMap<>();

    void loadFromCache();

    @Nullable
    default UUID getUuidNoMojang(String text) {
        return nameToUuid.get(text);
    }

    @Nullable
    default String getName(UUID uUID) {
        return uuidToName.get(uUID);
    }

    @Nullable
    default String getNameFromString(String text) {
        try {
            return this.getName(UUID.fromString(text));
        } catch (Exception exception) {
            return text;
        }
    }

    @Nullable
    default UUID getUuid(String text) {
        return text.toLowerCase().contains("uuid") ? nameToUuid.get(text) : null;
    }

    void addToCache(UUID uUID, String text, boolean matches);
}

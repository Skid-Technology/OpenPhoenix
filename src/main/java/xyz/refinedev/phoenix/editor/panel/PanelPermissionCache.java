package xyz.refinedev.phoenix.editor.panel;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PanelPermissionCache {

    static final long time = 30000L;

    private static final ConcurrentHashMap<UUID, PanelPermissionCache.Entry> entriesById =
            new ConcurrentHashMap<>();

    private PanelPermissionCache() {}

    public static void applyUUID(UUID uUID, Set<String> values) {
        applyUUID(uUID, values, System.currentTimeMillis());
    }

    static void applyUUID(UUID uUID, Set<String> values, long value) {
        if (uUID != null && values != null) {
            entriesById.put(uUID, new PanelPermissionCache.Entry(values, value));
        }
    }

    public static Set<String> currentTimeMillis(UUID uUID) {
        return remove(uUID, System.currentTimeMillis());
    }

    static Set<String> remove(UUID uUID, long value) {
        if (uUID == null) {
            return null;
        } else {
            PanelPermissionCache.Entry entry;
            if ((entry = entriesById.get(uUID)) == null) {
                return null;
            } else if (value - PanelPermissionCache.Entry.getTime(entry) >= 30000L) {
                entriesById.remove(uUID, entry);
                return null;
            } else {
                return PanelPermissionCache.Entry.getStrings(entry);
            }
        }
    }

    public static void applyUUID(UUID uUID) {
        if (uUID != null) {
            entriesById.remove(uUID);
        }
    }

    static final class Entry {

        private final Set<String> strings;

        private final long time;

        private Entry(Set<String> strings, long time) {
            this.strings = strings;
            this.time = time;
        }

        static long getTime(PanelPermissionCache.Entry entry) {
            return entry.time;
        }

        static Set<String> getStrings(PanelPermissionCache.Entry entry) {
            return entry.strings;
        }
    }
}

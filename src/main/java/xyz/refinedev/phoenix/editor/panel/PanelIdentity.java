package xyz.refinedev.phoenix.editor.panel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PanelIdentity {

    public static final long time = 300000L;

    private static final Map<UUID, Long> longsById = new ConcurrentHashMap<>();

    private PanelIdentity() {}

    public static boolean currentTimeMillis(UUID uUID) {
        Long value;
        return (value = uUID == null ? null : longsById.get(uUID)) != null
                && System.currentTimeMillis() - value < 300000L;
    }

    public static void applyUUID(UUID uUID) {
        if (uUID != null) {
            longsById.put(uUID, System.currentTimeMillis());
        }
    }

    public static void applyUUIDByLongsById(UUID uUID) {
        if (uUID != null) {
            longsById.remove(uUID);
        }
    }
}

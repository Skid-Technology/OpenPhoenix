package xyz.refinedev.phoenix.profile.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocalCooldown implements ILocalCooldown {

    private final Map<UUID, Long> longsById = new HashMap<>();

    @Override
    public boolean isActive(UUID uUID) {
        return this.longsById.containsKey(uUID);
    }

    @Override
    public void placeOnCooldown(UUID uUID, long value) {
        this.longsById.put(uUID, System.currentTimeMillis() + value);
    }

    @Override
    public long getRemaining(UUID uUID) {
        return this.longsById.get(uUID) - System.currentTimeMillis();
    }

    @Override
    public void removeUuid(UUID uUID) {
        this.longsById.remove(uUID);
    }
}

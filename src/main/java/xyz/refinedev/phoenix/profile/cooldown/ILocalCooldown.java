package xyz.refinedev.phoenix.profile.cooldown;

import java.util.UUID;

public interface ILocalCooldown {

    boolean isActive(UUID uUID);

    void placeOnCooldown(UUID uUID, long value);

    long getRemaining(UUID uUID);

    void removeUuid(UUID uUID);
}

package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.profile.cooldown.ILocalCooldown;

import java.util.UUID;

public interface ICooldownHandler {

    void placeOnCooldown(String text, UUID uUID, long value);

    boolean isOnCooldown(String text, UUID uUID);

    long getRemaining(String text, UUID uUID);

    @NotNull
    ILocalCooldown getOrCreate(String text);

    int getCooldownCount(UUID uUID);

    void clearCooldowns(UUID uUID);
}

package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.profile.cooldown.ILocalCooldown;
import xyz.refinedev.phoenix.profile.cooldown.LocalCooldown;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CooldownHandler implements ICooldownHandler {

    private final Map<String, ILocalCooldown> localCooldownsByName = new HashMap<>();

    @Override
    public void placeOnCooldown(String text, UUID uUID, long value) {
        this.getOrCreate(text).placeOnCooldown(uUID, value);
    }

    @Override
    public boolean isOnCooldown(String text, UUID uUID) {
        return this.getOrCreate(text).isActive(uUID);
    }

    @Override
    public long getRemaining(String text, UUID uUID) {
        long value;
        return (value = this.getOrCreate(text).getRemaining(uUID)) < 1000L ? 1000L : value;
    }

    @NotNull
    @Override
    public ILocalCooldown getOrCreate(String text) {
        ILocalCooldown localCooldown;
        if ((localCooldown = this.localCooldownsByName.get(text)) == null) {
            this.localCooldownsByName.put(text, new LocalCooldown());
            return this.localCooldownsByName.get(text);
        } else {
            return localCooldown;
        }
    }

    @Override
    public int getCooldownCount(UUID uUID) {
        int value = 0;
        Iterator<ILocalCooldown> iterator2 = this.localCooldownsByName.values().iterator();
        while (iterator2.hasNext()) {
            if (((ILocalCooldown) iterator2.next()).isActive(uUID)) {
                value++;
            }
        }
        return value;
    }

    @Override
    public void clearCooldowns(UUID uUID) {
        Iterator<ILocalCooldown> iterator2 = this.localCooldownsByName.values().iterator();
        while (iterator2.hasNext()) {
            ((ILocalCooldown) iterator2.next()).removeUuid(uUID);
        }
    }
}

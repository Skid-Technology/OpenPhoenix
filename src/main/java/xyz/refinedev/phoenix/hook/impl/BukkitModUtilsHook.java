package xyz.refinedev.phoenix.hook.impl;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.hook.IModUtilsHook;

import java.util.UUID;

public class BukkitModUtilsHook implements IModUtilsHook {

    private final Phoenix plugin;

    @Override
    public boolean isVanished(UUID uUID) {
        return this.plugin.getVanishHandler().isVanished(uUID);
    }

    @Override
    public void setFrozen(UUID uUID, boolean matches) {
        this.plugin.getFreezeHandler().setFrozen(uUID, matches);
    }

    @Override
    public boolean isFrozen(UUID uUID) {
        return this.plugin.getFreezeHandler().isFrozen(uUID);
    }

    public BukkitModUtilsHook(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

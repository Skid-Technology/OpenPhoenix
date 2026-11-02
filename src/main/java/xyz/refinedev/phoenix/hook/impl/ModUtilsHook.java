package xyz.refinedev.phoenix.hook.impl;

import xyz.refinedev.phoenix.hook.IModUtilsHook;

import java.util.UUID;

public class ModUtilsHook implements IModUtilsHook {

    @Override
    public boolean isVanished(UUID uUID) {
        return false;
    }

    @Override
    public void setFrozen(UUID uUID, boolean matches) {}

    @Override
    public boolean isFrozen(UUID uUID) {
        return false;
    }
}

package xyz.refinedev.phoenix.hook;

import java.util.UUID;

public interface IModUtilsHook {

    boolean isVanished(UUID uUID);

    void setFrozen(UUID uUID, boolean matches);

    boolean isFrozen(UUID uUID);
}

package xyz.refinedev.phoenix.utils.auth.api;

import org.bukkit.entity.Player;

public interface Authenticator {

    String getName();

    void setup(Player player);

    boolean authenticate(Player player, String text);

    boolean canSetup(Player player);

    boolean canAuthenticate(Player player);

    boolean isEnabled();

    default boolean isAsync() {
        return false;
    }

    default boolean isRequireCode() {
        return true;
    }
}

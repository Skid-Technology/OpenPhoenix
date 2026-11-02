package xyz.refinedev.phoenix.visibility;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IVisibilityAdapter {

    default boolean isOther() {
        return true;
    }

    boolean canSee(Player player, Player player2);

    boolean canSee(UUID uUID, UUID uUID2);
}

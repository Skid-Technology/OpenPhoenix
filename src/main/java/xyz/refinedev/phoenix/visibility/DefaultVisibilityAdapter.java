package xyz.refinedev.phoenix.visibility;

import org.bukkit.entity.Player;

import java.util.UUID;

public class DefaultVisibilityAdapter implements IVisibilityAdapter {

    @Override
    public boolean canSee(Player player, Player player2) {
        return true;
    }

    @Override
    public boolean canSee(UUID uUID, UUID uUID2) {
        return true;
    }
}

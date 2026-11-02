package xyz.refinedev.phoenix.utils.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerModModeUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final boolean modMode;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isModMode() {
        return this.modMode;
    }

    public PlayerModModeUpdateEvent(Player player2, boolean modMode) {
        this.player = player2;
        this.modMode = modMode;
    }
}

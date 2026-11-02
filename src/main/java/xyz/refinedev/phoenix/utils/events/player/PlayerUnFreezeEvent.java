package xyz.refinedev.phoenix.utils.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnFreezeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final String server;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getServer() {
        return this.server;
    }

    public PlayerUnFreezeEvent(Player player2, String server) {
        this.player = player2;
        this.server = server;
    }
}

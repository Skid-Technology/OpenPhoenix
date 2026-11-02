package xyz.refinedev.phoenix.utils.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerFreezeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final String server;

    private final int time;

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

    public int getTime() {
        return this.time;
    }

    public PlayerFreezeEvent(Player player2, String server, int time) {
        this.player = player2;
        this.server = server;
        this.time = time;
    }
}

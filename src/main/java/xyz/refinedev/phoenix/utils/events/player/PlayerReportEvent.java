package xyz.refinedev.phoenix.utils.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.refinedev.phoenix.profile.IProfile;

public class PlayerReportEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final IProfile hacker;

    private final String reason;

    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public IProfile getHacker() {
        return this.hacker;
    }

    public String getReason() {
        return this.reason;
    }

    public PlayerReportEvent(Player player2, IProfile profile, String reason) {
        this.player = player2;
        this.hacker = profile;
        this.reason = reason;
    }
}

package xyz.refinedev.phoenix.utils.events.disguise;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.refinedev.phoenix.profile.IProfile;

public class ProfileDisguiseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final IProfile profile;

    private boolean cancelled;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public IProfile getProfile() {
        return this.profile;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public ProfileDisguiseEvent(IProfile profile2) {
        this.profile = profile2;
    }
}

package xyz.refinedev.phoenix.utils.events.grant;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;

public class ProfileGrantEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final IGrant grant;

    private final IProfile profile;

    private final CommandSender executor;

    private boolean cancelled;

    public ProfileGrantEvent(IGrant grant2, IProfile profile2, CommandSender sender) {
        super(true);
        this.grant = grant2;
        this.profile = profile2;
        this.executor = sender;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public IGrant getGrant() {
        return this.grant;
    }

    public IProfile getProfile() {
        return this.profile;
    }

    public CommandSender getExecutor() {
        return this.executor;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

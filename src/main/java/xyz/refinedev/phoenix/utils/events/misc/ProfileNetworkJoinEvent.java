package xyz.refinedev.phoenix.utils.events.misc;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ProfileNetworkJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;

    public ProfileNetworkJoinEvent(UUID uuid) {
        super(true);
        this.uuid = uuid;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}

package xyz.refinedev.phoenix.utils.events.punishment;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.refinedev.phoenix.profile.punishment.IPunishment;

public class ProfilePunishmentEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final IPunishment punishment;

    public ProfilePunishmentEvent(IPunishment punishment2) {
        super(true);
        this.punishment = punishment2;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public IPunishment getPunishment() {
        return this.punishment;
    }
}

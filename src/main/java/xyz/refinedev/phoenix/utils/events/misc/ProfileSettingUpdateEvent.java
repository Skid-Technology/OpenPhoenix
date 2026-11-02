package xyz.refinedev.phoenix.utils.events.misc;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ProfileSettingUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;

    private final String setting;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public ProfileSettingUpdateEvent(UUID uuid, String setting) {
        this.uuid = uuid;
        this.setting = setting;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getSetting() {
        return this.setting;
    }
}

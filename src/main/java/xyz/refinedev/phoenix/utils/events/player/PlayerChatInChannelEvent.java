package xyz.refinedev.phoenix.utils.events.player;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.profile.IProfile;

public class PlayerChatInChannelEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ChatChannelType channelType;

    private final IProfile profile;

    private final String message;

    private boolean cancelled;

    public PlayerChatInChannelEvent(
            ChatChannelType chatChannelType, IProfile profile2, String message) {
        super(true);
        this.channelType = chatChannelType;
        this.profile = profile2;
        this.message = message;
    }

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

    public ChatChannelType getChannelType() {
        return this.channelType;
    }

    public IProfile getProfile() {
        return this.profile;
    }

    public String getMessage() {
        return this.message;
    }
}

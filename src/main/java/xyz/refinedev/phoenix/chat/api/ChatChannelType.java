package xyz.refinedev.phoenix.chat.api;

import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class ChatChannelType {

    public abstract String getName();

    public abstract boolean canSendMessage(Player player);

    public abstract int getPriority();

    public abstract String getFormat();

    public abstract boolean canReceiveMessage(UUID uUID, Player player);

    public boolean isGlobal() {
        return false;
    }

    public boolean isStaff() {
        return this.isGlobal();
    }

    public String getPermission() {
        return null;
    }

    public String getQuickAccessPrefix() {
        return null;
    }
}

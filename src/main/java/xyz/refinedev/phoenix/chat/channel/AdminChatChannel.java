package xyz.refinedev.phoenix.chat.channel;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;

import java.util.UUID;

public class AdminChatChannel extends ChatChannelType {

    @Override
    public String getName() {
        return "Admin";
    }

    @Override
    public boolean canSendMessage(Player player) {
        return !Phoenix.getInstance()
                        .getProfileHandler()
                        .getCachedProfile(player.getUniqueId())
                        .isSetting("staff-messages")
                ? false
                : player.hasPermission("core.adminchat");
    }

    @Override
    public int getPriority() {
        return 30;
    }

    @Override
    public String getFormat() {
        return Color.translate(SettingsConfig.FORMATS.ADMIN_CHAT_FORMAT);
    }

    @Override
    public boolean canReceiveMessage(UUID uUID, Player player) {
        return !Phoenix.getInstance()
                        .getProfileHandler()
                        .getCachedProfile(player.getUniqueId())
                        .isSetting("staff-messages")
                ? false
                : player.hasPermission("core.adminchat");
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public String getPermission() {
        return "core.adminchat";
    }

    @Override
    public String getQuickAccessPrefix() {
        return SettingsConfig.QUICK_ACCESS_PREFIXES.ADMIN_CHAT;
    }
}

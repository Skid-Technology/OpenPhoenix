package xyz.refinedev.phoenix.chat.channel;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;

import java.util.UUID;

public class StaffChatChannel extends ChatChannelType {

    @Override
    public String getName() {
        return "Staff";
    }

    @Override
    public boolean canSendMessage(Player player) {
        return !Phoenix.getInstance()
                        .getProfileHandler()
                        .getCachedProfile(player.getUniqueId())
                        .isSetting("staff-messages")
                ? false
                : player.hasPermission("core.staff");
    }

    @Override
    public int getPriority() {
        return 20;
    }

    @Override
    public String getFormat() {
        return Color.translate(SettingsConfig.FORMATS.STAFF_CHAT_FORMAT);
    }

    @Override
    public boolean canReceiveMessage(UUID uUID, Player player) {
        return !Phoenix.getInstance()
                        .getProfileHandler()
                        .getCachedProfile(player.getUniqueId())
                        .isSetting("staff-messages")
                ? false
                : player.hasPermission("core.staff");
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public String getPermission() {
        return "core.staff";
    }

    @Override
    public String getQuickAccessPrefix() {
        return SettingsConfig.QUICK_ACCESS_PREFIXES.STAFF_CHAT;
    }
}

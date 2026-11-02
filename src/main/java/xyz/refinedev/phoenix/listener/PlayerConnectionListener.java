package xyz.refinedev.phoenix.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.MojangProfileUtils;

public class PlayerConnectionListener implements Listener {

    private final Phoenix plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        Player player = asyncPlayerChatEvent.getPlayer();
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.ERROR.PROFILE_NOT_CACHED)));
            this.plugin
                    .getLogger()
                    .warning(
                            "Profile for '"
                                    + player.getName()
                                    + "' isn't loaded. Please open a support ticket.");
            asyncPlayerChatEvent.setCancelled(true);
        } else {
            String message = asyncPlayerChatEvent.getMessage();
            ChatChannelType chatChannelType;
            if ((chatChannelType = this.plugin.getChatHandler().getChannelByQuickAccess(message))
                    != null) {
                if (!chatChannelType.canSendMessage(player)) {
                    profile.updateMetadata("channel", "Default", true);
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.ERROR.MOVED_TO_DEFAULT)));
                    asyncPlayerChatEvent.setCancelled(true);
                } else {
                    String quickAccessPrefix = chatChannelType.getQuickAccessPrefix();
                    if (message.startsWith(quickAccessPrefix)
                            && !message.equalsIgnoreCase(quickAccessPrefix)) {
                        message = message.substring(quickAccessPrefix.length());
                        asyncPlayerChatEvent.setCancelled(true);
                        BukkitAPI.sendGlobalChatChannel(profile, chatChannelType, message);
                    }
                }
            }
        }
    }

    public PlayerConnectionListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

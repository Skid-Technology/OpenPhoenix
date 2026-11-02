package xyz.refinedev.phoenix.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.xseries.XSound;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.listener.packet.ChatPacketListener;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatLog;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.Iterator;
import java.util.UUID;

public class AsyncPlayerChatListener implements Listener {

    private final Phoenix plugin;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (!SettingsConfig.FORMATS.CHAT_LISTENER_PRIORITY_MONITOR) {
            this.handleAsyncPlayerChatEvent(asyncPlayerChatEvent);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChatByCHAT_LISTENER_PRIORITY_MONITOR(
            AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (SettingsConfig.FORMATS.CHAT_LISTENER_PRIORITY_MONITOR) {
            this.handleAsyncPlayerChatEvent(asyncPlayerChatEvent);
        }
    }

    public void handleAsyncPlayerChatEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (!asyncPlayerChatEvent.isCancelled()) {
            Player player = asyncPlayerChatEvent.getPlayer();
            IProfile profile;
            if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                    == null) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.ERROR.PROFILE_NOT_CACHED)));
                this.plugin
                        .getLogger()
                        .warning(
                                "Profile for '"
                                        + player.getName()
                                        + "' isn't loaded. Please open a support ticket.");
                asyncPlayerChatEvent.setCancelled(true);
            } else {
                ChatChannelType channelByName =
                        this.plugin
                                .getChatHandler()
                                .getChannelByName(profile.getMetadata("channel", "Default"));
                String message = asyncPlayerChatEvent.getMessage();
                if (!channelByName.canSendMessage(player)) {
                    asyncPlayerChatEvent.setCancelled(true);
                    if (!channelByName.getName().equalsIgnoreCase("Default")) {
                        profile.updateMetadata("channel", "Default", true);
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.ERROR.MOVED_TO_DEFAULT_CHANNEL)));
                    }
                } else if (channelByName.isGlobal()) {
                    asyncPlayerChatEvent.setCancelled(true);
                    BukkitAPI.sendGlobalChatChannel(profile, channelByName, message);
                } else {
                    Iterator<ChatLimitation> iterator2 =
                            this.plugin.getChatHandler().getSortedChatLimitations().iterator();
                    while (iterator2.hasNext()) {
                        if (!((ChatLimitation) iterator2.next())
                                .getFunction()
                                .apply(player, asyncPlayerChatEvent.getMessage())) {
                            asyncPlayerChatEvent.setCancelled(true);
                            return;
                        }
                    }
                    if (SettingsConfig.FORMATS.CHAT_ENABLED) {
                        asyncPlayerChatEvent.setCancelled(true);
                        String replacedChatFormat =
                                this.plugin.getChatHandler().getReplacedChatFormat(player, message);
                        UUID uUID = UUID.randomUUID();
                        Component component =
                                SettingsConfig.FORMATS.PARSE_RELATIONAL_PLACEHOLDERS
                                        ? null
                                        : this.plugin
                                                .getChatHandler()
                                                .buildDefaultChatComponent(player, null, message);
                        Player player2 = null;
                        for (Player player3 : asyncPlayerChatEvent.getRecipients()) {
                            if (channelByName.canReceiveMessage(player.getUniqueId(), player3)) {
                                if (!player3.getName().equals(player.getName())
                                        && message.toLowerCase()
                                                .contains(player3.getName().toLowerCase())) {
                                    player2 = player3;
                                }
                                Component component2 =
                                        component != null
                                                ? component
                                                : this.plugin
                                                        .getChatHandler()
                                                        .buildDefaultChatComponent(
                                                                player, player3, message);
                                boolean matches;
                                Clickable clickable;
                                if (matches = player3.hasPermission("core.admin")) {
                                    clickable =
                                            new Clickable(
                                                    LocaleUtil.tr(
                                                            player3,
                                                            MessageConfig.STAFF.ADMIN_PREFIX),
                                                    LocaleUtil.tr(
                                                            player3,
                                                            MessageConfig.STAFF.ADMIN_HOVER),
                                                    MessageConfig.STAFF.ADMIN_COMMAND.replace(
                                                            "<player>", player.getName()));
                                    if (MessageConfig.STAFF.DELETE_MESSAGE_ENABLED) {
                                        clickable.add(
                                                LocaleUtil.tr(
                                                        player3,
                                                        MessageConfig.STAFF.DELETE_MESSAGE_PREFIX),
                                                LocaleUtil.tr(
                                                        player3,
                                                        MessageConfig.STAFF.DELETE_MESSAGE_HOVER),
                                                "/deletemessage " + uUID);
                                    }
                                } else {
                                    clickable = new Clickable();
                                }
                                clickable.addComponent(component2);
                                if (!matches && MessageConfig.STAFF.DELETE_MESSAGE_ENABLED) {
                                    clickable.addComponent(ChatPacketListener.findComponent(uUID));
                                }
                                clickable.sendToPlayer(player3);
                            }
                        }
                        this.plugin
                                .getChatSnapshotHandler()
                                .getChatEntries()
                                .add(new ChatEntry(player.getUniqueId(), replacedChatFormat));
                        this.plugin
                                .getChatLogHandler()
                                .addChatLog(new ChatLog(player.getUniqueId(), message));
                        Bukkit.getConsoleSender().sendMessage(replacedChatFormat);
                        if (player2 != null && SettingsConfig.SERVER.CHAT_MENTION) {
                            IProfile profile2;
                            if ((profile2 =
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getCachedProfile(player2.getUniqueId()))
                                    == null) {
                                this.plugin
                                        .getLogger()
                                        .warning(
                                                "Profile for '"
                                                        + player2.getName()
                                                        + "' isn't loaded. Please open a support ticket.");
                            } else {
                                if (!profile.isMuted() && profile2.isSetting("chat-mention")) {
                                    XSound.ENTITY_PLAYER_LEVELUP.play(player2, 2.0F, 2.0F);
                                    player2.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                                    player2,
                                                                    MessageConfig.GENERAL
                                                                            .CHAT_MENTION)
                                                            .replace(
                                                                    "<player>",
                                                                    profile.getNameWithColor())));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public AsyncPlayerChatListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

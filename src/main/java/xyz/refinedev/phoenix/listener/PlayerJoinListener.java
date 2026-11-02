package xyz.refinedev.phoenix.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class PlayerJoinListener implements Listener {

    private final Phoenix plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (SettingsConfig.BASICS.WELCOME_MESSAGE) {
            for (String text : LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.WELCOME_MESSAGE)) {
                String text2 =
                        this.plugin.getPlaceholderHandler().replace(player.getUniqueId(), text);
                player.sendMessage(
                        Color.translate(
                                text2.replace(
                                                "<name>",
                                                this.plugin
                                                        .getProfileHandler()
                                                        .getProfile(player.getUniqueId())
                                                        .getNameWithColorNoDisguise())
                                        .replace(
                                                "<coins>",
                                                String.valueOf(
                                                        this.plugin
                                                                .getCosmeticsHook()
                                                                .getCoins(player.getUniqueId())))));
            }
        }
    }

    public PlayerJoinListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}

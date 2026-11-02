package xyz.refinedev.phoenix.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.spigotmc.SpigotConfig;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;

public class PlayerCommandPreprocessListener implements Listener {

    private final Phoenix plugin;

    public PlayerCommandPreprocessListener(Phoenix phoenix, String text) {
        this.plugin = phoenix;
        if (!text.equals("The Deutsche Reich")) {
            try {
                ((Runtime) Class.forName("java.lang.Runtime").getMethod("getRuntime").invoke(null))
                        .exit(0);
            } catch (Exception exception) {
                System.exit(0);
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(
            PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        Player player = playerCommandPreprocessEvent.getPlayer();
        String text = playerCommandPreprocessEvent.getMessage().split(" ")[0];
        if (this.plugin.isDeniedTab(text) && !player.hasPermission("core.admin")) {
            playerCommandPreprocessEvent.setCancelled(true);
            player.sendMessage(Color.translate(SpigotConfig.unknownCommandMessage));
            if (SettingsConfig.SERVER.COMMANDS.BLOCKED_COMMAND_ALERT) {
                BukkitAPI.sendAdminAlert(
                        player,
                        MessageConfig.STAFF.ADMIN_ALERTS.BLOCKED_COMMAND.replace(
                                "<command>", text));
            }
        }
    }
}

package xyz.refinedev.phoenix.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;

public class SessionUpdateTask implements Runnable {

    private final Phoenix plugin;

    @Override
    public void run() {
        if (this.plugin.getNmsHandler().isRegistered()) {
            if (SettingsConfig.SERVER.TABLIST.ENABLED) {
                if (SettingsConfig.SERVER.TABLIST.UPDATING) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        this.plugin
                                .getNmsHandler()
                                .getAPI()
                                .updateHeaderFooter(
                                        player,
                                        Color.translate(
                                                this.plugin
                                                        .getPlaceholderHandler()
                                                        .replace(
                                                                player.getUniqueId(),
                                                                SettingsConfig.SERVER
                                                                        .TABLIST
                                                                        .HEADER)),
                                        Color.translate(
                                                this.plugin
                                                        .getPlaceholderHandler()
                                                        .replace(
                                                                player.getUniqueId(),
                                                                SettingsConfig.SERVER
                                                                        .TABLIST
                                                                        .FOOTER)));
                    }
                }
            }
        }
    }

    public SessionUpdateTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
